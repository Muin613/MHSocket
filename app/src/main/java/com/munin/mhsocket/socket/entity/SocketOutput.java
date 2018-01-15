package com.munin.mhsocket.socket.entity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by munin on 2017/12/9.
 */

public class SocketOutput {
    //    输出
    OutputStream output;
    SocketClient client;
    private Thread writerThread;
    private volatile boolean done = false;
    private final BlockingQueue<byte[]> queue = new ArrayBlockingQueue<byte[]>(500, true);

    public SocketOutput(SocketClient client, OutputStream output) {
        this.client = client;
        this.output = output;
        done = false;
    }

    public void sendData(byte[] packet) {
        if (this.done) {
            return;
        }

        try {
            queue.put(packet);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        synchronized (queue) {
            queue.notifyAll();
        }
    }


    public synchronized void startup() {
        done = false;
        if (writerThread != null && writerThread.isAlive()) {
            return;
        }
        writerThread = new Thread() {
            public void run() {
                writePackets(this);
            }
        };
        writerThread.start();
    }


    private void shutdown() {
        if (this.done) {
            return;
        }
        this.done = true;

        synchronized (queue) {
            queue.notifyAll();
        }

        if (writerThread != null) {
            writerThread.interrupt();
            writerThread = null;
        }
    }

    private byte[] nextPacket() {
        byte[] packet = null;
        while (!this.done && (packet = queue.poll()) == null) {
            try {
                synchronized (queue) {
                    queue.wait();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return packet;
    }

    private void writePackets(Thread thisThread) {
        while (!this.done && this.writerThread == thisThread) {
            byte[] packet = nextPacket();
            if (packet != null && !done && this.writerThread == thisThread) {
                try {
                    output.write(packet);
                    output.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                    close();
                    if (null != client)
                        client.destroy();
                }
            }
        }
    }

    public void clearQueue() {
        if (queue != null) {
            queue.clear();
        }
    }


    public OutputStream getOutput() {
        return output;
    }

    public void setOutput(OutputStream output) {
        this.output = output;
    }

    public void close() {
        shutdown();
        try {
            if (null != output) {
                output.close();
                output = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        clearQueue();
    }


}

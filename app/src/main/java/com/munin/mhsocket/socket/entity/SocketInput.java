package com.munin.mhsocket.socket.entity;

import com.munin.mhsocket.socket.interfaces.base.ISocketController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Created by munin on 2017/12/9.
 */

public class SocketInput {
    //    输入
    InputStream input;
    SocketClient client;
    private volatile boolean done = false;
    private Thread readerThread;
    private ISocketController listener;
    Socket socket;

    public SocketInput(SocketClient client, InputStream input, Socket socket) {
        this.client = client;
        this.input = input;
        done = false;
        this.socket = socket;
    }

    public SocketInput bindListener(ISocketController controller) {
        listener = controller;
        return this;
    }

    public InputStream getInput() {
        return input;
    }

    public void setInput(InputStream input) {
        this.input = input;
    }

    public void close() {
        shutdown();
        try {
            if (input != null) {
                input.close();
                input = null;
                listener = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void startup() {
        if (!this.done && readerThread != null) {
            return;
        }
        done = false;
        if (readerThread != null && readerThread.isAlive()) {
            return;
        }
        readerThread = new Thread() {
            public void run() {
                parsePackets(this);
            }
        };
        readerThread.start();
    }


    private void shutdown() {
        if (this.done) {
            return;
        }
        done = true;

        if (readerThread != null) {
            readerThread.interrupt();
            readerThread = null;
        }
    }


    private void parsePackets(Thread thisThread) {
        byte[] buffer = new byte[1024 * 20];
        int length = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            while (!this.done && this.readerThread == thisThread && client.isConnect() && ((length = input.read(buffer)) != -1) && socket.isConnected()) {
                if (length > 0) {
                    outputStream.write(buffer, 0, length);
                    byte[] result = outputStream.toByteArray();
                    if (null != listener)
                        listener.receiveByteData(result);
                    outputStream.reset();
                }
            }
        } catch (Exception e) {
            try {
                if (null != outputStream) {
                    outputStream.close();
                    outputStream = null;
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            close();
            client.destroy();
        }
    }


}

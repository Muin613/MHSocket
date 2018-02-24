package com.munin.mhsocket.socket.entity;

import com.munin.mhsocket.socket.interfaces.base.ISocketController;
import com.munin.mhsocket.socket.log.Debug;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

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
    BufferedSource bufferedSource;
    Source source;

    public SocketInput(SocketClient client, InputStream input, Socket socket) {
        this.client = client;
        this.input = input;
        done = false;
        this.socket = socket;
        source = Okio.source(this.input);
        bufferedSource = Okio.buffer(source);
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
            if (bufferedSource != null) {
                bufferedSource.close();
                bufferedSource = null;
            }
            if (source != null) {
                source.close();
                source = null;
            }
            if (input != null) {
                input.close();
                input = null;
            }
            listener = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void startup() {
        Debug.E("socket", "连接    1 ");
        if (!this.done && readerThread != null) {
            return;
        }
        done = false;
        Debug.E("socket", "连接     2");
        if (readerThread != null && readerThread.isAlive()) {
            return;
        }
        Debug.E("socket", "连接     3");
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
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            while (!this.done && this.readerThread == thisThread && socket.isConnected()) {
                try {
                    int length = bufferedSource.read(buffer);
                    if (length > 0) {
                        outputStream.write(buffer, 0, length);
                        byte[] result = outputStream.toByteArray();
                        outputStream.reset();
                        if (null != listener)
                            listener.receiveByteData(result);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

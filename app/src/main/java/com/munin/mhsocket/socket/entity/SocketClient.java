package com.munin.mhsocket.socket.entity;

import com.munin.mhsocket.socket.interfaces.base.ISocketState;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by munin on 2017/12/9.
 */

public class SocketClient {
    Socket socket;
    SocketConfig config;
    SocketInput input;
    SocketOutput output;
    ISocketState listener;

    public SocketClient(SocketConfig config, ISocketState listener) {
        this.config = config;
        this.listener = listener;
    }

    public void createClient() {
        try {
            listener.createState();
            socket = new Socket(config.getHost(), config.getPort());
            listener.connectingState();
            if (socket.isConnected()) {
                socket.setReceiveBufferSize(1024 * 20);
                socket.setTcpNoDelay(true);
                input = new SocketInput(this, socket.getInputStream(),socket);
                output = new SocketOutput(this, socket.getOutputStream());
                listener.connectedState();
            } else {
                listener.disconnectState();
            }
        } catch (Exception e) {
            e.printStackTrace();
            listener.createFailState();
        }
    }

    public void destroy() {
        closeIO();
        if (null != socket && !socket.isClosed()) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;

            }
        } else if (null != socket) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;
            }
        }
        listener.cancelConnectState();
    }

    public SocketInput getInput() {
        return input;
    }

    public void setInput(SocketInput input) {
        this.input = input;
    }

    public SocketOutput getOutput() {
        return output;
    }

    public void setOutput(SocketOutput output) {
        this.output = output;
    }

    public ISocketState getListener() {
        return listener;
    }

    public void setListener(ISocketState listener) {
        this.listener = listener;
    }

    public boolean isConnect() {
        if (socket == null)
            return false;
        return socket.isConnected();
    }

    private void closeIO() {
        if (null != input) {
            input.close();
            input = null;
        }
        if (null != output) {
            output.close();
            output = null;
        }
    }


    public static class Builder {

        SocketConfig config;
        ISocketState listener;

        public Builder(SocketConfig config, ISocketState listener) {
            this.config = config;
            this.listener = listener;
        }

        public Builder(SocketConfig config) {
            this.config = config;
        }

        public Builder bindStateListener(ISocketState listener) {
            this.listener = listener;
            return this;
        }

        public SocketClient build() {
            if (this.listener == null)
                throw new NullPointerException("socket config error ,please init listener");
            return new SocketClient(this.config, this.listener);
        }
    }
}

package com.munin.mhsocket.socket.entity;

/**
 * Created by munin on 2017/12/9.
 */

public class SocketConfig {
    private String host = "";//指定ip地址
    private int port = -1;//指定端口号

    public SocketConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "SocketConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}

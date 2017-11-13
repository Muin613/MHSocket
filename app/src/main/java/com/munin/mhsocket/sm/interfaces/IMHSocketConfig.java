package com.munin.mhsocket.sm.interfaces;

/**
 * Created by Administrator on 2017/11/13.
 */

public class IMHSocketConfig {
    private String host = "";//指定ip地址
    private int port = 0;//指定端口号
    private boolean needConnected = true;//是否需要重连
    private int checkConnectTime = 200;
    private IMHSocketListener listener;

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

    public boolean isNeedConnected() {
        return needConnected;
    }

    public void setNeedConnected(boolean needConnected) {
        this.needConnected = needConnected;
    }

    public IMHSocketListener getListener() {
        return listener;
    }

    public void setListener(IMHSocketListener listener) {
        this.listener = listener;
    }


    public int getCheckConnectTime() {
        return checkConnectTime;
    }

    public void setCheckConnectTime(int checkConnectTime) {
        this.checkConnectTime = checkConnectTime;
    }


    @Override
    public String toString() {
        return "IMHSocketConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", needConnected=" + needConnected +
                ", checkConnectTime=" + checkConnectTime +
                ", listener=" + listener +
                '}';
    }



    public IMHSocketConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }
}

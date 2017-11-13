package com.munin.mhsocket.sm.interfaces;

/**
 * Created by Administrator on 2017/11/12.
 */

public interface IMHSocketListener {

    //    连接
    void connect();

    // 连接中
    void connecting();

    //连接了
    void connected();

    //失去连接
    void disconnect();

    // 检查连接
    void checkConnected(boolean isConnect);

    //接收数据
    void receive(byte[] data);

    //异常
    void onException(Exception error);

    //    重连
    void reconnect();
}

package com.munin.mhsocket.socket.interfaces.base;

/**
 * Created by munin on 2017/12/9.
 */

public interface ISocketState {

    //创建
    void createState();

    //   创建失败
    void createFailState();

    // 连接中
    void connectingState();

    //已连接
    void connectedState();

    //失去连接
    void disconnectState();

    //取消连接
    void cancelConnectState();


}

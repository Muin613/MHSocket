package com.munin.mhsocket.socket.interfaces.base;

/**
 * Created by munin on 2017/12/9.
 */

public interface ISocketController {
    //开启
    void startSocket();

    //停止
    void stopSocket();

    //发送数据
    void sendByteMsg(byte[] data);

    //接收数据
    void receiveByteData(byte[] data);

}

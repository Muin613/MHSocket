package com.munin.mhsocket.socket.interfaces.base;

/**
 * Created by munin on 2017/12/9.
 */

public interface ISocketController {
    //开启
    ISocketController startSocket();

    //停止
    ISocketController stopSocket();

    //发送数据
    ISocketController sendByteMsg(byte[] data);

    //接收数据
    ISocketController receiveByteData(byte[] data);

}

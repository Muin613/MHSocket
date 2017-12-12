package com.munin.mhsocket.socket.interfaces;

/**
 * Created by munin on 2017/12/12.
 */

public interface ISocketListener {

    void onSocketState(int socketState,String description);

    //发送数据
    void sendData(byte[] data);

    //接收数据
    void receiveData(byte[] data);
}

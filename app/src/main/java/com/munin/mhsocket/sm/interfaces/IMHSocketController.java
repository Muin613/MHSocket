package com.munin.mhsocket.sm.interfaces;

/**
 * Created by Administrator on 2017/11/13.
 */

public interface IMHSocketController {
    //开启
    void startSocket();

    //停止
    void stopSocket();

    //发送数据
    void sendByteMsg(byte[] data);

    void onException(Exception error);

    //接收数据
    void receiveByteData(byte[] data);

    // 发送定时数据
    void sendScheduleTimeData(IMHTimingData data);

    //释放所有定时数据
    void releaseScheduleTimeData();

    //释放某个tag的定时数据
    void releaseScheduleTimeDataByTag(String tag);

    //检测网络
    void checkSocketConnect(boolean isConnected);
}

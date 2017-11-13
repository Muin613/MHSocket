package com.munin.mhsocket.sm.interfaces;

import android.os.Handler;

/**
 * Created by Administrator on 2017/11/13.
 */

public interface IMHTimingData extends Runnable{

    //毫秒级别 定时发送
    IMHTimingData setScheduleTime(int millsTime);

    //handler(定时handler)
    IMHTimingData setHandler(Handler postHandler);

    //发送的数据
    IMHTimingData setByteData(byte[] data);

    //标志（确定发送哪一个）
    IMHTimingData setTag(String tag);

    //controller(负责发送功能)
    IMHTimingData setSocketController(IMHSocketController controller);

    //释放资源
    void release();

    //获取定时时间
    int getScheduleTime();

    //获取发送数据
    byte[] getByteData();

    //获取指定标志
    String getTag();

    //获取操控定时的handler
    Handler getHandler();

    //获取操控数据的controller
    IMHSocketController getSocketController();


}

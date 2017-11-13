package com.munin.mhsocket.sm;

import android.os.Handler;

import com.munin.mhsocket.sm.interfaces.IMHSocketController;
import com.munin.mhsocket.sm.interfaces.IMHTimingData;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/11/1.
 * <p>
 * 定时发送的特殊数据
 */

public class MHTimingData implements Serializable, IMHTimingData {


    private int time = -1;//毫秒级别 定时发送
    private byte[] data;//发送的数据
    private String tag;//标志（确定发送哪一个）
    private Handler handler;//handler(定时handler)
    private IMHSocketController manager;//manager(负责发送功能)


    @Override
    public void run() {
        if (null != handler && null != manager) {
            manager.sendByteMsg(data);
            if (time > -1)
                handler.postDelayed(this, time);
        }

    }

    @Override
    public IMHTimingData setScheduleTime(int millsTime) {
        time = millsTime;
        return this;
    }

    @Override
    public IMHTimingData setHandler(Handler postHandler) {
        handler = postHandler;
        return this;
    }

    @Override
    public IMHTimingData setByteData(byte[] data) {
        this.data = data;
        return this;
    }

    @Override
    public IMHTimingData setTag(String tag) {
        this.tag = tag;
        return this;
    }

    @Override
    public IMHTimingData setSocketController(IMHSocketController controller) {
        manager = controller;
        return this;
    }

    @Override
    public void release() {
        handler.removeCallbacks(this);
        handler = null;
        manager = null;

    }


    @Override
    public int getScheduleTime() {
        return time;
    }

    @Override
    public byte[] getByteData() {
        return data;
    }

    @Override
    public String getTag() {
        return tag;
    }

    @Override
    public Handler getHandler() {
        return handler;
    }

    @Override
    public IMHSocketController getSocketController() {
        return manager;
    }



}

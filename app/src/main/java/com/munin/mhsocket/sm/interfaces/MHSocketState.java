package com.munin.mhsocket.sm.interfaces;

/**
 * Created by Administrator on 2017/11/13.
 */

public class MHSocketState {
    //    连接中
    public static final String STATE_CONNECTING = "CONNECTING";
    //    失去连接（手动取消连接）
    public static final String STATE_DISCONNECTED = "DISCONNECT";
    //    重连
    public static final String STATE_RECONNECT = "RECONNECT";
    //    已连接
    public static final String STATE_CONNECTED = "CONNECTED";
    //    默认
    public static final String STATE_DEFAULT = "DEFAULT";

}

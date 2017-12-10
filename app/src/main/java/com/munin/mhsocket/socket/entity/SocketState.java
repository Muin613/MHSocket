package com.munin.mhsocket.socket.entity;

/**
 * Created by munin on 2017/12/9.
 */

public class SocketState {
    //    默认
    public static final int STATE_DEFAULT = -1;
    //    连接中
    public static final int STATE_CONNECTING = 0;
    //    失去连接（手动取消连接）
    public static final int STATE_DISCONNECTED = 1;
    //    重连
    public static final int STATE_RECONNECT = 1 << 1;
    //    已连接
    public static final int STATE_CONNECTED = 1 << 2;
    //    停止连接
    public static final int STATE_STOP = 1 << 3;
    //   有异常
    public static final int STATE_ERROR = 1 << 4;
    //    创建失败
    public static final int STATE_CREATE_FAIL = 1 << 5;

}

package com.munin.mhsocket.socket.interfaces;

import com.munin.mhsocket.socket.interfaces.base.ISocketState;

/**
 * Created by munin on 2017/12/9.
 */

public interface ISocketStateListener extends ISocketState {
    //    重连
    void reconnectState();

}

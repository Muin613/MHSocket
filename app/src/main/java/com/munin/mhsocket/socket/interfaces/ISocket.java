package com.munin.mhsocket.socket.interfaces;

import com.munin.mhsocket.socket.interfaces.base.ISocketController;

/**
 * Created by munin on 2017/12/9.
 */

public interface ISocket extends ISocketController {

    //    重连
    ISocket reconnect();

}

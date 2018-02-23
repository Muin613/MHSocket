package com.munin.mhsocket.socket;

import com.munin.mhsocket.socket.entity.SocketClient;
import com.munin.mhsocket.socket.entity.SocketConfig;
import com.munin.mhsocket.socket.entity.SocketOutput;
import com.munin.mhsocket.socket.interfaces.ISocket;
import com.munin.mhsocket.socket.interfaces.ISocketListener;
import com.munin.mhsocket.socket.interfaces.ISocketStateListener;
import com.munin.mhsocket.socket.log.Debug;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by munin on 2017/12/9.
 */

public class SocketManager implements ISocket, ISocketStateListener {
    SocketClient client;
    private static SocketManager manager;
    private String host = "";
    private int port = -1;
    private String defaultHost = "192.169.1.1";
    private int defaultPort = 8080;
    private SocketOutput output;
    private Timer timer;
    private Timer checkTimer;
    private Object lock;
    private Object checkLock;
    private byte[] heartData = null;
    private int heartTime = 1000;
    private ISocketListener listener;
    private int heartTimeRatio = 1;

    private SocketManager() {

    }

    public static synchronized SocketManager newInstance() {
        if (null == manager)
            manager = new SocketManager();
        return manager;
    }

    public SocketManager setHostPort(String host, int port) {
        this.host = host;
        this.port = port;
        return this;
    }

    public synchronized SocketManager build() {
        createClient();
        return this;
    }

    //    设置时间，检测时间和心跳时间 默认1s
    public synchronized SocketManager setTime(int time) {
        heartTime = time;
        return this;
    }

    //设置以基准为准的心跳比例
    public synchronized SocketManager setHeartTimeRatio(int time) {
        heartTimeRatio = time;
        return this;
    }

    private void createClient() {
        if (null == client) {
            if (null == host || "".equals(host))
                client = new SocketClient
                        .Builder(new SocketConfig(defaultHost, defaultPort))
                        .bindStateListener(this)
                        .build();
            else
                client = new SocketClient
                        .Builder(new SocketConfig(host, port))
                        .bindStateListener(this)
                        .build();
            lock = new Object();
            checkLock = new Object();
        }
    }

    private void bindListenerAndStart() {
        try {
            client.destroy();
            client.createClient(SocketManager.this);
            output = client.getOutput();
        } catch (Exception e) {
            reconnect();
        }
    }

    public SocketManager setListener(ISocketListener listener) {
        this.listener = listener;
        return this;
    }

    public SocketManager setHeart(byte[] heartData) {
        this.heartData = heartData;
        return this;
    }

    @Override
    public SocketManager startSocket() {
        start();
        return this;
    }

    @Override
    public synchronized SocketManager stopSocket() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (null != client)
                    client.destroy();
            }
        }).start();
        return this;
    }

    @Override
    public synchronized SocketManager sendByteMsg(byte[] data) {
        if (null != listener)
            listener.sendData(data);
        if (null != output)
            output.sendData(data);
        else
            reconnect();
        return this;
    }

    @Override
    public SocketManager receiveByteData(byte[] data) {
        if (null != listener)
            listener.receiveData(data);
        return this;
    }

    @Override
    public SocketManager reconnect() {
        reconnectState();
        startSocket();
        return this;
    }


    @Override
    public void createState() {
        Debug.E("socket", " socket create");
        if (null != listener)
            listener.onSocketState(500, " socket create");
    }

    @Override
    public void createFailState() {
        Debug.E("socket", " socket create fail");
        if (null != listener)
            listener.onSocketState(400, " socket create fail");
    }

    @Override
    public void connectingState() {
        Debug.E("socket", " socket connecting ");
        if (null != listener)
            listener.onSocketState(501, " socket connecting ");
    }

    @Override
    public void connectedState() {
        Debug.E("socket", " socket connected");
        if (null != listener)
            listener.onSocketState(502, " socket connected");
    }

    @Override
    public void disconnectState() {
        Debug.E("socket", " socket disconnected");
        if (null != listener)
            listener.onSocketState(401, " socket disconnected");
    }

    @Override
    public void cancelConnectState() {
        Debug.E("socket", " socket cancel");
        if (null != listener)
            listener.onSocketState(402, " socket cancel");
    }

    @Override
    public void reconnectState() {
        Debug.E("socket", " socket reconnect");
        if (null != listener)
            listener.onSocketState(403, " socket reconnect");
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (SocketManager.this) {
                    if (null != client) {
                        bindListenerAndStart();
                    } else {
                        createClient();
                        bindListenerAndStart();
                    }
                }
            }
        }).start();
    }

    public SocketManager startCheckConnect() {
        stopCheckConnect();
        synchronized (checkLock) {
            checkTimer = new Timer();
            checkTimer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (!client.isConnect())
                        reconnect();
                }
            }, 0, heartTime);
        }
        return this;
    }


    public SocketManager startHeart() {
        stopHeart();
        synchronized (lock) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (heartData != null && heartData.length > 0)
                        sendByteMsg(heartData);
                }
            }, 0, heartTimeRatio * heartTime);
        }
        return this;
    }

    private void stopHeart() {
        synchronized (lock) {
            if (null != timer) {
                timer.cancel();
            }
            timer = null;
        }
    }

    private void stopCheckConnect() {
        synchronized (checkLock) {
            if (null != checkTimer) {
                checkTimer.cancel();
            }
            checkTimer = null;
        }
    }

}

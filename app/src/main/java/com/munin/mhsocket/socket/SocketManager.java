package com.munin.mhsocket.socket;

import com.munin.mhsocket.socket.entity.SocketClient;
import com.munin.mhsocket.socket.entity.SocketConfig;
import com.munin.mhsocket.socket.entity.SocketInput;
import com.munin.mhsocket.socket.entity.SocketOutput;
import com.munin.mhsocket.socket.interfaces.ISocket;
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
    private SocketInput input;
    private SocketOutput output;
    private Timer timer;
    private Timer checkTimer;
    private Object lock;
    private Object checkLock;
    private byte[] heartData = null;
    private int heartTime = 1000;

    private SocketManager() {
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

    public static synchronized SocketManager newInstance() {
        if (null == manager)
            manager = new SocketManager();
        return manager;
    }


    @Override
    public void startSocket() {
        start();
    }

    @Override
    public synchronized void stopSocket() {
        client.destroy();
    }

    @Override
    public synchronized void sendByteMsg(byte[] data) {
        if (null != output)
            output.sendData(data);
    }

    @Override
    public void receiveByteData(byte[] data) {
    }

    @Override
    public void reconnect() {
        reconnectState();
        startSocket();
    }


    @Override
    public void createState() {
        Debug.E("socket", " socket create");
    }

    @Override
    public void createFailState() {
        Debug.E("socket", " socket create fail");
    }

    @Override
    public void connectingState() {
        Debug.E("socket", " socket connecting ");
    }

    @Override
    public void connectedState() {
        Debug.E("socket", " socket connected");
    }

    @Override
    public void disconnectState() {
        Debug.E("socket", " socket disconnected");
    }

    @Override
    public void cancelConnectState() {
        Debug.E("socket", " socket cancel");
    }

    @Override
    public void reconnectState() {
        Debug.E("socket", " socket reconnect");
    }

    private void start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.destroy();
                client.createClient();
                input = client.getInput();
                output = client.getOutput();
                input.bindListener(SocketManager.this);
                input.startup();
                output.startup();
            }
        }).start();
    }

    public void startCheckConnect() {
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
    }


    public void startHeart() {
        stopHeart();
        synchronized (lock) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (heartData != null && heartData.length > 0)
                        sendByteMsg(heartData);
                }
            }, 0, heartTime);
        }
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

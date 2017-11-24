package com.munin.mhsocket.sm;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.munin.mhsocket.sm.interfaces.IMHSocketConfig;
import com.munin.mhsocket.sm.interfaces.IMHSocketController;
import com.munin.mhsocket.sm.interfaces.IMHSocketListener;
import com.munin.mhsocket.sm.interfaces.IMHTimingData;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.munin.mhsocket.sm.interfaces.MHSocketState.STATE_CONNECTED;
import static com.munin.mhsocket.sm.interfaces.MHSocketState.STATE_CONNECTING;
import static com.munin.mhsocket.sm.interfaces.MHSocketState.STATE_DEFAULT;
import static com.munin.mhsocket.sm.interfaces.MHSocketState.STATE_DISCONNECTED;
import static com.munin.mhsocket.sm.interfaces.MHSocketState.STATE_RECONNECT;


/**
 * Created by Administrator on 2017/10/31.
 */

public class MHSocketManager implements IMHSocketController {
    //实现定时器的功能
    Handler socketHandler = new Handler(Looper.getMainLooper());
    //    socket
    Socket socket;
    //    输入
    InputStream is_socket;
    //    输出
    OutputStream os_socket;
    //    socketManager
    private static MHSocketManager manager;

    private static String STATE_SOCKET = STATE_DEFAULT;

    private IMHSocketConfig defaultConfigure;

    private Lock mLock = new ReentrantLock();

    private MHSocketManager() {
    }


    public MHSocketManager setDefaultConfigure(IMHSocketConfig defaultConfigure) {
        this.defaultConfigure = defaultConfigure;
        return this;
    }

    public MHSocketManager setIMHSocketListener(IMHSocketListener listener) {
        if (defaultConfigure == null) {
            onException(new NullPointerException("请初始化你的 IMHSocketConfig"));
            throw new NullPointerException("请初始化你的 IMHSocketConfig");
        }
        defaultConfigure.setListener(listener);
        return this;
    }

    public static synchronized MHSocketManager newInstance() {
        if (null == manager)
            manager = new MHSocketManager();
        return manager;
    }

    //定时发送数据(包括心跳)
    private ConcurrentHashMap<String, IMHTimingData> postMsg = new ConcurrentHashMap<>();


    //socket连接runnable
    private Runnable connectRunnable = new Runnable() {
        @Override
        public void run() {
            init();
        }
    };


    //    接收消息
    private Runnable receiveRunnable = new Runnable() {
        @Override
        public void run() {
            receive();
        }
    };


    //    开启socket没有连接上再次开启
    private Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
            new Thread(connectRunnable).start();
        }
    };

    //检测socket是否连接
    private Runnable checkConnect = new Runnable() {
        @Override
        public void run() {

            if (socket != null) {
                checkSocketConnect(socket.isConnected());
                if (!socket.isConnected()) {
                    reconnect();

                }
            }
            socketHandler.postDelayed(this, defaultConfigure.getCheckConnectTime());
        }
    };


    @Override
    public void startSocket() {
        start();
    }

    @Override
    public void stopSocket() {
        stop();
    }


    @Override
    public void sendByteMsg(byte[] data) {
        sendData(data);
    }

    @Override
    public void onException(Exception error) {
        if (defaultConfigure != null) {
            IMHSocketListener listener = defaultConfigure.getListener();
            if (listener != null)
                listener.onException(error);
        }
    }

    @Override
    public void receiveByteData(byte[] data) {
        if (defaultConfigure != null) {
            IMHSocketListener listener = defaultConfigure.getListener();
            if (listener != null)
                listener.receive(data);
        }
    }

    @Override
    public void sendScheduleTimeData(IMHTimingData data) {
        sendScheduleData(data);
    }

    @Override
    public void releaseScheduleTimeData() {
        releaseScheduleDataAll();
    }

    @Override
    public void releaseScheduleTimeDataByTag(String tag) {
        releaseSpecialMsg(tag);
    }

    @Override
    public void checkSocketConnect(boolean isConnected) {
        if (defaultConfigure != null) {
            IMHSocketListener listener = defaultConfigure.getListener();
            if (listener != null)
                listener.checkConnected(isConnected);
        }
    }


    private void init() {
        if (STATE_SOCKET == STATE_CONNECTED)
            return;
        try {
            MHDebug.E("mh_socket", "start11:" + STATE_SOCKET);

            if (defaultConfigure == null) {
                onException(new NullPointerException("IMHSocketConfigure 未初始化"));
                throw new NullPointerException("IMHSocketConfigure 未初始化");
            }
            if (defaultConfigure != null) {
                IMHSocketListener listener = defaultConfigure.getListener();
                if (listener != null)
                    listener.connect();
            }
//            取消检测
            socketHandler.removeCallbacks(checkConnect);
            STATE_SOCKET = STATE_CONNECTING;
            if (defaultConfigure != null) {
                IMHSocketListener listener = defaultConfigure.getListener();
                if (listener != null)
                    listener.connecting();
            }
            MHDebug.E("mh_socket", "初始化:" + STATE_SOCKET);
            socket = new Socket(defaultConfigure.getHost(), defaultConfigure.getPort());
            if (defaultConfigure.isNeedConnected())
                socketHandler.postDelayed(checkConnect, defaultConfigure.getCheckConnectTime());
            MHDebug.E("mh_socket", "初始化1:" + STATE_SOCKET);
            if (socket.isConnected()) {
                socket.setReceiveBufferSize(1024 * 20);
                STATE_SOCKET = STATE_CONNECTED;
                is_socket = socket.getInputStream();
                os_socket = socket.getOutputStream();

                if (defaultConfigure != null) {
                    IMHSocketListener listener = defaultConfigure.getListener();
                    if (listener != null)
                        listener.connected();
                }
                MHDebug.E("mh_socket", STATE_SOCKET);
//                接收信息
                new Thread(receiveRunnable).start();

            } else {
//            重连
                MHDebug.E("mh_socket", "socket未连接操作+重连");
                reconnect();
                MHDebug.E("mh_socket", STATE_SOCKET);
            }
            MHDebug.E("mh_socket", "state 1:" + STATE_SOCKET);
        } catch (IOException e) {
            e.printStackTrace();
//            重连
            MHDebug.E("mh_socket", "连接操作+重连" + e.getMessage());
            reconnect();
            MHDebug.E("mh_socket", "state 2:" + STATE_SOCKET);

        }
    }

    //    开始连接socket
    private void start() {
        MHDebug.E("mh_socket", "开始。。。。。。。。。");
        new Thread(startRunnable).start();
    }


    //取消socket
    private void cancel() {
        if (null != socket && !socket.isClosed()) {
            try {
                socket.shutdownInput();
                socket.shutdownOutput();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                socket = null;
                closeIO();
            }
        }
    }

    //手动取消socket
    private void stop() {
        if (STATE_SOCKET != STATE_CONNECTED)
            return;
        STATE_SOCKET = STATE_DISCONNECTED;
        if (defaultConfigure != null) {
            IMHSocketListener listener = defaultConfigure.getListener();
            if (listener != null)
                listener.disconnect();
        }
        MHDebug.E("mh_socket", STATE_SOCKET);
        MHDebug.E("mh_socket", "停止。。。。。。。。。");
        cancel();
    }


    //重连
    private void reconnect() {
        STATE_SOCKET = STATE_RECONNECT;
        cancel();
        if (defaultConfigure != null) {
            IMHSocketListener listener = defaultConfigure.getListener();
            if (listener != null)
                listener.reconnect();
        }
        MHDebug.E("mh_socket", "重连。。。。。。。。。");
        socketHandler.removeCallbacks(startRunnable);
        socketHandler.postDelayed(startRunnable, 200);
    }

    //    发送数据
    private void sendData(byte[] msg) {
        if (STATE_SOCKET == STATE_CONNECTED) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            new SendSocketMsgThread(msg).start();
        }
    }


    //关闭io流
    private void closeIO() {
        if (null != is_socket) {
            try {
                is_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                is_socket = null;
            }

        }
        if (null != os_socket) {
            try {
                os_socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                os_socket = null;
            }

        }
    }

//    接收数据部分

    private void receive() {
        try {
            byte[] buffer = new byte[1024 * 20];
            int length = 0;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            while (STATE_CONNECTED.equals(STATE_SOCKET) && !socket.isClosed() && !socket.isInputShutdown() &&
                    ((length = is_socket.read(buffer)) != -1) && socket.isConnected()) {
                if (length > 0) {
                    outputStream.write(buffer, 0, length);
                    byte[] result = outputStream.toByteArray();
//接收数据（暴露出数据）
                    receiveByteData(result);
                    outputStream.reset();
                }
            }
            if (!socket.isConnected())
                reconnect();
        } catch (Exception e) {
            e.printStackTrace();

            reconnect();
        }
    }


    private void sendScheduleData(IMHTimingData data) {
        if (TextUtils.isEmpty(data.getTag()) || data.getScheduleTime() < 0 || data.getByteData() == null) {
            onException(new NullPointerException("请初始化 定时数据的tag ,定时的时间 及其 发送的数据"));
            throw new NullPointerException("请初始化 定时数据的tag ,定时的时间 及其 发送的数据");
        }
        if (!postMsg.containsKey(data.getTag())) {
            postMsg.put(data.getTag(), data);
//           开启定时操作
            data.setHandler(socketHandler);
            data.setSocketController(this);
            data.run();
        }
    }


    private void releaseScheduleDataAll() {
        if (postMsg.isEmpty())
            return;
        Iterator<Map.Entry<String, IMHTimingData>> it = postMsg.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, IMHTimingData> entry = it.next();
            entry.getValue().release();
            it.remove();
        }
    }

    private void releaseSpecialMsg(String tag) {
        if (postMsg.containsKey(tag)) {
            postMsg.get(tag).release();
            postMsg.remove(tag);
        }
    }


    public class SendSocketMsgThread extends Thread {
        private byte[] msg = null;

        public SendSocketMsgThread(byte[] msg) {
            this.msg = msg;
        }

        @Override
        public void run() {
            super.run();
            sendMsg(msg);
        }
    }

    //发送数据
    private void sendMsg(byte[] msg) {
        mLock.lock();
        try {
            Thread.sleep(100);
            MHDebug.E("mh_socket", "发送。。。。。。。。。");
            if (null == socket) {
                reconnect();
                return;
            }
            if (!socket.isClosed() && !socket.isOutputShutdown()) {
                try {
                    os_socket.write(msg);
                    os_socket.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                    MHDebug.E("mh_socket", "写操作+重连" + e.getMessage());
                    reconnect();
                }
            } else {
                MHDebug.E("mh_socket", "读写无法连接+重连");
                reconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mLock.unlock();
        }
    }

}

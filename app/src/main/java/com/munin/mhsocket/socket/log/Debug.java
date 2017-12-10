package com.munin.mhsocket.socket.log;
import android.util.Log;

/**
 * Created by Administrator on 2017/10/31.
 * 调试使用
 * logcat输出显示
 */

public class Debug {
    private static boolean enDebug = true;

    public static void I(boolean control, String tag, String msg) {
        if (control && enDebug)
            Log.i(tag, tag + ": " + msg);
    }

    public static void E(boolean control, String TAG, String msg) {
        if (control && enDebug)
            Log.e(TAG, TAG + " :" + msg);
    }

    public static void D(boolean control, String TAG, String msg) {
        if (control && enDebug)
            Log.d(TAG, TAG + " :" + msg);
    }

    public static void syso(boolean control, String TAG, String msg) {
        if (control && enDebug)
            System.out.println(TAG + ": " + msg);
    }


    public static void I(String tag, String msg) {
        I(true, tag, msg);
    }

    public static void E(String TAG, String msg) {
        E(true, TAG, "当前线程: "+Thread.currentThread()+"  " + msg);
    }

    public static void D(String TAG, String msg) {
        D(true, TAG, msg);
    }

    public static void syso(String TAG, String msg) {
        syso(true, TAG, msg);
    }
}

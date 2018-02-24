package com.munin.mhsocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

/**
 * Created by munin on 2017/12/8.
 */

public class MainTest {
    public static void main(String[] args) {
        Socket socket = null;
        try {
            socket = new Socket("127.0.0.1", 8080);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedSource source = Okio.buffer(Okio.source(inputStream));
            BufferedSink sink = Okio.buffer(Okio.sink(outputStream));
            writeMsg(sink, "hello");
            while (true) {
                int length = source.readInt();
                String message = source.readString(length, Charset.forName("utf-8"));
                System.out.println("length is: "+length+" , message is : "+message);
                if ("error exit".equals(message)) {
                    break;
                }
                String respMsg = getResponseAccordMsg(message);
                writeMsg(sink, respMsg);
                if ("error exit".equals(respMsg)) {
                    break;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void writeMsg(BufferedSink sink, String msg) {
        try {
            int msgLength = msg.getBytes().length;
            sink.writeInt(msgLength);
            sink.writeString(msg, Charset.forName("utf-8"));
            sink.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getResponseAccordMsg(String msg) {
        String result = "";
        if (msg != null && msg.length() > 0) {
            if (msg.equals("hello")) {
                result = "nice to meet you";
            } else if (msg.equals("nice to meet you too")) {
                result = "see you";
            }
        }
        if (result.length() == 0) {
            result = "error exit";
        }
        return result;
    }

}

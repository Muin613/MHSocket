package com.munin.mhsocket;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2017/9/22.
 * 数据转化类
 */

public class ParseUtils {


    /**将二进制转换成16进制
     * @param buf
     * @return
     */
    public static String parseByte2HexStr(byte buf[]) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < buf.length; i++) {
            String hex = Integer.toHexString(buf[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase());
        }
        return sb.toString();
    }

    /**将16进制转换为二进制
     * @param hexStr
     * @return
     */
    public static byte[] parseHexStr2Byte(String hexStr) {
        if (hexStr.length() < 1)
            return null;
        byte[] result = new byte[hexStr.length()/2];
        for (int i = 0;i< hexStr.length()/2; i++) {
            int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);
            int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);
            result[i] = (byte) (high * 16 + low);
        }
        return result;
    }



    public static boolean isEmpty(CharSequence str){
        if (str == null || str.length() == 0)
            return true;
        else
            return false;
    }


    /*
    * 把16进制字符串转换成字节数组 @param hex @return
    */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
//        byteTo16(result);
        return result;
    }

    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


    public static String byteTo16(byte[] bts){
        String[] strHex={"0","1","2","3","4","5","6","7","8","9","a","b","c","d","e","f"};
        StringBuilder resStr= new StringBuilder();
        for(byte bt: bts){
            int low =(bt & 15);
            int high = bt>>4 & 15;
            String Str = strHex[high]+strHex[low];
            resStr.append(Str);
//            resStr += Str;
        }
        return resStr.toString();
    }

    /**
     * 大端byte[]转int
     * @param bytes
     * @return
     */
    public static int big_bytesToInt(byte[] bytes) {
        int addr = 0;
        if (bytes.length == 1) {
            addr = bytes[0] & 0xFF;
        } else if (bytes.length == 2) {
            addr = bytes[0] & 0xFF;
            addr = (addr << 8) | (bytes[1] & 0xff);
        } else {
            addr = bytes[0] & 0xFF;
            addr = (addr << 8) | (bytes[1] & 0xff);
            addr = (addr << 8) | (bytes[2] & 0xff);
            addr = (addr << 8) | (bytes[3] & 0xff);
        }
        return addr;
    }


    /**
     * 用大端表示法将int转换为不同字节数的byte数组
     * @param i
     * @param len
     * @return
     */
    public static byte[] big_intToByte(int i, int len) {
        byte[] abyte = new byte[len];
        ;
        if (len == 1) {
            abyte[0] = (byte) (0xff & i);
        } else if (len == 2) {
            abyte[0] = (byte) ((i >>> 8) & 0xff);
            abyte[1] = (byte) (i & 0xff);
        } else {
            abyte[0] = (byte) ((i >>> 24) & 0xff);
            abyte[1] = (byte) ((i >>> 16) & 0xff);
            abyte[2] = (byte) ((i >>> 8) & 0xff);
            abyte[3] = (byte) (i & 0xff);
        }
        return abyte;
    }

    /**
     * 小端byte[]转int
     * @param bytes
     * @return
     */
    public static int little_bytesToInt(byte[] bytes) {
        int addr = 0;
        if (bytes.length == 1) {
            addr = bytes[0] & 0xFF;
        } else if (bytes.length == 2) {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
        } else {
            addr = bytes[0] & 0xFF;
            addr |= (((int) bytes[1] << 8) & 0xFF00);
            addr |= (((int) bytes[2] << 16) & 0xFF0000);
            addr |= (((int) bytes[3] << 24) & 0xFF000000);
        }
        return addr;
    }

    /**
     * 用小端表示法将int转换为不同字节数的byte数组
     * @param i
     * @param len
     * @return
     */
    public static byte[] little_intToByte(int i, int len) {
        byte[] byte_data = new byte[len];
        if (len == 1) {
            byte_data[0] = (byte) (0xff & i);
        } else if (len == 2) {
            byte_data[0] = (byte) (0xff & i);
            byte_data[1] = (byte) ((0xff00 & i) >> 8);
        } else {
            byte_data[0] = (byte) (0xff & i);
            byte_data[1] = (byte) ((0xff00 & i) >> 8);
            byte_data[2] = (byte) ((0xff0000 & i) >> 16);
            byte_data[3] = (byte) ((0xff000000 & i) >> 24);
        }
        return byte_data;
    }

    /**
     * 合并多个byte数组
     * @param args
     * @return
     */
    public static byte[] byteMerger(byte[]... args){
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            for(byte[] data : args){
                bos.write(data);
            }
        }catch (IOException e){
            return null;
        }
        byte[] total_data = bos.toByteArray();
        return total_data;
    }

    public static String convertStringToASCII(String str){
        if(str==null || str.equals(""))
            return "";
        else{
            try{
                byte[] strings = str.getBytes("utf-8");
                return byteTo16(strings);
            }catch (Exception e){
                return null;
            }
        }
    }

    public static String convertASCIIToString(String hex){
        if(hex==null || hex.equals(""))
            return "";
        else {
            try{
                byte[] data = tvhexStringToByte(hex);
                return new String(data,"utf-8");
            }catch (Exception e){
                return null;
            }
        }
    }


    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] tvhexStringToByte(String hexString) {
        if (TextUtils.isEmpty(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }
}

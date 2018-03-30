package com.munin.mhsocket;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

/**
 * Created by munin on 2017/12/14.
 * 心跳
 */

public class HeartCmd {
    public static final byte MByte = 0x4D;
    public static final byte[] M = {MByte};
    //   心跳包
    public static final byte[] heart() {
        byte[] CID = "5003".getBytes();
        /**
         * 计算CommandLength的长度：报文中Message ID以及Text Message字段,包括CL本身的总字节数
         */
        int cl = CID.length + 2;

        byte[] CL = ParseUtils.little_intToByte(cl,2);
        byte[] SID = {0x00,0x00,0x00,0x00};
        int tl = 4 + M.length+getMacAddr().length+CL.length+CID.length+SID.length+1;
        byte[] TL = ParseUtils.little_intToByte(tl,4);

        byte[] total_data = ParseUtils.byteMerger(TL,M,getMacAddr(),CL,CID,SID);
        byte[] CK = getCheckNum(total_data);
        ParseUtils.byteTo16(ParseUtils.byteMerger(total_data,CK));

        return ParseUtils.byteMerger(total_data,CK);
    }
    public static byte[] getMacAddr()  {
        String[] macaddress = getMacAddress().split(":");
//        Log.d("mac地址",getMacAddress());
        byte[] ret = new byte[macaddress.length];
        for(int i=0;i<macaddress.length;i++){
            ret[i] = ParseUtils.hexStringToByte(macaddress[i])[0];
        }
        return ret;
    }
    /**
     * 获取MAC地址
     * @return 数据发送方的mac地址
     */
    public static String getMacAddress() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:",b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00";
    }

    public static byte[] getCheckNum(byte[] data) {
        int checknum = data[0] ^ data[1];
        for (int i = 2; i < data.length; i++) {
            checknum = checknum ^ data[i];
        }
        byte[] CK = ParseUtils.little_intToByte(checknum, 1);
        return CK;
    }
}

package com.example.tuankiet.myapp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class Constant {
    final public static String SERVER_ADDR="172.28.12.16";
    final public static int SERVER_PORT=5000;
    final public static int LOCAL_PORT=3000;
    final public static String BASE_URL="http://o-michat.herokuapp.com/";
    final public static String CHANNEL_ID="34243";
    public static String TOKEN="";
    public static String ip;
    public static String getIP(Context context)  {
        WifiManager wifiMan = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInf = wifiMan.getConnectionInfo();
        int ipAddress = wifiInf.getIpAddress();
        String ip = String.format("%d.%d.%d.%d", (ipAddress & 0xff),(ipAddress >> 8 & 0xff),(ipAddress >> 16 & 0xff),(ipAddress >> 24 & 0xff));
        return ip;
    }
}

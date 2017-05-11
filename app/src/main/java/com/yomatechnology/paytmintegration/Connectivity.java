package com.yomatechnology.paytmintegration;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;



public class Connectivity {
    public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;

    /**
     * Get the network info
     *
     * @param context
     * @return
     */
    public static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    /**
     * Check if there is any connectivity
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected());
    }

    /**
     * Check if there is any connectivity to a Wifi network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedWifi(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * Check if there is any connectivity to a mobile network
     *
     * @param context
     * @return
     */
    public static boolean isConnectedMobile(Context context) {
        NetworkInfo info = Connectivity.getNetworkInfo(context);
        return (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE);
    }

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {

            // 2 G Connection
            case TelephonyManager.NETWORK_TYPE_GPRS:// ~ 100 kbps
                return "2G" + " " + "100 kbps";
            case TelephonyManager.NETWORK_TYPE_EDGE:// ~ 50-100 kbps
                return "2G" + " " + "50-100 kbps";
            case TelephonyManager.NETWORK_TYPE_CDMA:// ~ 14-64 kbps
                return "2G" + " " + "14-64 kbps";
            case TelephonyManager.NETWORK_TYPE_1xRTT: // ~ 50-100 kbps
                return "2G" + " " + "50-100 kbps";
            case TelephonyManager.NETWORK_TYPE_IDEN:// ~25 kbps
                return "2G" + " " + "25 kbps";

            // 3 G Connection
            case TelephonyManager.NETWORK_TYPE_UMTS:// ~ 400-7000 kbps
                return "3G" + " " + "400-7000 kbps";
            case TelephonyManager.NETWORK_TYPE_EVDO_0:// ~ 400-1000 kbps
                return "3G" + " " + "400-1000 kbps";
            case TelephonyManager.NETWORK_TYPE_EVDO_A:// ~ 600-1400 kbps
                return "3G" + " " + "600-1400 kbps";
            case TelephonyManager.NETWORK_TYPE_HSDPA:// ~ 2-14 Mbps
                return "3G" + " " + "2-14 Mbps";
            case TelephonyManager.NETWORK_TYPE_HSUPA:// ~ 1-23 Mbps
                return "3G" + " " + "1-23 Mbps";
            case TelephonyManager.NETWORK_TYPE_HSPA:// ~ 700-1700 kbps
                return "3G" + " " + "700-1700 kbps";
            case TelephonyManager.NETWORK_TYPE_EVDO_B:// ~ 5 Mbps
                return "3G" + " " + "5 Mbps";
            case TelephonyManager.NETWORK_TYPE_EHRPD:// ~ 1-2 Mbps
                return "3G" + " " + "1-2 Mbps";
            case TelephonyManager.NETWORK_TYPE_HSPAP:// ~ 10-20 Mbps
                return "3G" + " " + "10-20 Mbps";

            // 4 G Connection
            case TelephonyManager.NETWORK_TYPE_LTE:// ~ 20+ Mbps
                return "4G" + " " + "20+ Mbps";
            default:
                return "Unknown";
        }
    }


}
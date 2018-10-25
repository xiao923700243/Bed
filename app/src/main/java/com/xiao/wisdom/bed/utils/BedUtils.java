package com.xiao.wisdom.bed.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.xiao.wisdom.bed.R;

import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * Created by Administrator on 2018/8/2.
 */

public class BedUtils {
    public static final String DEVICE_SSID = "hewei"; //TP-LINK_99B1 //hewei

    public static final String CMD_MOTOR_UP = "CMD_MOTOR_UP";
    public static final String CMD_MOTOR_DOWN  = "CMD_MOTOR_DOWN";
    public static final String CMD_MOTOR_PAUSE = "CMD_MOTOR_PAUSE";
    public static final String CMD_DIRECTION_CONVERT = "CMD_DIRECTION_CONVERT";
    public static final String CMD_LED_ON = "CMD_LED_ON";
    public static final String CMD_LED_OFF = "CMD_LED_OFF";
    public static final String CMD_PRESSURE_TEST = "CMD_PRESSURE_TEST_[val]_#*#*$OK";
    public static final String CMD_PRESSURE_AUTO_CALIBRATION = "CMD_PRESSURE_AUTO_CALIBRATION";
    public static final String CMD_READ_ALL = "CMD_READ_ALL";
    public static final String CMD_LIFE_TIME_TEST_START = "CMD_LIFE_TIME_TEST_START";
    public static final String CMD_LIFE_TIME_TEST_FINISH = "CMD_LIFE_TIME_TEST_FINISH";
    public static final String CMD_LIFE_TIME_VAL = "CMD_LIFE_TIME_TEST_[val]_#*#*$OK";
    public static final String CMD_SEND_TIME_XX_XX_XX_XX_XX_XX = "CMD_SEND_TIME_YY_mm_DD_HH_MM_SS";
    public static final String RSP_SSID_NAME_END = "RSP_SSID_NAME_END";
    public static final String RSP_PASSWORD_END = "RSP_PASSWORD_END";
    public static final String CMD_SEND_SSID_NAME_END = "CMD_SSID_NAME_[ssid]_#*#*$OK";
    public static final String CMD_SEND_PASSWORD_END = "CMD_PASSWORD_[password]_#*#*$OK";
    public static final String RSP_SSID_PASSWORD_CONFIG_END = "RSP_SSID_PASSWORD_CONFIG_END";
    public static final String CHECK_PASSWORD = "herway";

    /**
     * 判断网络状态
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    networkInfo[i].isAvailable();
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * md5加密
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result.append(temp);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断Wifi是否连接
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWiFiNetworkInfo = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if(mWiFiNetworkInfo != null){
            return mWiFiNetworkInfo.isAvailable();
        }
        return false;
    }

    /**
     *
     * 获取连接的WIFI SSID
     * @param context
     * @return
     */
    public static String getWifiSSID(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if(wifiInfo!=null){
            return wifiInfo.getSSID();
        }
        return "";
    }

    public static boolean isConnectSSID(Context context) {
        Context myContext = context;
        if (myContext == null) {
            throw new NullPointerException("context is null");
        }
        WifiManager wifiMgr = (WifiManager) myContext.getSystemService(Context.WIFI_SERVICE);
        if (wifiMgr.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            ConnectivityManager connManager = (ConnectivityManager) myContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifiInfo = connManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(wifiInfo.isConnected()){
                //Log.i("xiao_","已经连接SSID = "+wifiMgr.getConnectionInfo().getSSID());
                return wifiMgr.getConnectionInfo().getSSID().replace("\"","").equals(DEVICE_SSID);
            }else{
                return false;
            }
        } else {
            return false;
        }
    }

    public static String detailsToDevName(Context context,String type){
        if(type.equals(context.getResources().getString(R.string.bind_activity_smartbed_msg))){
            return "smartbed";
        }else if(type.equals(context.getResources().getString(R.string.bind_activity_smartlock_msg))){
            return "smartlock";
        }else if(type.equals(context.getResources().getString(R.string.bind_activity_smartcabinet_msg))){
            return "smartcabinet";
        }
        return "smartbed";
    }


    /**
     *
     * 获取手机唯一码
     * @param context
     * @return
     */
    public static String getImei(Context context){
        String imei = ShareUtils.getInstance(context).getIMEI();
        if(TextUtils.isEmpty(imei)){
            imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            if(TextUtils.isEmpty(imei) || imei.equals("9774d56d682e549c")){
                imei = UUID.randomUUID().toString();
            }
            ShareUtils.getInstance(context).saveIMEI(imei);
            return imei;
        }else{
            return imei;
        }
    }

    /**
     * 字符串转换UTF-8编码格式
     * @param source
     * @return
     */
    public static String getUtf8String(String source){
        try{
            return URLDecoder.decode(source, "UTF-8");
        }catch (Exception e){

        }
        return null;
    }
}

package com.xiao.wisdom.bed.net;


import android.os.Handler;

import com.xiao.wisdom.bed.bean.BindUserDeviceResult;
import com.xiao.wisdom.bed.bean.ChengeDeviceInfoResult;
import com.xiao.wisdom.bed.bean.GetDevStatsResult;
import com.xiao.wisdom.bed.bean.GetUserAllDevStatsResult;
import com.xiao.wisdom.bed.bean.LoginUserResult;
import com.xiao.wisdom.bed.bean.RegisterResult;
import com.xiao.wisdom.bed.bean.SendCmdResult;
import com.xiao.wisdom.bed.bean.ChengeUserPasswordResult;
import com.xiao.wisdom.bed.bean.disBindUserDeviceResult;
import com.xiao.wisdom.bed.net.base.OkRequest;
import com.xiao.wisdom.bed.net.base.RequestParams;
import com.xiao.wisdom.bed.net.base.ResultCallBack;
import com.xiao.wisdom.bed.utils.BedUtils;

import java.util.ArrayList;
import java.util.HashSet;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class NetApi extends UrlTool{
    public static final String Service_Address = "http://118.89.26.53:7079";
    //public static final String Service_Address = "http://10.0.0.151:8080";
    public static final String Login_Address = "/smartdev/loginUser";
    public static final String SendCmd_Address = "/smartdev/sendDevCommend";
    public static final String GetDevState_Address = "/smartdev/getDevStats";
    public static final String GetUserCheckCode_Address = "/smartdev/getUserCheckCode";
    public static final String Register_Address = "/smartdev/registerUser";
    public static final String ChangePassword_Address = "/smartdev/chengeUserPassword";
    public static final String BindDevice_Address = "/smartdev/bindUserDevice";
    public static final String UnBindDervice_Address = "/smartdev/disBindUserDevice";
    public static final String GetUserAllDevStats_Address = "/smartdev/getUserAllDevStats";
    public static final String ChengeDeviceInfo_Address = "/smartdev/chengeDeviceInfo";

    /**
     * 发送指令
     * @param user
     * @param devid
     * @param cmd
     * @param callBack
     */
    public static void sendDevCommend(String user, String devid, String cmd, ResultCallBack<SendCmdResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("user", user)
                .put("devid",devid)
                .put("cmd",cmd);
        String url = Service_Address+SendCmd_Address;
        new OkRequest.Builder().url(url).params(params).get(callBack);
    }

    /**
     * 获取设备状态
     * @param user
     * @param devid
     * @param callBack
     */
    public static void getDevStats(String user, String devid, ResultCallBack<GetDevStatsResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("user", user)
                .put("devid",devid);
        String url = Service_Address+GetDevState_Address;
        new OkRequest.Builder().url(url).params(params).get(callBack);
    }

    /**
     * 获取验证码
     * @param imei
     * @param callback
     */
    public static void getUserCheckCode(String imei, Callback callback){
        String url = Service_Address+GetUserCheckCode_Address+"?imei="+imei;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    /**
     * 用户注册
     * @param imei
     * @param user
     * @param password
     * @param checkcode
     * @param callBack
     */
    public static void registerUser(String imei,String user,String password,String checkcode,ResultCallBack<RegisterResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("imei", imei)
                .put("user",user)
                .put("password", BedUtils.md5(password))
                .put("checkcode",checkcode);
        String url = Service_Address+Register_Address;
        new OkRequest.Builder().url(url).params(params).get(callBack);
    }

    /**
     * 用户登录
     * @param user
     * @param password
     * @param callBack
     */
    public static void loginUser(String user, String password, ResultCallBack<LoginUserResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("user",user)
                .put("password", BedUtils.md5(password));
        String url = Service_Address+Login_Address;
        new OkRequest.Builder().url(url).params(params).get(callBack);
    }

    /**
     * 修改密码
     * @param imei
     * @param user
     * @param newpsd
     * @param checkcode
     * @param callBack
     */
    public static void chengeUserPassword(String imei, String user, String newpsd, String checkcode, ResultCallBack<ChengeUserPasswordResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("imei",imei)
                .put("user",user)
                .put("checkcode",checkcode)
                .put("newpsd", BedUtils.md5(newpsd));
        String url = Service_Address+ChangePassword_Address;
        new OkRequest.Builder().url(url).params(params).get(callBack);
    }


    /**
     * 绑定设备
     * @param user
     * @param devid
     * @param devtype
     * @param callBack
     */
    public static void bindUserDevice(String user, String devid,String devname, String devtype,String cstname, ResultCallBack<BindUserDeviceResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("user",user)
                .put("devid",devid)
                .put("cstname",cstname)
                .put("devtype",devtype) //区分Gps、wifi设备类型
                .put("devname",devname); //区分智能床、智能锁、智能柜
        String url = Service_Address+BindDevice_Address;
        new OkRequest.Builder().url(url).params(params).post(callBack);
    }

    /**
     * 设备解绑
     * @param user
     * @param devid
     * @param devtype
     * @param callBack
     */
    public static void disBindUserDevice(String user, String devid, String devtype, ResultCallBack<disBindUserDeviceResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("user",user)
                .put("devid",devid)
                .put("devtype",devtype);
        String url = Service_Address+UnBindDervice_Address;
        new OkRequest.Builder().url(url).params(params).get(callBack);
    }

    public static void getUserAllDevStats(String user, ResultCallBack<GetUserAllDevStatsResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("user",user);
        String url = Service_Address+GetUserAllDevStats_Address;
        new OkRequest.Builder().url(url).params(params).get(callBack);
    }


    /**
     * 修改设备信息
     * @param user
     * @param devid
     * @param devname
     * @param devtype
     * @param cstname
     * @param callBack
     */
    public static void chengeDeviceInfo(String user, String devid, String devname, String devtype, String cstname, ResultCallBack<ChengeDeviceInfoResult> callBack){
        RequestParams params = RequestParams.newInstance()
                .put("user",user)
                .put("devid",devid)
                .put("devname",devname)
                .put("devtype",devtype)
                .put("cstname",cstname);
        String url = Service_Address+ChengeDeviceInfo_Address;
        new OkRequest.Builder().url(url).params(params).post(callBack);
    }
}

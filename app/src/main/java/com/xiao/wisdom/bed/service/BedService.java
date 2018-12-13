package com.xiao.wisdom.bed.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.xiao.wisdom.bed.BedApplication;
import com.xiao.wisdom.bed.bean.BindUserDeviceResult;
import com.xiao.wisdom.bed.bean.ChengeDeviceInfoResult;
import com.xiao.wisdom.bed.bean.ChengeUserPasswordResult;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.GetDevStatsResult;
import com.xiao.wisdom.bed.bean.GetUserAllDevStatsResult;
import com.xiao.wisdom.bed.bean.LoginUserResult;
import com.xiao.wisdom.bed.bean.QuickRegiserResult;
import com.xiao.wisdom.bed.bean.RegisterResult;
import com.xiao.wisdom.bed.bean.SendCmdResult;
import com.xiao.wisdom.bed.bean.UpdateUserInfoResult;
import com.xiao.wisdom.bed.bean.disBindUserDeviceResult;
import com.xiao.wisdom.bed.net.NetApi;
import com.xiao.wisdom.bed.net.base.ResultCallBack;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.utils.ShareUtils;

import org.greenrobot.eventbus.EventBus;

import okhttp3.Headers;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/8/2.
 */

public class BedService extends Service {
    private Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
    }

    public class BedBinder extends Binder{
        public BedService getService(){
            return BedService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new BedBinder();
    }

    public void init(final Handler handler){
        final String user = ShareUtils.getInstance(this).getUser();
        final String password = ShareUtils.getInstance(this).getPassword();
        boolean auto = (boolean) ShareUtils.getInstance(this).getValue("auto_login",true);
        boolean exit = (boolean) ShareUtils.getInstance(this).getValue("exit_account",false);
        if(!TextUtils.isEmpty(user) && auto && !exit){
            NetApi.loginUser(user,password,new ResultCallBack<LoginUserResult>(){
                @Override
                public void onFailure(int statusCode, Request request, Exception e) {
                    handler.sendEmptyMessage(0x02);
                }

                @Override
                public void onSuccess(int statusCode, Headers headers, LoginUserResult model) {
                    if(model.status.equals("ok")){
                        handler.sendEmptyMessage(0x04);
                        ShareUtils.getInstance(context).saveUser(user,password,model.data.nickname); //保存登录信息
                    }else{
                        handler.sendEmptyMessage(0x02);
                    }
                }
            });
        }else{
            handler.sendEmptyMessage(0x02);
        }
    }

    public void quickRegiser(final String imei,final Handler handler){
        NetApi.quickRegiser(imei,new ResultCallBack<QuickRegiserResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                handler.sendEmptyMessage(0x01);
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, QuickRegiserResult model) {
                handler.sendMessage(obtainMessage(0x04,model));
            }
        });
    }

    public void loginUser(final Handler handler,final String user,final String password){
        NetApi.loginUser(user,password,new ResultCallBack<LoginUserResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                handler.sendEmptyMessage(0x01); //连接服务器异常
                showL("loginUser：0x01");
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, LoginUserResult model) {
                if(model.status.equals("ok")){
                    ShareUtils.getInstance(context).saveUser(user,password,model.data.nickname); //保存登录信息
                    handler.sendEmptyMessage(0x02); //登录成功
                    showL("loginUser：0x02");
                }else{
                    handler.sendEmptyMessage(0x03); //登录失败
                    showL("loginUser：0x03");
                }
            }
        });
    }

    public void checkDeviceBind(final String deviceid,final Handler handler){
        showL("校验ID = "+deviceid);
        final String user = ShareUtils.getInstance(this).getUser();
        NetApi.getUserAllDevStats(user,new ResultCallBack<GetUserAllDevStatsResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01);
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, GetUserAllDevStatsResult model) {
                super.onSuccess(statusCode, headers, model);
                showL(model.msg);
                showL(model.status);
                if(model.status.equals("ok")){
                    if(model.data!=null && model.data.size()>0){
                        int index = -1;
                        for(int i=0;i<model.data.size();i++){
                            if(deviceid.equals(model.data.get(i).devid.trim())){
                                showL("验证失败，已经绑定该设备");
                                index = i;
                                handler.sendEmptyMessage(0x04); //已经绑定改设备
                                break;
                            }
                        }
                        showL("校验结果：index = "+index);
                        if(index == -1){
                            handler.sendEmptyMessage(0x03); //验证成功
                        }
                    }else{
                        showL("data值为null，验证成功");
                        handler.sendEmptyMessage(0x03); //验证成功
                    }
                }else{
                    showL("接口返回失败，验证成功");
                    handler.sendEmptyMessage(0x03); //请重新扫描
                }
            }
        });
    }

    public void getDevStats(final String deviceid,final Handler handler,final boolean state){
        final String user = ShareUtils.getInstance(this).getUser();
        NetApi.getUserAllDevStats(user,new ResultCallBack<GetUserAllDevStatsResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                if(state){
                    handler.sendEmptyMessage(0x07); //服务器异常或手机网络异常
                }
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, GetUserAllDevStatsResult model) {
                super.onSuccess(statusCode, headers, model);
                if(model.status.equals("ok")){
                    if(model!=null && model.data!=null && model.data.size()>0){
                        boolean isSuccess = false;
                        for(int i=0;i<model.data.size();i++){
                            if(model.data.get(i).devid.trim().equals(deviceid) && model.data.get(i).online == 1){
                                isSuccess = true;
                                break;
                            }
                        }
                        if(isSuccess){
                            if(state) {
                                handler.sendEmptyMessage(0x08);
                            }
                        }else{
                            if(state) {
                                handler.sendEmptyMessage(0x07);
                            }
                        }
                    }else{
                        if(state) {
                            handler.sendEmptyMessage(0x07);
                        }
                    }
                }else{
                    if(state){
                        handler.sendEmptyMessage(0x07);
                    }
                }
            }
        });
    }

    public void bindUserDevice(final String devid,final String cstname,final Handler handler,final boolean needSend){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.bindUserDevice(user,devid,"smartbed","WIFI",cstname,new ResultCallBack<BindUserDeviceResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                if(needSend){
                    handler.sendEmptyMessage(0x03);// 绑定失败
                }
            }
            @Override
            public void onSuccess(int statusCode, Headers headers, BindUserDeviceResult model) {
                super.onSuccess(statusCode, headers, model);
                if(model.status.equals("ok")){
                    if(needSend){
                        handler.sendEmptyMessage(0x02);// 绑定成功
                    }
                }else{
                    if(needSend){
                        handler.sendEmptyMessage(0x03);
                    }
                }
            }
        });
    }

    public void getUserAllDevStats(final Handler handler,final boolean need){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.getUserAllDevStats(user,new ResultCallBack<GetUserAllDevStatsResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                if(need){
                    handler.sendEmptyMessage(0x01); //服务器或网络异常
                }
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, GetUserAllDevStatsResult model) {
                super.onSuccess(statusCode, headers, model);
                if(need){
                    handler.sendMessage(obtainMessage(0x02,model));
                }
                EventBus.getDefault().post(new Event.DevStateEvent(model));
            }
        });
    }
    public void sendAutoCalibration(String devid,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.sendDevCommend(user,devid,BedUtils.CMD_AUTO_LOAD_CALIBRATION,new ResultCallBack<SendCmdResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01);
            }
            @Override
            public void onSuccess(int statusCode, Headers headers, SendCmdResult model) {
                super.onSuccess(statusCode, headers, model);
                handler.sendMessage(obtainMessage(0x06,model));
            }
        });
    }

    public void sendConvert(String devid,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.sendDevCommend(user,devid,BedUtils.CMD_DIRECTION_CONVERT ,new ResultCallBack<SendCmdResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01);
            }
            @Override
            public void onSuccess(int statusCode, Headers headers, SendCmdResult model) {
                super.onSuccess(statusCode, headers, model);
                handler.sendMessage(obtainMessage(0x07,model));
            }
        });
    }

    public void sendPressure(String devid,int value,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        String cmd = BedUtils.CMD_PRESSURE_SET.replace("[Pressure]",value+"");
        NetApi.sendDevCommend(user,devid, cmd,new ResultCallBack<SendCmdResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01);
            }
            @Override
            public void onSuccess(int statusCode, Headers headers, SendCmdResult model) {
                super.onSuccess(statusCode, headers, model);
                handler.sendMessage(obtainMessage(0x08,model));
            }
        });
    }


    public void sendDevCommend(String devid,final String cmd,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.sendDevCommend(user,devid,cmd,new ResultCallBack<SendCmdResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                showL("发送失败");
                handler.sendEmptyMessage(0x05);
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, SendCmdResult model) {
                super.onSuccess(statusCode, headers, model);
                if(model.status.equals("ok")){
                    showL("发送成功");
                }else{
                    showL("发送失败");
                    handler.sendMessage(obtainMessage(0x06,model.msg));
                }
            }
        });
    }


    public void registerUser(String imei,String user,String passowrd,String checkcode,String nickname,final Handler handler){
        NetApi.registerUser(imei,user,passowrd,checkcode,nickname,new ResultCallBack<RegisterResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01); //网络异常
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, RegisterResult model) {
                super.onSuccess(statusCode, headers, model);
                if(model.status.equals("ok")){
                    handler.sendEmptyMessage(0x02); //注册完成
                }else{
                    handler.sendMessage(obtainMessage(0x03,model.msg));
                }
            }
        });
    }

    public void chengeUserPassword(String imei, String newpsd, String checkcode,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.chengeUserPassword(imei,user,newpsd,checkcode,new ResultCallBack<ChengeUserPasswordResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01);
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, ChengeUserPasswordResult model) {
                super.onSuccess(statusCode, headers, model);
                if(model.status.equals("ok")){
                    handler.sendEmptyMessage(0x02);
                }else{
                    handler.sendMessage(obtainMessage(0x03,model.msg));
                }
            }
        });
    }


    public void chengeDeviceInfo(final String devid, final String cstname,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.chengeDeviceInfo(user,devid,"smartbed","WIFI",cstname,new ResultCallBack<ChengeDeviceInfoResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01);//服务器异常或网络异常
            }


            @Override
            public void onSuccess(int statusCode, Headers headers, ChengeDeviceInfoResult model) {
                super.onSuccess(statusCode, headers, model);
                if(model.status.equals("ok")){
                    handler.sendEmptyMessage(0x02);//修改完成
                }else{
                    handler.sendMessage(obtainMessage(0x03,model.msg));
                }
            }
        });
    }


    public void disBindUserDevice(final String devid, final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.disBindUserDevice(user,devid,"WIFI",new ResultCallBack<disBindUserDeviceResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01);//服务器异常或网络异常
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, disBindUserDeviceResult model) {
                super.onSuccess(statusCode, headers, model);
                if(model.status.equals("ok")){
                    handler.sendEmptyMessage(0x04);//解绑成功
                }else{
                    handler.sendMessage(obtainMessage(0x05,null));
                }
            }
        });
    }


    public void changeUserInfo(String nickname,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.changeUserInfo(user,nickname,new ResultCallBack<UpdateUserInfoResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                handler.sendEmptyMessage(0x01);
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, UpdateUserInfoResult model) {
                if(model.status.equals("ok")){
                    handler.sendEmptyMessage(0x02);
                }else{
                    handler.sendMessage(obtainMessage(0x03,model));
                }
            }
        });
    }







    /**
     * 构造Message
     * @param what
     * @param obj
     * @return
     */
    private Message obtainMessage(int what,Object obj){
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        return message;
    }


    /**
     * log
     * @param msg
     */
    public void showL(String msg){
        if(BedApplication.isDebug){
            Log.i("20180822",msg);
        }
    }
}

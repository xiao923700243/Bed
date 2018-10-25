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

import com.xiao.wisdom.bed.BedApplication;
import com.xiao.wisdom.bed.bean.BindUserDeviceResult;
import com.xiao.wisdom.bed.bean.ChengeDeviceInfoResult;
import com.xiao.wisdom.bed.bean.ChengeUserPasswordResult;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.GetDevStatsResult;
import com.xiao.wisdom.bed.bean.GetUserAllDevStatsResult;
import com.xiao.wisdom.bed.bean.LoginUserResult;
import com.xiao.wisdom.bed.bean.RegisterResult;
import com.xiao.wisdom.bed.bean.SendCmdResult;
import com.xiao.wisdom.bed.bean.disBindUserDeviceResult;
import com.xiao.wisdom.bed.net.NetApi;
import com.xiao.wisdom.bed.net.base.ResultCallBack;
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
        if(!TextUtils.isEmpty(user)){
            NetApi.loginUser(user,password,new ResultCallBack<LoginUserResult>(){
                @Override
                public void onFailure(int statusCode, Request request, Exception e) {
                    handler.sendEmptyMessage(0x02);
                }

                @Override
                public void onSuccess(int statusCode, Headers headers, LoginUserResult model) {
                    if(model.status.equals("ok")){
                        handler.sendEmptyMessage(0x04);
                        ShareUtils.getInstance(context).saveUser(user,password); //保存登录信息
                    }else{
                        handler.sendEmptyMessage(0x02);
                    }
                }
            });
        }else{
            handler.sendEmptyMessage(0x02);
        }
    }

    public void loginUser(final Handler handler,final String user,final String password){
        NetApi.loginUser(user,password,new ResultCallBack<LoginUserResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                handler.sendEmptyMessage(0x01); //连接服务器异常
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, LoginUserResult model) {
                if(model.status.equals("ok")){
                    ShareUtils.getInstance(context).saveUser(user,password); //保存登录信息
                    handler.sendEmptyMessage(0x02); //登录成功
                }else{
                    handler.sendEmptyMessage(0x03); //登录失败
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

    public void bindUserDevice(final String devid,final String devname,final String devtype,final String cstname,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.bindUserDevice(user,devid,devname,devtype,cstname,new ResultCallBack<BindUserDeviceResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x03);// 绑定失败
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, BindUserDeviceResult model) {
                super.onSuccess(statusCode, headers, model);
                if(model.status.equals("ok")){
                    handler.sendEmptyMessage(0x01);// 绑定成功
                }else{
                    handler.sendMessage(obtainMessage(0x02,null));
                }
            }
        });
    }

    public void getUserAllDevStats(final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.getUserAllDevStats(user,new ResultCallBack<GetUserAllDevStatsResult>(){
            @Override
            public void onFailure(int statusCode, Request request, Exception e) {
                super.onFailure(statusCode, request, e);
                handler.sendEmptyMessage(0x01); //服务器或网络异常
            }

            @Override
            public void onSuccess(int statusCode, Headers headers, GetUserAllDevStatsResult model) {
                super.onSuccess(statusCode, headers, model);
                handler.sendMessage(obtainMessage(0x02,model));
                EventBus.getDefault().post(new Event.DevStateEvent(model));
            }
        });
    }

    public void sendDevCommend(String devid,String cmd,final Handler handler){
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


    public void registerUser(String imei,String user,String passowrd,String checkcode,final Handler handler){
        NetApi.registerUser(imei,user,passowrd,checkcode,new ResultCallBack<RegisterResult>(){
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


    public void chengeDeviceInfo(final String devid, final String devname, final String devtype, final String cstname,final Handler handler){
        showL("devid = "+devid);
        showL("devname = "+devname);
        showL("devtype = "+devtype);
        showL("cstname = "+cstname);
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.chengeDeviceInfo(user,devid,devname,devtype,cstname,new ResultCallBack<ChengeDeviceInfoResult>(){
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


    public void disBindUserDevice(final String devid, final String devtype,final Handler handler){
        String user = ShareUtils.getInstance(this).getUser();
        NetApi.disBindUserDevice(user,devid,devtype,new ResultCallBack<disBindUserDeviceResult>(){
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

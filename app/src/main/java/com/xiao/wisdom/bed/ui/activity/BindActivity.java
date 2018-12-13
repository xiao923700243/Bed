package com.xiao.wisdom.bed.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.view.BindAlert;
import com.xiao.wisdom.bed.view.ConfigErrorAlert;
import com.xiao.wisdom.bed.view.WaitDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.InputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;


/**
 * Created by Administrator on 2018/8/6.
 */

public class BindActivity extends BaseActivity {


    private EditText devide_devid,devide_cstname,wifi_ssid,wifi_password;
    private CheckBox lampBox;
    private Button nextButton;
    private String devid,cstname,wifissid,wifipassword;
    private BindAlert bindAlert;

    private WaitDialog messageDialog;
    private AutoConnectDeviceHotspotThread autoConnectDeviceHotspotThread;
    private ConfigDeviceApThread configDeviceApThread;
    private boolean needExBind = true;
    private ConfigErrorAlert errorAlert;

    private Runnable bindRunnable = new Runnable() {
        @Override
        public void run() {
            bedService.bindUserDevice(devid,cstname,mHandler,needExBind);
        }
    };

    @Override
    public int intiLayout() {
        return R.layout.activity_bind;
    }

    @Override
    public void initView() {
        devide_devid = findViewById(R.id.devide_devid);
        devide_cstname = findViewById(R.id.devide_cstname);
        wifi_ssid = findViewById(R.id.wifi_ssid);
        wifi_password = findViewById(R.id.wifi_password);
        lampBox = findViewById(R.id.lamp_box);
        nextButton = findViewById(R.id.next_);
    }

    @Override
    public void initData() {
        //显示设备序列号
        String code = getIntent().getExtras().getString("code");
        if(!TextUtils.isEmpty(code)){
            devide_devid.setText(code);
        }

        //显示WIFI名称
        if(BedUtils.isWifiConnected(this)){
            String ssid = BedUtils.getWifiSSID(this);
            if(ssid!=null && ssid.length()>0){
                wifi_ssid.setText(ssid.replace("\"",""));
            }
        }
        lampBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    nextButton.setBackgroundResource(R.drawable.button_background);
                    nextButton.setTextColor(Color.parseColor("#ffffff"));
                }else{
                    nextButton.setBackgroundResource(R.drawable.button_unavailable_background);
                    nextButton.setTextColor(Color.parseColor("#AAAAAA"));
                }
            }
        });
    }

    private void showDialogMessage(String msg){
        if(messageDialog == null){
            messageDialog = new WaitDialog(this,R.style.Dialog);
        }
        if(!messageDialog.isShowing()){
            messageDialog.show();
        }
        messageDialog.showMessage(msg);
    }

    private void dismissConfigDialog(){
        if(messageDialog!=null){
            messageDialog.dismiss();
        }
    }


    public void onNext(View v){
        if(!lampBox.isChecked()){
            return;
        }
        devid = devide_devid.getText().toString().trim();
        if(TextUtils.isEmpty(devid)){
            showToast(R.string.bind_activity_device_sn_input_error_msg);
            return ;
        }

        cstname = devide_cstname.getText().toString().trim();
        if(TextUtils.isEmpty(cstname)){
            showToast(R.string.bind_activity_device_nickname_input_error_msg);
            return;
        }
        cstname = BedUtils.getUtf8String(cstname)!=null?BedUtils.getUtf8String(cstname):cstname;

        wifissid = wifi_ssid.getText().toString().trim();
        if(TextUtils.isEmpty(wifissid)){
            showToast(R.string.ap_activity_ssid_error_msg);
            return;
        }

        wifipassword = wifi_password.getText().toString().trim();
        if(TextUtils.isEmpty(wifipassword)){
            showToast(R.string.ap_activity_password_error_msg);
            return;
        }
        showMessageAlert(); //确认框
    }
    private void showConfigErrorAlert(){
        if(errorAlert == null){
            errorAlert = new ConfigErrorAlert(this,R.style.Dialog);
        }
        errorAlert.show();
    }

    private void showMessageAlert(){
        if(bindAlert!=null){
            bindAlert.cancel();
            bindAlert = null;
        }
        if(bindAlert == null){
            bindAlert = new BindAlert(this,R.style.Dialog,new BindAlert.BindEvenet() {
                @Override
                public void onCancel() {
                    bindAlert.cancel();
                }
                @Override
                public void onConfirm() {
                    bindAlert.cancel();
                    showDialogMessage(getResString(R.string.bind_activity_bind_message_1));
                    //连接WIFI
                    if(autoConnectDeviceHotspotThread == null){
                        autoConnectDeviceHotspotThread = new AutoConnectDeviceHotspotThread();
                    }
                    autoConnectDeviceHotspotThread.start();
                }
            });
        }
        bindAlert.show();
    }

    @Override
    public void onMessage(Message msg) {
        switch (msg.what){
            case 0x01: {
                int arg1 = msg.arg1;
                if (arg1 == 1) {
                    //触发配送ap信息
                    configDeviceApThread = null;
                    configDeviceApThread = new ConfigDeviceApThread();
                    configDeviceApThread.start();
                }else if(arg1 == 2){
                    dismissConfigDialog();
                    //未查询到指定WIFI
                    showLongToast(R.string.ap_activity_no_search_wifi_msg);
                }else if(arg1 == 3){
                    dismissConfigDialog();
                    //连接失败，请手动尝试
                    showLongToast(R.string.ap_activity_connect_wifi_error);
                }else if(arg1 == 4){
                    dismissConfigDialog();
                    //配置失败，对话框提示
                    showConfigErrorAlert();
                }else if(arg1 == 5){
                    //执行绑定动作
                    mHandler.postDelayed(bindRunnable,1000);
                    mHandler.sendEmptyMessageDelayed(0x04,60*1000);
                }
            }break;
            case 0x02:
                showToast(R.string.bind_activity_bind_sunccess_msg);
                mHandler.removeCallbacks(bindRunnable);
                mHandler.removeMessages(0x04);
                dismissConfigDialog();
                this.finish();
                break;
            case 0x03:
                mHandler.postDelayed(bindRunnable,1000);
                break;
            case 0x04:
                dismissConfigDialog();
                //配置失败，请重试
                showConfigErrorAlert();
                break;
        }
    }

    public void onExit(View v){
        this.finish();
    }


    @Override
    protected void onDestroy() {
        needExBind = false;
        super.onDestroy();
    }

    public void onSwitchWifi(View v){
        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
    }

    private Message obtainArg1Message(int arg1){
        Message message = Message.obtain();
        message.what = 0x01;
        message.arg1 = arg1;
        return message;
    }

    public interface ReadMessageCallBack{
        void onMessage(String msg);
    }
    public class ReadThread extends Thread{
        private InputStream is;
        private byte[] buffer;
        private ReadMessageCallBack readMessageInterface;
        public ReadThread(InputStream is,ReadMessageCallBack readMessageInterface){
            this.is = is;
            this.readMessageInterface = readMessageInterface;
        }

        @Override
        public void run() {
            try{
                while (!isInterrupted() && is!=null){
                    buffer = new byte[is.available()];
                    if(is.read(buffer)>0){
                        this.readMessageInterface.onMessage(new String(buffer));
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public class ConfigDeviceApThread extends Thread implements ReadMessageCallBack{
        private Socket deviceSocket;
        private PrintWriter pw;
        private InputStream is;
        private ReadThread readThread;
        private Handler handler;
        private int ssidcount = 1;
        private int pwdcount = 1;

        @Override
        public void onMessage(String msg) {
            showL("收到消息："+msg);
            if(msg.trim().startsWith(BedUtils.RSP_SSID_NAME_END)){
                handler.removeMessages(0x01);
                pwdcount = 1;
                sendDeviceCMD(BedUtils.CMD_SEND_PASSWORD_END.replace("[password]",wifipassword));
                handler.sendEmptyMessageDelayed(0x02,3000);
            }else if(msg.trim().startsWith(BedUtils.RSP_PASSWORD_END) || msg.trim().startsWith(BedUtils.RSP_SSID_PASSWORD_CONFIG_END)){
                handler.removeCallbacksAndMessages(null);
                mHandler.sendMessage(obtainArg1Message(5));
                close();
            }
        }

        private void initHandler(){
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what){
                        case 0x01: {
                            showL("3s未收到消息：重新发送SSID配送");
                            if (ssidcount <= 3) {
                                sendDeviceCMD(BedUtils.CMD_SEND_SSID_NAME_END.replace("[ssid]", wifissid));
                                showL("发送SSID");
                                handler.sendEmptyMessageDelayed(0x01, 3000);
                                ssidcount++;
                            } else {
                                showL("3次未收到SSID配送成功消息：配送失败");
                                mHandler.sendMessage(obtainArg1Message(4));
                                close();
                            }
                        }break;
                        case 0x02: {
                            showL("3s未收到消息：重新发送PASSWORD配送");
                            if(pwdcount <= 3){
                                sendDeviceCMD(BedUtils.CMD_SEND_PASSWORD_END.replace("[password]",wifipassword));
                                showL("发送PASSWORD");
                                handler.sendEmptyMessageDelayed(0x02,3000);
                                pwdcount++;
                            } else {
                                showL("3次未收到消息：配送失败");
                                mHandler.sendMessage(obtainArg1Message(4));
                                close();
                            }
                        }break;
                    }
                }
            };
        }

        private void close(){
            try{
                if(readThread!=null){
                    readThread.interrupt();
                    readThread = null;
                }
                if(is!=null){
                    is.close();
                }
                if(pw!=null){
                    pw.close();
                }
                if(deviceSocket!=null){
                    deviceSocket.close();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private void sendDeviceCMD(String CMD){
            if(pw!=null){
                pw.write(CMD);
                pw.flush();
            }
        }

        @Override
        public void run() {
            try{
                Looper.prepare();
                initHandler();
                int count = 0;
                boolean isConnect = false;
                deviceSocket = new Socket();
                while(count<=3 && !isConnect){
                    try{
                        deviceSocket.connect(new InetSocketAddress(BedUtils.CONFIG_DEVICE_HOST,BedUtils.CONFIG_DEVICE_PORT),3000);
                        isConnect = true;
                    }catch (Exception e){
                        e.printStackTrace();
                        count++;
                    }
                }
                if(isConnect){
                    if(deviceSocket!=null){
                        try{
                            pw = new PrintWriter(deviceSocket.getOutputStream());
                            is = deviceSocket.getInputStream();
                        }catch (Exception e){
                            e.printStackTrace();
                            pw = null;
                            is = null;
                            mHandler.sendMessage(obtainArg1Message(4));
                        }
                    }
                    if(pw!=null && is!=null){
                        //启动读取消息线程

                        readThread = new ReadThread(is,this);
                        readThread.start();
                        ssidcount = 1;
                        sendDeviceCMD(BedUtils.CMD_SEND_SSID_NAME_END.replace("[ssid]",wifissid));
                        handler.sendEmptyMessageDelayed(0x01,3000);
                    }
                }else{
                    //配置失败，请重试
                    mHandler.sendMessage(obtainArg1Message(4));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Looper.loop();
        }
    }

    public class AutoConnectDeviceHotspotThread extends Thread{
        private WifiManager wifiMgr;
        public AutoConnectDeviceHotspotThread(){
            wifiMgr = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        }
        @Override
        public void run() {
            try{
                //打开
                if (!wifiMgr.isWifiEnabled()) {
                    wifiMgr.setWifiEnabled(true);
                    while (!wifiMgr.isWifiEnabled()) {
                        Thread.sleep(500);
                    }
                }
                //是否连接到指定WIFI
                if(!BedUtils.isConnectSSID(mContext)){
                    wifiMgr.startScan();
                    List<ScanResult> array;
                    boolean isWait = true;
                    long startTime = System.currentTimeMillis();
                    while(isWait && System.currentTimeMillis() - startTime<= 30*1000){
                        array = wifiMgr.getScanResults();
                        for(int i=0;i<array.size();i++){
                            if(array.get(i).SSID.replace("\"","").equals(BedUtils.DEVICE_SSID)){
                                isWait = false;
                                break;
                            }
                        }
                        Thread.sleep(200);
                    }
                    if(isWait){
                        //未查询到指定WIFI，请手动切换
                        mHandler.sendMessage(obtainArg1Message(2));
                    }else{
                        wifiMgr.enableNetwork(getNetWorkId(),true);
                        startTime = System.currentTimeMillis();
                        while(!isWait && System.currentTimeMillis() - startTime <=30*1000){
                            if(BedUtils.isConnectSSID(mContext)){
                                isWait = true;
                                Thread.sleep(500);
                            }
                        }
                        if(isWait){
                            //触发SSID&Password配送
                            mHandler.sendMessage(obtainArg1Message(1));
                        }else{
                            //未连接到指定WIFI，请手动切换
                            mHandler.sendMessage(obtainArg1Message(3));
                        }
                    }
                }else{
                    //触发SSID&Password配送
                    mHandler.sendMessage(obtainArg1Message(1));
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        private int getNetWorkId(){
            //判断是否已经连接过，如果已经连接，直接返回网络ID
            List<WifiConfiguration> configs = wifiMgr.getConfiguredNetworks();
            for (WifiConfiguration config : configs) {
                if (config.SSID.replace("\"","").equals(BedUtils.DEVICE_SSID)) {
                    return config.networkId;
                }
            }
            WifiConfiguration config = new WifiConfiguration();
            config.allowedAuthAlgorithms.clear();
            config.allowedGroupCiphers.clear();
            config.allowedKeyManagement.clear();
            config.allowedPairwiseCiphers.clear();
            config.allowedProtocols.clear();
            config.SSID = "\"" + BedUtils.DEVICE_SSID + "\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            int rid = wifiMgr.addNetwork(config);
            return rid;
        }
    }
}

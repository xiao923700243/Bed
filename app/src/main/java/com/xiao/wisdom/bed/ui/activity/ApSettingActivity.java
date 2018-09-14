package com.xiao.wisdom.bed.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.LoginUserResult;
import com.xiao.wisdom.bed.utils.ApSettingThread;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.utils.ShareUtils;
import com.xiao.wisdom.bed.view.WaitDialog;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

/**
 * Created by Administrator on 2018/8/7.
 */

public class ApSettingActivity extends BaseActivity {

    private EditText wifiSSid,wifiPassword;
    private CheckBox checkBox;
    private Button next;
    private String device_sn = "";
    private WaitDialog configDialog;
    private WaitDialog deployDialog;
    private DeployThread deployThread;

    @Override
    public int intiLayout() {
        return R.layout.activity_apsetting;
    }

    @Override
    public void initView() {
        wifiSSid = findViewById(R.id.wifi_ssid);
        wifiPassword = findViewById(R.id.wifi_password);
        checkBox = findViewById(R.id.button_state);
        next = findViewById(R.id.next_);
    }

    @Override
    public void initData() {
        device_sn = getIntent().getExtras().getString("devid");
        if(BedUtils.isWifiConnected(this)){
            String ssid = BedUtils.getWifiSSID(this);
            if(ssid!=null && ssid.length()>0){
                wifiSSid.setText(ssid.replace("\"",""));
            }
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    next.setBackgroundResource(R.drawable.button_background);
                    next.setTextColor(Color.parseColor("#ffffff"));
                    /*if(deployDialog == null){
                        deployDialog = new WaitDialog(ApSettingActivity.this,R.style.Dialog);
                    }
                    deployDialog.show();
                    deployDialog.showMessage(getResString(R.string.ap_activity_conn_msg));
                    deployThread = null;
                    if(deployThread == null){
                        deployThread = new DeployThread(ApSettingActivity.this);
                        deployThread.start();
                    }*/
                }else{
                    next.setBackgroundResource(R.drawable.button_unavailable_background);
                    next.setTextColor(Color.parseColor("#AAAAAA"));
                }
            }
        });

    }




    public void onSwitchWifi(View v){
        startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
    }

    public void onNext(View v){
        if(!checkBox.isChecked()){
            return;
        }
        String strSSid = wifiSSid.getText().toString().trim();
        if(TextUtils.isEmpty(strSSid)){
            showToast(R.string.ap_activity_ssid_error_msg);
            return;
        }
        String strPassword = wifiPassword.getText().toString().trim();
        if(TextUtils.isEmpty(strPassword)){
            showToast(R.string.ap_activity_password_error_msg);
            return;
        }
        if(!BedUtils.isWifiConnected(this)){
            showToast(R.string.ap_activity_ap_error_msg);
            return;
        }

        if(!BedUtils.isConnectSSID(this)){
            showToast(R.string.ap_activity_ap_error_msg);
            return;
        }

        if(configDialog == null){
            configDialog = new WaitDialog(this,R.style.Dialog);
        }
        configDialog.show();
        connectThread = null;
        if(connectThread == null){
            connectThread = new ConnectThread();
            connectThread.start();
        }
    }


    private void getDeviceState(int delay){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bedService.getDevStats(device_sn,mHandler);
            }
        },delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    public void onExit(View v){
        this.finish();
    }


    @Override
    public void onMessage(Message msg) {
        switch (msg.what){
            case 0x01:
                int count = (int) msg.obj;
                showConfigDialogMsg(getResString(R.string.ap_activity_device_connect_msg).replace("[_number]",count+""));
                break;
            case 0x02:
                showConfigDialogMsg(getResString(R.string.ap_activity_device_connect_success));
                //获取输入输出流
                if(getDatapipeline()){
                    //开启消息读取
                    if(readThread == null){
                        readThread = new ReadThread();
                        readThread.start();
                    }
                    //发送指令
                    ssidCount = 1;
                    showConfigDialogMsg(getResString(R.string.ap_activity_config_ssid_msg).replace("[_number]",ssidCount+""));
                    sendMsgDevice(BedUtils.CMD_SEND_SSID_NAME_END.replace("[ssid]",wifiSSid.getText().toString().trim()));
                    mHandler.sendEmptyMessageDelayed(0x05,3000);
                }else{ //输入输出流获取失败
                    dismissConfigDialog();
                    if(deviceConnect!=null){
                        try {
                            deviceConnect.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    showToast(R.string.ap_activity_device_connect_error_msg);
                }
                break;
            case 0x03:
                dismissConfigDialog();
                showToast(R.string.ap_activity_device_connect_error_msg);
                break;
            case 0x04: // 收到消息
                String result = (String) msg.obj;
                if(result.trim().startsWith(BedUtils.RSP_SSID_NAME_END)){
                    showConfigDialogMsg(getResString(R.string.ap_activity_config_ssid_success));
                    mHandler.removeMessages(0x05);
                    pwdCount = 1;
                    showConfigDialogMsg(getResString(R.string.ap_activity_config_password_msg).replace("[_number]",pwdCount+""));
                    sendMsgDevice(BedUtils.CMD_SEND_PASSWORD_END.replace("[password]",wifiPassword.getText().toString().trim()));
                    mHandler.sendEmptyMessageDelayed(0x06,3000);
                }else if(result.trim().startsWith(BedUtils.RSP_PASSWORD_END)){
                    mHandler.removeMessages(0x06);
                    showConfigDialogMsg(getResString(R.string.ap_activity_config_password_success));
                }else if(result.trim().startsWith(BedUtils.RSP_SSID_PASSWORD_CONFIG_END)){
                    showToast(R.string.ap_activity_config_wifi_success);
                    closeConnection();
                    showConfigDialogMsg(getResString(R.string.ap_activity_wait_device_online_msg));
                    getDeviceState(0);
                    mHandler.sendEmptyMessageDelayed(0x09,30*1000);
                }
                break;
            case 0x05:
                if(ssidCount<3){
                    ssidCount = ssidCount+1;
                    showConfigDialogMsg(getResString(R.string.ap_activity_config_ssid_number_msg).replace("[_number]",ssidCount+""));
                    sendMsgDevice(BedUtils.CMD_SEND_SSID_NAME_END.replace("[ssid]",wifiSSid.getText().toString().trim()));
                    mHandler.sendEmptyMessageDelayed(0x05,3000);
                }else{
                    dismissConfigDialog();
                    closeConnection();
                    showToast(R.string.ap_activity_config_ssid_error);
                }
                break;
            case 0x06:
                if(pwdCount<3){
                    pwdCount = pwdCount+1;
                    showConfigDialogMsg(getResString(R.string.ap_activity_config_password_number_msg).replace("[_number]",pwdCount+""));
                    sendMsgDevice(BedUtils.CMD_SEND_PASSWORD_END.replace("[password]",wifiPassword.getText().toString().trim()));
                    mHandler.sendEmptyMessageDelayed(0x06,3000);
                }else{
                    dismissConfigDialog();
                    closeConnection();
                    showToast(R.string.ap_activity_config_password_error);
                }
                break;
            case 0x07:
                getDeviceState(200);
                break;
            case 0x08:
                dismissConfigDialog();
                showToast(R.string.ap_activity_device_online_success);
                Event.BindEvent event = new Event.BindEvent();
                event.what = 0x01;
                EventBus.getDefault().post(event);
                this.finish();
                break;
            case 0x09:
                dismissConfigDialog();
                showToast(R.string.ap_activity_device_online_error);
                mHandler.removeCallbacksAndMessages(null);
                break;
            case 0x10:
                showDeployDialogMsg((String) msg.obj);
                break;
            case 0x11:
                dismissDeployDialog();
                showToast(R.string.ap_activity_no_search_wifi_msg);
                break;
            case 0x12:
                dismissDeployDialog();
                showToast(R.string.ap_activity_connect_wifi_success);
                break;
            case 0x13:
                dismissDeployDialog();
                showToast(R.string.ap_activity_connect_wifi_error);
                break;
        }
    }

    public class DeployThread extends Thread{
        private Context context;
        private WifiManager wifiMgr;
        private DeployThread(Context context){
            this.context = context;
        }
        @Override
        public void run() {
            try {
                //检测是否打开WIFI
                wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                //打开
                if (!wifiMgr.isWifiEnabled()) {
                    mHandler.sendMessage(obtainMessage(0x10, getResString(R.string.ap_activity_open_wifi_msg)));
                    wifiMgr.setWifiEnabled(true);
                    while (!wifiMgr.isWifiEnabled()) {
                        Thread.sleep(500);
                    }
                }
                //是否连接WIFI
                if(!BedUtils.isConnectSSID(context)){
                    wifiMgr.startScan();
                    List<ScanResult> array;
                    boolean isWait = true;
                    long startTime = System.currentTimeMillis();
                    mHandler.sendMessage(obtainMessage(0x10, getResString(R.string.ap_activity_search_wifi_msg)));
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
                        mHandler.sendMessage(obtainMessage(0x11, null)); //未查询到指定WIFI
                    }else{
                        //已经查找到指定WIFI
                        mHandler.sendMessage(obtainMessage(0x10, getResString(R.string.ap_activity_connect_wifi_msg)));
                        boolean enable = wifiMgr.enableNetwork(getNetWorkId(),true);
                        long ctime = System.currentTimeMillis();
                        boolean isC = false;
                        while(!isC && System.currentTimeMillis() - ctime <= 30 * 1000){ //小于30s
                            if(BedUtils.isConnectSSID(context)){
                                isC = true;
                                Thread.sleep(500);
                            }
                        }
                        if(isC){
                            mHandler.sendMessage(obtainMessage(0x12, null)); //连接指定WIFI成功
                        }else{
                            mHandler.sendMessage(obtainMessage(0x13, null)); //连接指定WIFI失败
                        }

                    }
                }else{
                    //已经连接到指定WIFI
                    mHandler.sendMessage(obtainMessage(0x12, null));  //连接指定WIFI成功
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

    private void showConfigDialogMsg(String msg){
        if(configDialog!=null){
            configDialog.showMessage(msg);
        }
    }

    private void dismissConfigDialog(){
        if(configDialog!=null){
            configDialog.dismiss();
        }
    }

    private void showDeployDialogMsg(String msg){
        if(deployDialog!=null){
            deployDialog.showMessage(msg);
        }
    }

    private void dismissDeployDialog(){
        if(deployDialog!=null){
            deployDialog.dismiss();
        }
    }

    private Message obtainMessage(int what,Object obj){
        Message message = Message.obtain();
        message.what = what;
        if(obj!=null){
            message.obj = obj;
        }
        return message;
    }

    private Socket deviceConnect;
    private PrintWriter pw;
    private InputStream is;
    private ConnectThread connectThread;
    private ReadThread readThread;
    private int ssidCount = 1;
    private int pwdCount = 1;

    private void closeConnection(){
        try {
            if (readThread != null) {
                readThread.stopRead();
                readThread=  null;
            }
            if(is!=null){
                is.close();
            }
            if(pw!=null){
                pw.close();
            }
            if(deviceConnect!=null){
                deviceConnect.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean getDatapipeline(){
        try {
            if (deviceConnect != null) {
                pw = new PrintWriter(deviceConnect.getOutputStream());
                is = deviceConnect.getInputStream();
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void sendMsgDevice(final String msg){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(pw!=null){
                    pw.write(msg);
                    pw.flush();
                }
            }
        }).start();
    }

    class ConnectThread extends Thread{
        private boolean available = false;
        private int count = 0;
        private String host = "192.168.1.168";//192.168.1.168
        private int port = 8080;
        @Override
        public void run() {
            deviceConnect = new Socket();
            while(!available && count<3){
                try{
                    //发送进度
                    mHandler.sendMessage(obtainMessage(0x01,count));
                    deviceConnect.connect(new InetSocketAddress(host,port),3000);
                    available = true;
                    showL("设备连接成功");
                }catch (Exception e){
                    available = false;
                    count++;
                }
            }

            if(available){
                mHandler.sendMessage(obtainMessage(0x02,null)); //连接成功
            }else{
                mHandler.sendMessage(obtainMessage(0x03,null)); //连接失败
            }
        }
    }

    class ReadThread extends Thread{
        private boolean isRead = true;
        private byte[] buffer;
        private int size;
        public void stopRead(){
            isRead = false;
        }
        @Override
        public void run() {
            while(isRead && is!=null){
                try {
                    buffer = new byte[is.available()];
                    size = is.read(buffer);
                    if(size>0){
                        showL("收到消息："+new String(buffer));
                        mHandler.sendMessage(obtainMessage(0x04,new String(buffer)));
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}

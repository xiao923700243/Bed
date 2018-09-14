package com.xiao.wisdom.bed.utils;

import android.os.Handler;
import android.util.Log;

import com.xiao.wisdom.bed.BedApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by Administrator on 2018/8/7.
 */

public class ApSettingThread extends Thread {
    private String ssid;
    private String password;
    private Handler handler;
    private String host = "192.168.1.168";
    private int port = 8080;
    private Socket socket;
    private boolean isRun = true;
    private int count = 0;

    private PrintWriter pw;
    private PrintStream ps;
    private BufferedReader br;

    public ApSettingThread(String sid, String pwd, Handler handler){
        this.ssid = sid;
        this.password = pwd;
        this.handler = handler;
        this.socket = new Socket();
    }

    @Override
    public void run() {
        super.run();
        while(isRun && count<3){
            try {
                socket.connect(new InetSocketAddress(host,port),3000);
                isRun = false;
                showL("socket连接成功");
            } catch (IOException e) {
                e.printStackTrace();
                showL("socket连接失败，继续循环等待");
                isRun = true;
                count++;
            }
        }

        if(isRun){
            showL("socket连接失败，循环等待结束，通知设置失败");
            handler.sendEmptyMessage(0x01); //不能连接到ip进行通讯
        }
        try {
            pw = new PrintWriter(socket.getOutputStream());
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sendSSID();
            sendPassword();
            pw.close();
            handler.sendEmptyMessage(0x02); //写入完成，等待设备连接Wifi后进行心跳传输
            showL("socket数据发送成功，通知等待设备上线");
        } catch (IOException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(0x01); //不能连接到ip进行通讯
            showL("socket连接失败，无法向目标发送数据");
        }
    }


    private void sendSSID(){
        if(pw!=null){
            pw.write("CMD_SSID_NAME_"+ssid+"_#*#*$OK");
            pw.flush();
        }
    }

    private void sendPassword(){
        if(pw!=null){
            pw.write("CMD_PASSWORD_"+password+"_#*#*$OK");
            pw.flush();
        }
    }



    private void showL(String msg){
        if(BedApplication.isDebug){
            Log.i("xiao_",msg);
        }
    }
}

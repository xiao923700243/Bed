package com.xiao.wisdom.bed.ui.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.view.BindAlert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.picker.SinglePicker;
import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by Administrator on 2018/8/6.
 */

public class BindActivity extends BaseActivity {

    private SinglePicker<String> devnamePicker;
    private SinglePicker<String> devtypePicker;
    private List<String> devnameOption;
    private List<String> devtypeOption;

    private EditText devide_devid,devide_cstname;
    private String devid,cstname,devname,devtype;
    private TextView devide_devname,devide_devtype;
    private BindAlert bindAlert;

    @Override
    public int intiLayout() {
        return R.layout.activity_bind;
    }

    @Override
    public void initView() {
        devide_devid = findViewById(R.id.devide_devid);
        devide_cstname = findViewById(R.id.devide_cstname);
        devide_devname = findViewById(R.id.devide_devname);
        devide_devtype = findViewById(R.id.devide_devtype);
    }

    @Override
    public void initData() {
        String code = getIntent().getExtras().getString("code");
        if(!TextUtils.isEmpty(code)){
            devide_devid.setText(code);
        }
        EventBus.getDefault().register(this);
    }

    public void onDeviceName(View v){
        if(devnamePicker == null){
            devnameOption = new ArrayList<>();
            devnameOption.add(getResString(R.string.bind_activity_smartbed_msg));
            devnameOption.add(getResString(R.string.bind_activity_smartlock_msg));
            devnameOption.add(getResString(R.string.bind_activity_smartcabinet_msg));
            devnamePicker = new SinglePicker<String>(this,devnameOption);
            devnamePicker.setCanceledOnTouchOutside(false);
            devnamePicker.setSelectedIndex(0);
            devnamePicker.setCycleDisable(true);
            devnamePicker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
                @Override
                public void onItemPicked(int index, String item) {
                    devide_devname.setText(item);
                }
            });
        }
        devnamePicker.show();
    }


    public void onDeviceType(View v){
        if(devtypePicker == null){
            devtypeOption = new ArrayList<>();
            devtypeOption.add(getResString(R.string.bind_xml_device_type_wifi_msg));
            devtypeOption.add(getResString(R.string.bind_xml_device_type_gps_msg));
            devtypePicker = new SinglePicker<String>(this,devtypeOption);
            devtypePicker.setCanceledOnTouchOutside(false);
            devtypePicker.setSelectedIndex(0);
            devtypePicker.setCycleDisable(true);
            devtypePicker.setOnItemPickListener(new SinglePicker.OnItemPickListener<String>() {
                @Override
                public void onItemPicked(int index, String item) {
                    devide_devtype.setText(item);
                }
            });
        }
        devtypePicker.show();
    }

    public void onNext(View v){
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
        devname = devide_devname.getText().toString().trim();
        if(TextUtils.isEmpty(devname)){
            showToast(R.string.bind_activity_device_name_input_error_msg);
            return;
        }
        devtype = devide_devtype.getText().toString().trim();
        if(TextUtils.isEmpty(devtype)){
            showToast(R.string.bind_activity_device_type_input_error_msg);
            return;
        }
        showMessageAlert(); //确认框
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
                    showWaitMsg(getResString(R.string.bind_activity_bind_device_msg));
                    bedService.bindUserDevice(devid,BedUtils.detailsToDevName(BindActivity.this,devname),devtype,cstname,mHandler);
                }
            });
        }
        bindAlert.show();
    }

    @Override
    public void onMessage(Message msg) {
        hideWait();
        switch (msg.what){
            case 0x01:
                //绑定成功
                showToast(R.string.bind_activity_device_config_wifi_msg);
                Bundle bundle = new Bundle();
                bundle.putString("devid",devid);
                startActivity(ApSettingActivity.class,bundle);
                break;
            case 0x02:
                //绑定失败，错误原因
                showToast(getResString(R.string.bind_activity_bind_error_msg));
                break;
            case 0x03:
                //绑定失败，网络异常或服务器异常
                if(isNetWork()){
                    showToast(R.string.public_service_error);
                }else{
                    showToast(R.string.public_network_error);
                }
                break;
        }
    }

    public void onExit(View v){
        this.finish();
    }


    @Subscribe
    public void onEvent(Event.BindEvent event){
        if(event.what == 0x01){
            //exit
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

package com.xiao.wisdom.bed.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.SendCmdResult;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.view.AutoAlert;
import com.xiao.wisdom.bed.view.ButtonMenu.BottomMenuFragment;
import com.xiao.wisdom.bed.view.ButtonMenu.MenuItem;
import com.xiao.wisdom.bed.view.ButtonMenu.MenuItemOnClickListener;
import com.xiao.wisdom.bed.view.InputAlert;
import com.xiao.wisdom.bed.view.PressureInputAlert;
import com.xiao.wisdom.bed.view.UnBindAlert;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.SinglePicker;

/**
 * Created by Administrator on 2018/8/20.
 */

public class BedDetailsActivity extends BaseActivity {
    private TextView devide_devid; //devide_devname,devide_devtype,
    private EditText devide_cstname;
    private ImageView bedImage;

    private String devid,cstname;

    private BottomMenuFragment buttonMenu;

    private UnBindAlert unBindAlert;
    private PressureInputAlert pressureInputAlert;
    private int bedImageCount = 0;


    private SinglePicker<String> devnamePicker;
    private SinglePicker<String> devtypePicker;
    private List<String> devnameOption;
    private List<String> devtypeOption;

    @Override
    public int intiLayout() {
        return R.layout.activity_beddetails;
    }

    @Override
    public void initView() {
        devide_devid = findViewById(R.id.devide_devid);
        devide_cstname = findViewById(R.id.devide_cstname);
        bedImage = findViewById(R.id.details_info_image);
    }

    @Override
    public void initData() {
        devid = getIntent().getExtras().getString("devid");
        devide_devid.setText(devid);
        cstname = getIntent().getExtras().getString("cstname");
        devide_cstname.setText(cstname);
        bedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bedImageCount++;
                mHandler.removeMessages(0x09);
                if(bedImageCount>=6){
                    bedImageCount = 0;
                    onCheckIdentity();
                }
                mHandler.sendEmptyMessageDelayed(0x09,1000);
            }
        });
    }

    public void onExit(View v){
        this.finish();
    }

/*
    public void onDeviceName(View v){
        if(devnamePicker == null){
            devnameOption = new ArrayList<>();
            devnameOption.add("智能床");
            devnameOption.add("智能锁");
            devnameOption.add("智能柜");
            devnamePicker = new SinglePicker<String>(this,devnameOption);
            devnamePicker.setCanceledOnTouchOutside(false);
            if(getViewText(R.id.devide_devname).equals("智能床")){
                devnamePicker.setSelectedIndex(0);
            }else if(getViewText(R.id.devide_devname).equals("智能锁")){
                devnamePicker.setSelectedIndex(1);
            }else if(getViewText(R.id.devide_devname).equals("智能柜")){
                devnamePicker.setSelectedIndex(2);
            }

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
            devtypeOption.add("WIFI");
            devtypeOption.add("GPS");
            devtypePicker = new SinglePicker<String>(this,devtypeOption);
            devtypePicker.setCanceledOnTouchOutside(false);
            if(getViewText(R.id.devide_devtype).equals("WIFI")){
                devtypePicker.setSelectedIndex(0);
            }else{
                devtypePicker.setSelectedIndex(1);
            }

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
    */

    private InputAlert inputAlert;
    public void onCheckIdentity(){
        if(inputAlert == null){
            inputAlert = new InputAlert(this, R.style.Dialog, new InputAlert.InputAlertEvent() {
                @Override
                public void onInputAlertEvent(int event) {
                    if(event == 0x01){
                        showToast(R.string.details_activity_input_null_error_msg);
                    }else if(event == 0x02){
                        showToast(R.string.details_activity_input_error_msg);
                    }else if(event == 0x03){
                        Bundle bundle = new Bundle();
                        bundle.putString("devid",devid);
                        startActivity(DebugActivity.class,bundle);
                    }
                }
            });
        }
        if(inputAlert!=null && !inputAlert.isShowing()){
            inputAlert.show();
        }
    }

    private void showPressureInputAlert(){
        if(pressureInputAlert == null){
            pressureInputAlert = new PressureInputAlert(mContext,R.style.Dialog,new PressureInputAlert.PressureInputEvent(){
                @Override
                public void onPressureInputEvent(int code) {
                    bedService.sendPressure(devid,code,mHandler);
                }
            });
        }
        if(pressureInputAlert!=null && !pressureInputAlert.isShowing()){
            pressureInputAlert.show();
        }
    }

    private BottomMenuFragment.onLastButtonCallBack lastButtonCallBack = new BottomMenuFragment.onLastButtonCallBack() {
        @Override
        public void onLastButtonCallBack() {
            showMessageAlert();
        }
    };

    public void onDebug(View v){
        if(buttonMenu == null){
            buttonMenu = new BottomMenuFragment();
            List<MenuItem> menuItemList = new ArrayList<MenuItem>();

            MenuItem menuItem1 = new MenuItem();
            menuItem1.setText(getResString(R.string.details_settings_menu_1_title));
            menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(buttonMenu,menuItem1) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    if(bedService == null){
                        return;
                    }
                    bedService.sendAutoCalibration(devid,mHandler);
                    buttonMenu.dismiss();
                }
            });

            MenuItem menuItem2 = new MenuItem();
            menuItem2.setText(getResString(R.string.details_settings_menu_2_title));
            menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(buttonMenu,menuItem2) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    if(bedService == null){
                        return;
                    }
                    bedService.sendConvert(devid,mHandler);
                    buttonMenu.dismiss();
                }
            });

            MenuItem menuItem3 = new MenuItem();
            menuItem3.setText(getResString(R.string.details_settings_menu_3_title));
            menuItem3.setMenuItemOnClickListener(new MenuItemOnClickListener(buttonMenu,menuItem3) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    if(bedService == null){
                        return;
                    }
                    showPressureInputAlert();
                    buttonMenu.dismiss();
                }
            });
            menuItemList.add(menuItem1);
            menuItemList.add(menuItem2);
            menuItemList.add(menuItem3);
            buttonMenu.setMenuItems(menuItemList);
            buttonMenu.setOnCallback(lastButtonCallBack);
        }
        buttonMenu.show(getFragmentManager(), "BottomMenuFragment");
    }


    private void showMessageAlert(){
        if(unBindAlert == null){
            unBindAlert = new UnBindAlert(this, R.style.Dialog, new UnBindAlert.UnBindEvent() {
                @Override
                public void onCancel() {
                    unBindAlert.dismiss();
                }

                @Override
                public void onConfirm() {
                    showWaitMsg(getResString(R.string.details_unbind_dislog_msg));
                    bedService.disBindUserDevice(devid,mHandler);
                    unBindAlert.dismiss();
                }
            });
        }
        unBindAlert.show();
    }

    private AutoAlert autoAlert;
    private void showAutoAlert(){
        if(autoAlert == null){
            autoAlert = new AutoAlert(mContext,R.style.Dialog);
        }
        if(autoAlert!=null && !autoAlert.isShowing()){
            autoAlert.show();
        }
    }

    public void onSave(View v){
        if(bedService == null){
            return;
        }
        if(TextUtils.isEmpty(getViewText(R.id.devide_cstname))){
            showToast(R.string.details_device_name_input_error_msg);
            return;
        }
        showWaitMsg(getResString(R.string.details_update_device_info_msg));
        String cstName = getViewText(R.id.devide_cstname);
        cstName = BedUtils.getUtf8String(cstName)!=null?BedUtils.getUtf8String(cstName):cstName;
        bedService.chengeDeviceInfo(devid,cstName,mHandler);
    }

    public void unBindDervice(View v){
        if(bedService == null){
            return;
        }
        showMessageAlert();
    }

    @Override
    public void onMessage(Message msg) {
        hideWait();
        switch (msg.what){
            case 0x01:
                if(isNetWork()){
                    showToast(R.string.public_service_error);
                }else{
                    showToast(R.string.public_network_error);
                }
                break;
            case 0x02:
                showToast(R.string.details_update_success_msg);
                this.finish();
                break;
            case 0x03:
                String result = (String) msg.obj;
                if(result.equals("用户不存在")){
                    showToast(R.string.details_update_account_unavailable_error_msg);
                }else if(result.equals("用户未绑定设备")){
                    showToast(R.string.details_update_device_unavailable_error_msg);
                }else{
                    showToast(getResString(R.string.details_update_error_msg));
                }
                break;
            case 0x04:
                showToast(R.string.details_unbind_success_msg);
                this.finish();
                break;
            case 0x05:
                showToast(getResString(R.string.details_unbind_error_msg));
                break;
            case 0x06: {
                SendCmdResult cmdResult = (SendCmdResult) msg.obj;
                if(cmdResult.status.equals("ok")){
                    showToast(R.string.details_send_auto_sunccess_msg);
                    showAutoAlert();
                }else if(cmdResult.msg.equals("用户与设备未绑定")){
                    showLongToast(R.string.details_send_auto_error_1_msg);
                }else{
                    showLongToast(R.string.details_send_auto_error_2_msg);
                }
            }break;
            case 0x07:{
                SendCmdResult cmdResult = (SendCmdResult) msg.obj;
                if(cmdResult.status.equals("ok")){
                    showToast(R.string.details_send_convert_sunccess_msg);
                }else if(cmdResult.msg.equals("用户与设备未绑定")){
                    showLongToast(R.string.details_send_convert_error_1_msg);
                }else{
                    showLongToast(R.string.details_send_convert_error_2_msg);
                }
            }break;
            case 0x08:{
                SendCmdResult cmdResult = (SendCmdResult) msg.obj;
                if(cmdResult.status.equals("ok")){
                    showToast(R.string.details_send_pressure_sunccess_msg);
                }else if(cmdResult.msg.equals("用户与设备未绑定")){
                    showLongToast(R.string.details_send_pressure_error_1_msg);
                }else{
                    showLongToast(R.string.details_send_pressure_error_2_msg);
                }
            }break;
            case 0x09:{
                bedImageCount = 0;
            }break;
        }
    }
}

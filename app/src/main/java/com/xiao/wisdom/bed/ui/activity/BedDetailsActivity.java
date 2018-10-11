package com.xiao.wisdom.bed.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.view.ButtonMenu.BottomMenuFragment;
import com.xiao.wisdom.bed.view.ButtonMenu.MenuItem;
import com.xiao.wisdom.bed.view.ButtonMenu.MenuItemOnClickListener;
import com.xiao.wisdom.bed.view.InputAlert;
import com.xiao.wisdom.bed.view.UnBindAlert;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.qqtheme.framework.picker.SinglePicker;

/**
 * Created by Administrator on 2018/8/20.
 */

public class BedDetailsActivity extends BaseActivity {
    private TextView devide_devid,device_temper,device_humidity; //devide_devname,devide_devtype,
    private EditText devide_cstname;

    private String devid,devname,devtype,temper,humidity,cstname;

    private BottomMenuFragment buttonMenu;

    private UnBindAlert unBindAlert;


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
        //devide_devname = findViewById(R.id.devide_devname);
        //devide_devtype = findViewById(R.id.devide_devtype);
        device_temper = findViewById(R.id.device_temper);
        device_humidity = findViewById(R.id.device_humidity);
        devide_cstname = findViewById(R.id.devide_cstname);
    }

    @Override
    public void initData() {
        devid = getIntent().getExtras().getString("devid");
        devide_devid.setText(devid);
        devname = getIntent().getExtras().getString("devname");
        //devide_devname.setText(BedUtils.devNameToDetails(devname));
        devtype = getIntent().getExtras().getString("devtype");
        //devide_devtype.setText(devtype);
        cstname = getIntent().getExtras().getString("cstname");
        devide_cstname.setText(cstname);
        temper =  getIntent().getExtras().getFloat("temper")+"";
        device_temper.setText(temper);
        humidity =  getIntent().getExtras().getFloat("humidity")+"";
        device_humidity.setText(humidity);
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
    public void onCheckIdentity(View v){
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

    public void onDebug(View v){
        if(buttonMenu == null){
            buttonMenu = new BottomMenuFragment();
            List<MenuItem> menuItemList = new ArrayList<MenuItem>();


            MenuItem menuItem1 = new MenuItem();
            menuItem1.setText(getResString(R.string.details_activity_auto_calibration_msg));
            menuItem1.setMenuItemOnClickListener(new MenuItemOnClickListener(buttonMenu,menuItem1) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    EventBus.getDefault().post(new Event.CmdEvent(devid, BedUtils.CMD_PRESSURE_AUTO_CALIBRATION));
                    buttonMenu.dismiss();
                }
            });

            MenuItem menuItem2 = new MenuItem();
            menuItem2.setText(getResString(R.string.details_activity_direction_convert_msg));
            menuItem2.setMenuItemOnClickListener(new MenuItemOnClickListener(buttonMenu,menuItem2) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    EventBus.getDefault().post(new Event.CmdEvent(devid, BedUtils.CMD_DIRECTION_CONVERT));
                    buttonMenu.dismiss();
                }
            });

            MenuItem menuItem3 = new MenuItem();
            menuItem3.setText(getResString(R.string.details_activity_life_time_start_msg));
            menuItem3.setMenuItemOnClickListener(new MenuItemOnClickListener(buttonMenu,menuItem3) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    EventBus.getDefault().post(new Event.CmdEvent(devid, BedUtils.CMD_LIFE_TIME_TEST_START));
                    buttonMenu.dismiss();
                }
            });

            MenuItem menuItem4 = new MenuItem();
            menuItem4.setText(getResString(R.string.details_activity_life_time_end_msg));
            menuItem4.setMenuItemOnClickListener(new MenuItemOnClickListener(buttonMenu,menuItem4) {
                @Override
                public void onClickMenuItem(View v, MenuItem menuItem) {
                    EventBus.getDefault().post(new Event.CmdEvent(devid, BedUtils.CMD_LIFE_TIME_TEST_FINISH));
                    buttonMenu.dismiss();
                }
            });
            menuItemList.add(menuItem1);
            menuItemList.add(menuItem2);
            menuItemList.add(menuItem3);
            menuItemList.add(menuItem4);
            buttonMenu.setMenuItems(menuItemList);
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
                    bedService.disBindUserDevice(devid,"WIFI",mHandler);
                    unBindAlert.dismiss();
                }
            });
        }
        unBindAlert.show();
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
        try{
            cstName = new String(cstName.getBytes("ISO-8859-1"),"utf-8");
        }catch (Exception e){
            e.printStackTrace();
        }
        String devName = BedUtils.detailsToDevName(BedDetailsActivity.this,"");
        String devType = "WIFI";//getViewText(R.id.devide_devtype);
        bedService.chengeDeviceInfo(devid,devName,devType,cstName,mHandler);
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
        }
    }
}

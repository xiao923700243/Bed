package com.xiao.wisdom.bed.ui.activity;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.GetUserAllDevStatsResult;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.view.AutoAlert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

/**
 * Created by Administrator on 2018/9/26.
 */

public class DebugActivity extends BaseActivity implements View.OnClickListener{
    private String devid = "";
    private int online = -1;

    private EditText pressure_value;
    private Button pressure_button;

    private EditText load_value;
    private Button load_button;

    private Button auto_button;

    private EditText frequency_value;
    private Button frequency_button;

    private EditText interval_value;
    private Button interval_button;

    private Button lifetime_start;
    private Button lifetime_end;
    private TextView lifetime_state;
    private TextView lifetime_count;
    private TextView lifetime_time;

    private Button convert_button;

    private long lefttestnum;
    private long lefttesttime;
    private int lefttestflag;


    @Override
    public int intiLayout() {
        return R.layout.activity_debug;
    }

    @Override
    public void initView() {
        pressure_value = findViewById(R.id.pressure_value);
        pressure_button =findViewById(R.id.pressure_button);
        pressure_button.setOnClickListener(this);
        load_value = findViewById(R.id.load_value);
        load_button = findViewById(R.id.load_button);
        load_button.setOnClickListener(this);
        auto_button = findViewById(R.id.auto_button);
        auto_button.setOnClickListener(this);
        frequency_value = findViewById(R.id.frequency_value);
        frequency_button = findViewById(R.id.frequency_button);
        frequency_button.setOnClickListener(this);
        interval_value = findViewById(R.id.interval_value);
        interval_button = findViewById(R.id.interval_button);
        interval_button.setOnClickListener(this);
        lifetime_start = findViewById(R.id.lifetime_start);
        lifetime_start.setOnClickListener(this);
        lifetime_end = findViewById(R.id.lifetime_end);
        lifetime_end.setOnClickListener(this);
        lifetime_state = findViewById(R.id.lifetime_state);
        lifetime_count = findViewById(R.id.lifetime_count);
        lifetime_time = findViewById(R.id.lifetime_time);
        convert_button = findViewById(R.id.convert_button);
        convert_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //压力值设置
            case R.id.pressure_button: {
                String pressure = pressure_value.getText().toString().trim();
                if (pressure == null || pressure.length() <= 0) {
                    showToast(R.string.debug_activity_pressure_error_1_msg);
                    return;
                }
                int inpressure = Integer.valueOf(pressure);
                if(inpressure<160 || inpressure>720){
                    pressure_value.setText("");
                    showToast(R.string.debug_activity_pressure_error_2_msg);
                    return;
                }
                String cmd = BedUtils.CMD_PRESSURE_SET.replace("[Pressure]",inpressure+"");
                sendDevCommend(cmd);
            }break;
            //负荷值设置
            case R.id.load_button:{
                String load = load_value.getText().toString().trim();
                if(load == null || load.length()<=0){
                    showToast(R.string.debug_activity_load_error_1_msg);
                    return;
                }
                int inload = Integer.valueOf(load);
                if(inload<1200 || inload>2400){
                    load_value.setText("");
                    showToast(R.string.debug_activity_load_error_2_msg);
                    return;
                }
                String cmd = BedUtils.CMD_LOAD_SET_PRESSURE.replace("[load]",inload+"");
                sendDevCommend(cmd);
            }break;
            //自动负荷校正
            case R.id.auto_button:{
                String cmd = BedUtils.CMD_AUTO_LOAD_CALIBRATION;
                if(online == 1){
                    showAutoAlert();
                }
                sendDevCommend(cmd);
            }break;
            //寿命次数设置
            case R.id.frequency_button:{
                String frequency = frequency_value.getText().toString().trim();
                if(frequency == null || frequency.length()<=0){
                    showToast(R.string.debug_activity_frequency_error_1_msg);
                    return;
                }
                int infrequency = Integer.valueOf(frequency);
                if(infrequency<0 || infrequency>10000){
                    frequency_value.setText("");
                    showToast(R.string.debug_activity_frequency_error_2_msg);
                    return;
                }
                String cmd = BedUtils.CMD_LIFE_TIME_TEST_INIT.replace("[count]",infrequency+"");
                sendDevCommend(cmd);
            }break;
            //寿命间隔时间配置
            case R.id.interval_button:{
                String interval = interval_value.getText().toString().trim();
                if(interval == null || interval.length()<=0){
                    showToast(R.string.debug_activity_interval_error_1_msg);
                    return;
                }
                int ininterval = Integer.valueOf(interval);
                if(ininterval<1 || ininterval>10){
                    interval_value.setText("");
                    showToast(R.string.debug_activity_interval_error_2_msg);
                    return;
                }
                String cmd = BedUtils.CMD_LIFE_TIME_TEST_INTERVAL.replace("[interval]",ininterval+"");
                sendDevCommend(cmd);
            }break;
            //寿命测试开始
            case R.id.lifetime_start:{
                String cmd = BedUtils.CMD_LIFE_TIME_TEST_START;
                sendDevCommend(cmd);
            }break;
            //寿命测试结束
            case R.id.lifetime_end:{
                String cmd = BedUtils.CMD_LIFE_TIME_TEST_FINISH;
                sendDevCommend(cmd);
            }break;
            //切换正反转
            case R.id.convert_button:{
                String cmd = BedUtils.CMD_DIRECTION_CONVERT;
                sendDevCommend(cmd);
            }break;
        }
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


    @Override
    public void initData() {
        showWaitMsg(getResString(R.string.home_activity_wait_user_info));
        devid = getIntent().getExtras().getString("devid");
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Event.DevStateEvent event){
        hideWait();
        if(event.data!=null && event.data.data!=null && event.data.data.size()>0){
            for(GetUserAllDevStatsResult.Device d:event.data.data){
                if(d.devid.equals(devid)){
                    online = d.online;
                    //lefttestnum,lefttesttime,lefttestflag
                    lefttestnum = d.lefttestnum;
                    lefttesttime = d.lefttestsec;
                    lefttestflag = d.lefttestflag;
                    mHandler.sendEmptyMessage(0x01);
                }
            }
        }
    }

    @Override
    public void onMessage(Message msg) {
        switch (msg.what){
            case 0x01:
                lifetime_state.setText(lefttestflag==0?getResString(R.string.debug_activity_life_test_status_2_msg):getResString(R.string.debug_activity_life_test_status_1_msg));
                lifetime_count.setText(lefttestnum+"");
                //lifetime_time.setText(formatSeconds(lefttesttime));
                lifetime_time.setText(lefttesttime+getResString(R.string.debug_activity_life_time_ms_msg));
                break;
        }
    }

    public static String formatSeconds(long seconds) {
        String timeStr = seconds + "秒";
        if (seconds > 60) {
            long second = seconds % 60;
            long min = seconds / 60;
            timeStr = min + "分" + second + "秒";
            if (min >= 60) {
                min = (seconds / 60) % 60;
                long hour = (seconds / 60) / 60;
                timeStr = hour + "小时" + min + "分" + second + "秒";
                if (hour >= 24) {
                    hour = ((seconds / 60) / 60) % 24;
                    long day = (((seconds / 60) / 60) / 24);
                    timeStr = day + "天" + hour + "小时" + min + "分" + second + "秒";
                    if(day >= 30){
                        day = (((seconds / 60) / 60) / 24) % 30;
                        long month = ((((seconds / 60) / 60) / 24) / 30);
                        timeStr =month+"月"+ day + "天" + hour + "小时" + min + "分" + second + "秒";
                        if(month >= 12){
                            month = ((((seconds / 60) / 60) / 24) / 30) %12;
                            long year = (((((seconds / 60) / 60) / 24) / 30) /12);
                            timeStr =year +"年"+month+"月"+ day + "天" + hour + "小时" + min + "分" + second + "秒";
                        }
                    }
                }
            }
        }
        return timeStr;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onExit(View v){
        this.finish();
    }



    private void sendDevCommend(String cmd){
        if(online == 0){
            showToast(R.string.home_activbity_send_error_off_line_msg);
            return;
        }
        if(bedService == null){
            return;
        }
        if(devid == null || devid.length()<=0){
            return;
        }
        bedService.sendDevCommend(devid,cmd,mHandler);
    }
}

package com.xiao.wisdom.bed.ui.activity;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.GetUserAllDevStatsResult;
import com.xiao.wisdom.bed.utils.BedUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

/**
 * Created by Administrator on 2018/9/26.
 */

public class DebugActivity extends BaseActivity {
    private String devid = "";
    private EditText pressureEdittext,lifetimeExittext;
    private int online = -1;
    private int lefttestflag = -1;
    @Override
    public int intiLayout() {
        return R.layout.activity_debug;
    }

    @Override
    public void initView() {
        pressureEdittext = findViewById(R.id.pressure_value_);
        lifetimeExittext = findViewById(R.id.lifetime_value_);
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
                    lefttestflag = d.lefttestflag;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void onExit(View v){
        this.finish();
    }

    public void onPressure(View v){
        if(bedService == null){
            return;
        }
        String pressureValue = pressureEdittext.getText().toString().trim();
        if(TextUtils.isEmpty(pressureValue)){ //空
            showToast(R.string.debug_activity_pressure_val_error1_msg);
            return;
        }
        if(pressureValue.length()>5){  //大于99999
            showToast(R.string.debug_activity_pressure_val_error2_msg);
            return;
        }
        sendDevCommend(BedUtils.CMD_PRESSURE_TEST.replace("[val]",pressureValue));
    }

    public void onDirectionSwitch(View v){
        if(bedService == null){
            return;
        }
        sendDevCommend(BedUtils.CMD_DIRECTION_CONVERT);
    }

    public void onStartLifeTime(View v){
        if(bedService == null){
            return;
        }
        if(lefttestflag == 1){
            showToast(R.string.debug_activity_lifetime_val_error3_msg);
            return;
        }
        sendDevCommend(BedUtils.CMD_LIFE_TIME_TEST_START);
    }

    public void onEndLifeTime(View v){
        if(bedService == null){
            return;
        }
        if(lefttestflag != 1){
            showToast(R.string.debug_activity_lifetime_val_error4_msg);
            return;
        }
        sendDevCommend(BedUtils.CMD_LIFE_TIME_TEST_FINISH);
    }

    public void onUpdateTime(View v){
        if(bedService == null){
            return;
        }
        String lifeTime = lifetimeExittext.getText().toString().trim();
        if(TextUtils.isEmpty(lifeTime)){
            showToast(R.string.debug_activity_lifetime_val_error1_msg);
            return;
        }
        if(lifeTime.length()>5){
            showToast(R.string.debug_activity_lifetime_val_error2_msg);
            return;
        }
        sendDevCommend(BedUtils.CMD_LIFE_TIME_VAL.replace("[val]",lifeTime));
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

    @Override
    public void onMessage(Message msg) {
        switch (msg.what){

        }
    }
}

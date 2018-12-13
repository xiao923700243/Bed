package com.xiao.wisdom.bed.ui.activity;

import android.content.Intent;
import android.os.Message;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;

/**
 * Created by Administrator on 2018/8/2.
 */

public class LogoActivity extends BaseActivity {
    @Override
    public int intiLayout() {
        return R.layout.activity_logo;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        bedService.init(mHandler);
        mHandler.sendEmptyMessageDelayed(0x01,10*1000);
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {
        mHandler.sendEmptyMessageDelayed(0x01,10*1000);

    }

    @Override
    public void onBind() {
        super.onBind();
        bedService.init(mHandler);
    }

    @Override
    public void onMessage(Message msg) {
        if(msg.what == 0x01){
            //跳转到登录界面
            startActivity(LoginActivity.class);
        }else if(msg.what == 0x02){
            //没有查询到登录记录，延时2S后，跳转到登录界面
            mHandler.removeMessages(0x01);
            mHandler.sendEmptyMessageDelayed(0x03,2*1000);
        }else if(msg.what == 0x03){
            //跳转到登录界面
            startActivity(LoginActivity.class);
            //startActivity(HomeActivity.class,true);
        }else if(msg.what == 0x04){
            //跳转到主界面
            mHandler.removeMessages(0x01);
            mHandler.sendEmptyMessageDelayed(0x05,2*1000);
        }else if(msg.what == 0x05){
            //跳转到主界面
            startActivity(HomeActivity.class);
        }
    }
}

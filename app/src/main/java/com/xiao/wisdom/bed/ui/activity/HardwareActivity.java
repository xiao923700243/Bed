package com.xiao.wisdom.bed.ui.activity;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;

/**
 * Created by Administrator on 2018/12/7.
 */

public class HardwareActivity extends BaseActivity {
    private ImageView mImage20,mImage21,mImage22,mImage23;

    @Override
    public int intiLayout() {
        return R.layout.activity_hardware;
    }

    @Override
    public void initView() {
        mImage20 = findViewById(R.id.page_20);
        mImage21 = findViewById(R.id.page_21);
        mImage22 = findViewById(R.id.page_22);
        mImage23 = findViewById(R.id.page_23);
    }

    @Override
    public void initData() {
        Glide.with(this).load(R.mipmap.page020).override(600,849).into(mImage20);
        Glide.with(this).load(R.mipmap.page021).override(600,849).into(mImage21);
        Glide.with(this).load(R.mipmap.page022).override(600,849).into(mImage22);
        Glide.with(this).load(R.mipmap.page023).override(600,849).into(mImage23);
    }

    public void onExit(View v){
        this.finish();
    }
}

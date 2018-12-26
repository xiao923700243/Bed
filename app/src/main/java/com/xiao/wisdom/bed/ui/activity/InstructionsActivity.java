package com.xiao.wisdom.bed.ui.activity;

import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;

/**
 * Created by Administrator on 2018/9/27.
 */

public class InstructionsActivity extends BaseActivity {
    private ImageView mImage1,mImage2,mImage3,mImage4,mImage5,mImage6,mImage7,mImage8,mImage9,mImage10;
    private ImageView mImage11,mImage12,mImage13,mImage14,mImage15,mImage16,mImage17,mImage18,mImage19;
    @Override
    public int intiLayout() {
        return R.layout.activity_instructions;
    }

    @Override
    public void initView() {
        mImage1 = findViewById(R.id.page_01);
        mImage2 = findViewById(R.id.page_02);
        mImage3 = findViewById(R.id.page_03);
        mImage4 = findViewById(R.id.page_04);
        mImage5 = findViewById(R.id.page_05);
        mImage6 = findViewById(R.id.page_06);
        mImage7 = findViewById(R.id.page_07);
        mImage8 = findViewById(R.id.page_08);
        mImage9 = findViewById(R.id.page_09);
        mImage10 = findViewById(R.id.page_10);
        mImage11 = findViewById(R.id.page_11);
        mImage12 = findViewById(R.id.page_12);
        mImage13 = findViewById(R.id.page_13);
        mImage14 = findViewById(R.id.page_14);
        mImage15 = findViewById(R.id.page_15);
        mImage16 = findViewById(R.id.page_16);
        mImage17 = findViewById(R.id.page_17);
        mImage18 = findViewById(R.id.page_18);
        mImage19 = findViewById(R.id.page_19);
    }

    @Override
    public void initData() {
        Glide.with(this).load(R.mipmap.page001).override(600,849).into(mImage1);
        Glide.with(this).load(R.mipmap.page002).override(600,849).into(mImage2);
        Glide.with(this).load(R.mipmap.page003).override(600,849).into(mImage3);
        Glide.with(this).load(R.mipmap.page004).override(600,849).into(mImage4);
        Glide.with(this).load(R.mipmap.page005).override(600,849).into(mImage5);
        Glide.with(this).load(R.mipmap.page006).override(600,849).into(mImage6);
        Glide.with(this).load(R.mipmap.page007).override(600,849).into(mImage7);
        Glide.with(this).load(R.mipmap.page008).override(600,849).into(mImage8);
        Glide.with(this).load(R.mipmap.page009).override(600,849).into(mImage9);
        Glide.with(this).load(R.mipmap.page010).override(600,849).into(mImage10);
        Glide.with(this).load(R.mipmap.page011).override(600,849).into(mImage11);
        Glide.with(this).load(R.mipmap.page012).override(600,849).into(mImage12);
        Glide.with(this).load(R.mipmap.page013).override(600,849).into(mImage13);
        Glide.with(this).load(R.mipmap.page014).override(600,849).into(mImage14);
        Glide.with(this).load(R.mipmap.page015).override(600,849).into(mImage15);
        Glide.with(this).load(R.mipmap.page016).override(600,849).into(mImage16);
        Glide.with(this).load(R.mipmap.page017).override(600,849).into(mImage17);
        Glide.with(this).load(R.mipmap.page018).override(600,849).into(mImage18);
        Glide.with(this).load(R.mipmap.page019).override(600,849).into(mImage19);
    }

    public void onExit(View v){
        this.finish();
    }


}

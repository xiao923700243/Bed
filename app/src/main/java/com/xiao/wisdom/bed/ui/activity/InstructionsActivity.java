package com.xiao.wisdom.bed.ui.activity;

import android.view.View;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;

/**
 * Created by Administrator on 2018/9/27.
 */

public class InstructionsActivity extends BaseActivity {
    @Override
    public int intiLayout() {
        return R.layout.activity_instructions;
    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    public void onExit(View v){
        this.finish();
    }


}

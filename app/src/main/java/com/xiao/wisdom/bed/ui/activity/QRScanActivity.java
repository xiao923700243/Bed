package com.xiao.wisdom.bed.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.utils.SoundPlayUtils;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;

/**
 * Created by Administrator on 2018/8/6.
 */

public class QRScanActivity extends BaseActivity implements QRCodeView.Delegate{

    private ZBarView mZBarView;
    @Override
    public int intiLayout() {
        return R.layout.activity_qrscan;
    }

    @Override
    public void initView() {
        mZBarView = findViewById(R.id.zbarview);
    }

    @Override
    public void initData() {
        SoundPlayUtils.init(this);
        mZBarView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZBarView.startCamera();
        mZBarView.startSpotAndShowRect();
    }

    @Override
    protected void onStop() {
        mZBarView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZBarView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        showToast(getResString(R.string.scan_activity_code_content_msg)+result);
        SoundPlayUtils.play();
        if(result!=null && result.length()>0){
            Bundle bundle = new Bundle();
            bundle.putString("code",result);
            startActivity(BindActivity.class,bundle,true);
        }else{
            showToast(getResString(R.string.scan_activity_code_error_msg));
        }
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        showToast(R.string.scan_activity_code_permission_error_msg);
    }
}

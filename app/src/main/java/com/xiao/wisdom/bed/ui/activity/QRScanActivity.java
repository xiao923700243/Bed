package com.xiao.wisdom.bed.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

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
    private String data;
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
    public void onMessage(Message msg) {
        hideWait();
        switch (msg.what){
            case 0x01:
                //网络异常
                if(isNetWork()){
                    showToast(R.string.public_service_error);
                }else{
                    showToast(R.string.public_network_error);
                }
                break;
            case 0x02:
                showToast(R.string.scan_activity_error_1_msg); //请重新扫描
                break;
            case 0x03:
                if(data!=null && data.length()>0){
                    Bundle bundle = new Bundle();
                    bundle.putString("code",data);
                    startActivity(BindActivity.class,bundle,true);
                }else{
                    showToast(getResString(R.string.scan_activity_code_error_msg));
                }
                break;
            case 0x04:
                showToast(R.string.scan_activity_error_4_msg); //已经绑定改设备
                break;
        }
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        //showToast(getResString(R.string.scan_activity_code_content_msg)+result);
        if(bedService == null){
            return;
        }
        data = result;
        SoundPlayUtils.play();
        showWaitMsg(getResString(R.string.scan_activity_check_msg));
        bedService.checkDeviceBind(result.trim(),mHandler);
        mZBarView.startSpot();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        showToast(R.string.scan_activity_code_permission_error_msg);
    }
}

package com.xiao.wisdom.bed.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.xiao.wisdom.bed.BedApplication;
import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.net.NetApi;
import com.xiao.wisdom.bed.service.BedService;
import com.xiao.wisdom.bed.utils.BedUtils;

/**
 * Created by Administrator on 2018/8/2.
 */

public abstract class BaseActivity extends Activity {

    /**Handler消息传递**/
    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            onMessage(msg);
        }
    };
    protected BedService bedService;
    private ServiceConnection connection = new ServiceConnection(){
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BedService.BedBinder binder = (BedService.BedBinder) iBinder;
            bedService = binder.getService();
            onBind();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bedService = null;
        }
    };

    private Dialog waitDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置布局
        setContentView(intiLayout());
        //bind服务类
        bindBedService();
        //初始化控件
        initView();
        //设置数据
        initData();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bedService!=null){
            unbindService(connection);
        }
        if(waitDialog!=null && waitDialog.isShowing()){
            hideWait();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    private void bindBedService(){
        Intent i = new Intent(this,BedService.class);
        bindService(i,connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 设置布局
     *
     * @return
     */
    public abstract int intiLayout();

    /**
     * 初始化布局
     */
    public abstract void initView();

    /**
     * 设置数据
     */
    public abstract void initData();

    /**
     * log
     * @param msg
     */
    public void showL(String msg){
        if(BedApplication.isDebug){
            Log.i("xiao_",msg);
        }
    }

    public String getResString(int id){
        return getResources().getString(id);
    }

    /**
     * Toast
     * @param msg
     */
    public void showToast(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this,msg,Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Toast
     * @param id 字符串id
     */
    public void showToast(final int id){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this,getResString(id),Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * 启动Activity带数据
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 启动Activity带数据
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle,boolean exit) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if(exit){
            this.finish();
        }
    }


    /**
     * 启动Activity
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        startActivity(intent);
    }

    /**
     * 启动Activity
     * @param clz
     * @param exit
     */
    public void startActivity(Class<?> clz,boolean exit) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        startActivity(intent);
        if(exit){
            this.finish();
        }
    }

    /**
     * 判断网络
     * @return
     */
    public boolean isNetWork(){
        return BedUtils.isNetworkAvailable(this);
    }

    /**
     * 隐藏软件盘
     */
    public void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * 显示软键盘
     */
    public void showInputMethod(){
        if (getCurrentFocus() != null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(getCurrentFocus().getWindowToken(),0);
        }
    }

    /**
     * 显示等待圆形进度条
     * @param msg
     */
    public void showWaitMsg(String msg){
        if(waitDialog == null){
            waitDialog = new Dialog(this, R.style.image_dialog);
            waitDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View main = View.inflate(this, R.layout.dialog_wait, null);
            waitDialog.setContentView(main);
            TextView tv = (TextView) main.findViewById(R.id.msg);
            tv.setText(msg);
            waitDialog.setCancelable(false);
            waitDialog.setCanceledOnTouchOutside(false);
        }
        if (waitDialog != null && !waitDialog.isShowing()) {
            waitDialog.show();
        }
    }

    /**
     * 隐藏等待进度条
     */
    public void hideWait(){
        if (waitDialog != null && waitDialog.isShowing()) {
            waitDialog.dismiss();
            waitDialog = null;
        }
    }

    /**
     * 加载验证码
     * @param view
     */
    public void loadCheckCode(ImageView view){
        String url = NetApi.Service_Address + NetApi.GetUserCheckCode_Address+"?imei="+BedUtils.getImei(this);
        Glide.with(this)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.NONE )//禁用磁盘缓存
                .skipMemoryCache(true )//跳过内存缓存
                .into(view);
    }

    public String getViewText(int viewId){
        return ((TextView)findViewById(viewId)).getText().toString().trim();
    }

    public void onMessage(Message msg){

    }

    public void onBind(){

    }

}

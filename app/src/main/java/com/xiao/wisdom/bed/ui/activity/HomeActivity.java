package com.xiao.wisdom.bed.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.xiao.wisdom.bed.BedApplication;
import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.adapter.HomeAdapter;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.GetUserAllDevStatsResult;
import com.xiao.wisdom.bed.utils.ShareUtils;
import com.xiao.wisdom.bed.view.ConfigErrorAlert;
import com.xiao.wisdom.bed.view.ExitAlert;
import com.xiao.wisdom.bed.view.ScrollViewListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2018/8/3.
 */

public class HomeActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks,NavigationView.OnNavigationItemSelectedListener{
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private HomeAdapter homeAdapter;
    private ScrollViewListView listView;
    private TextView errorTextView;
    private boolean isInitData = true;
    private int delayTime = 500;
    private ExitAlert exitAlert;
    private TextView userName;


    @Override
    public int intiLayout() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {
        drawerLayout = findViewById(R.id.drawerLayout_);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        listView = findViewById(R.id.list_view);
        userName = navigationView.getHeaderView(0).findViewById(R.id.user_);
        errorTextView = findViewById(R.id.home_error_text);
    }


    public void onDrawer(View v){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);
        userName.setText(ShareUtils.getInstance(this).getUser());
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(exitAlert != null){
            exitAlert.dismiss();;
            exitAlert = null;
        }
        super.onDestroy();

    }


    public void onQRScanActivity(View v){
        startActivity(QRScanActivity.class,false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        RequestPermissions();
    }

    @Override
    public void onBind() {
        showWaitMsg(getResString(R.string.home_activity_wait_user_info));
        isInitData = true;
        bedService.getUserAllDevStats(mHandler);
    }

    @Override
    public void onMessage(Message msg) {
        switch (msg.what){
            case 0x01:
                if(isInitData){
                    hideWait();
                    isInitData = false;
                    if(isNetWork()){
                        showToast(R.string.public_service_error);
                    }else{
                        showToast(R.string.public_network_error);
                    }
                }else{
                    mHandler.sendEmptyMessageDelayed(0x04,delayTime);
                }
                break;
            case 0x02:
                GetUserAllDevStatsResult model = (GetUserAllDevStatsResult) msg.obj;
                if(model == null || model.data == null || model.data.size()<=0){
                    errorTextView.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }else{
                    errorTextView.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                }
                if(homeAdapter == null){
                    hideWait();
                    isInitData = false;
                    homeAdapter = new HomeAdapter(this,model);
                    listView.setAdapter(homeAdapter);
                }else{
                    homeAdapter.setData(model);
                }
                mHandler.sendEmptyMessageDelayed(0x04,delayTime);
                break;
            case 0x03:
                if(!isInitData){
                    mHandler.sendEmptyMessageDelayed(0x04,delayTime);
                }else{
                    hideWait();
                    isInitData = false;
                }
                break;
            case 0x04:
                bedService.getUserAllDevStats(mHandler);
                break;
            case 0x05:
                if(isNetWork()){
                    showToast(R.string.public_service_error);
                }else{
                    showToast(R.string.public_network_error);
                }
                break;
            case 0x06:
                //showToast(R.string.home_activity_send_error_msg);
                String result = (String) msg.obj;
                if(result.equals("用户与设备未绑定")){
                    showToast(R.string.home_activbity_send_error_unbind_msg);
                }else{
                    showToast(R.string.home_activbity_send_error_off_line_msg);
                }
                break;
        }
    }

    @Subscribe
    public void onEvent(Event.CmdEvent event){
        if(bedService!=null){
            bedService.sendDevCommend(event.devid,event.cmd,mHandler);
        }
    }

    private void RequestPermissions(){
        String[] perms = {Manifest.permission.INTERNET,Manifest.permission.CAMERA
                        , Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.ACCESS_NETWORK_STATE
                        ,Manifest.permission.ACCESS_WIFI_STATE
                        ,Manifest.permission.CHANGE_WIFI_STATE
                        ,Manifest.permission.ACCESS_COARSE_LOCATION
                        ,Manifest.permission.ACCESS_FINE_LOCATION};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, getResString(R.string.home_activity_apply_permission_msg), 0x01, perms);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).setTitle(getResString(R.string.home_activity_permission_error)).build().show();
        }else{
            RequestPermissions();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_item1: //重置密码
                startActivity(ChangePwdActivity.class);
                break;
            case R.id.nav_item2: //App使用教程
                startActivity(InstructionsActivity.class);
                break;
            case R.id.nav_item3: //退出登录
                if(exitAlert !=null){
                    exitAlert.cancel();
                    exitAlert = null;
                }
                if(exitAlert == null){
                    exitAlert = new ExitAlert(this, R.style.Dialog, new ExitAlert.ExitEvent() {
                        @Override
                        public void onCancel() {
                            exitAlert.cancel();
                        }

                        @Override
                        public void onConfirm() {
                            ShareUtils.getInstance(HomeActivity.this).cleanUser();
                            startActivity(LoginActivity.class,true);
                            exitAlert.dismiss();
                        }
                    });
                }
                exitAlert.show();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            //super.onBackPressed();
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
            intent.addCategory(Intent.CATEGORY_HOME);
            this.startActivity(intent);
        }
    }
}

package com.xiao.wisdom.bed.ui.activity;

import android.Manifest;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;


/**
 * Created by Administrator on 2018/8/3.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText userEdit,passwordEdit;
    private Button loginBtn;
    private TextView registerText,updateText;

    @Override
    public int intiLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        userEdit = findViewById(R.id.user_);
        passwordEdit = findViewById(R.id.password_);
        loginBtn = findViewById(R.id.login_);
        registerText = findViewById(R.id.register_);
        //updateText = findViewById(R.id.update_);

    }

    @Override
    public void initData() {

    }

    @Override
    public void onBind() {
        super.onBind();
        registerText.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        //updateText.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(bedService == null){
            return;
        }
        switch (view.getId()){
            case R.id.login_:
                String userStr = userEdit.getText().toString().trim();
                String passwordStr = passwordEdit.getText().toString().trim();
                if(TextUtils.isEmpty(userStr) || userStr.length()<6 || userStr.length()>12){
                    showToast(R.string.login_activity_toast_user_error);
                    return;
                }
                if(TextUtils.isEmpty(passwordStr) || passwordStr.length()<6 || passwordStr.length()>12){
                    showToast(R.string.login_activity_toast_password_error);
                    return;
                }

                showWaitMsg(getResString(R.string.login_activity_dialog_msg_login));
                bedService.loginUser(mHandler,userStr,passwordStr);
                break;
            case R.id.register_:
                startActivity(RegisterActivity.class);
                break;
            /**case R.id.update_:
                break;*/
        }
    }

    @Override
    public void onMessage(Message msg) {
        hideWait();
        switch (msg.what){
            case 0x01:
                if(isNetWork()){
                    showToast(R.string.public_service_error);
                }else{
                    showToast(R.string.public_network_error);
                }
                break;
            case 0x2:
                showToast(R.string.login_activity_toast_login_success);
                startActivity(HomeActivity.class,true);
                break;
            case 0x03:
                showToast(R.string.login_activity_toast_login_error);
                break;
        }

    }



}

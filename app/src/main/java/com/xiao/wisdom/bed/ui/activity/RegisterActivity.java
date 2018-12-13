package com.xiao.wisdom.bed.ui.activity;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.utils.BedUtils;

/**
 * Created by Administrator on 2018/8/10.
 */

public class RegisterActivity extends BaseActivity {
    private EditText userEdit;
    private EditText nickname;
    private EditText pwdEdit;
    private EditText checkEdit;
    private ImageView checkImage;


    @Override
    public int intiLayout() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        userEdit = findViewById(R.id.user_);
        pwdEdit = findViewById(R.id.password_);
        checkEdit = findViewById(R.id.check_edit);
        checkImage = findViewById(R.id.check_image);
        nickname = findViewById(R.id.nickname_);
    }

    @Override
    public void initData() {
        loadCheckCode(checkImage);
    }

    public void onExit(View v){
        this.finish();
    }

    public void onRegister(View v){
        if(bedService == null){
            return;
        }
        String strUser = userEdit.getText().toString().trim();
        if(strUser == null || strUser.length()<6 || strUser.length()>12){
            showToast(R.string.register_activity_account_input_error_msg);
           return;
        }
        String strNickName = nickname.getText().toString().trim();
        if(strNickName == null || strNickName.length()<=0 || strNickName.length()>12){
            showToast(R.string.register_activity_nickname_error_msg);
            return;
        }
        strNickName = BedUtils.getUtf8String(strNickName);
        String strPwd = pwdEdit.getText().toString().trim();
        if(strPwd == null || strPwd.length()<6 || strPwd.length()>12){
            showToast(R.string.register_activity_password_input_error_msg);
            return;
        }
        String strCheck = checkEdit.getText().toString().trim();
        if(TextUtils.isEmpty(strCheck)){
            showToast(R.string.register_activity_checkcode_input_error_msg);
            return;
        }

        showWaitMsg(getResString(R.string.register_activity_register_msg));
        bedService.registerUser(BedUtils.getImei(this),strUser,strPwd,strCheck,strNickName,mHandler);

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
            case 0x02:
                showToast(R.string.register_activity_success_msg);
                this.finish();
                break;
            case 0x03:
                String result = (String) msg.obj;
                if(result.equals("用户已注册")){
                    showToast(R.string.register_activity_re_register_error_msg);
                }else if(result.equals("验证码不匹配或已失效")){
                    showToast(R.string.register_activity_checkcode_unavailable_error_msg);
                }else{
                    showToast(R.string.register_activity_error_msg);
                }
                break;
        }
    }

    public void onRefreshCode(View v){
        loadCheckCode(checkImage);
    }
}

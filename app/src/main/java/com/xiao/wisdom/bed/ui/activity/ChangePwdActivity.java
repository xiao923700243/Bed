package com.xiao.wisdom.bed.ui.activity;

import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.utils.ShareUtils;

/**
 * Created by Administrator on 2018/8/13.
 */

public class ChangePwdActivity extends BaseActivity {
    private EditText newPwdEdit;
    private EditText rePwdEdit;
    private EditText codeEdit;
    private ImageView codeImage;

    private String newpwd;



    @Override
    public int intiLayout() {
        return R.layout.activity_changepwd;
    }

    @Override
    public void initView() {
        newPwdEdit = findViewById(R.id.password_new);
        rePwdEdit = findViewById(R.id.password_re);
        codeEdit = findViewById(R.id.check_edit);
        codeImage = findViewById(R.id.check_image);
    }

    @Override
    public void initData() {
        loadCheckCode(codeImage);
    }

    public void onExit(View v){
        this.finish();
    }


    public void onChange(View v){
        if(bedService == null){
            return;
        }
        newpwd = newPwdEdit.getText().toString().trim();
        if(newpwd == null || newpwd.length()<6 || newpwd.length()>12){
            showToast(R.string.change_activity_password_input_error_msg);
            return;
        }
        String repwd = rePwdEdit.getText().toString().trim();
        if(repwd == null || !repwd.equals(newpwd)){
            showToast(R.string.change_activity_re_password_input_error_msg);
            return;
        }
        String code = codeEdit.getText().toString().trim();
        if(TextUtils.isEmpty(code)){
            showToast(R.string.change_activity_checkcode_input_error_msg);
            return;
        }
        showWaitMsg(getResString(R.string.change_activity_submit_data_msg));
        bedService.chengeUserPassword(BedUtils.getImei(this),newpwd,code,mHandler);
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
                showToast(R.string.change_activity_update_success_msg);
                //changeData(newpwd);
                this.finish();
                break;
            case 0x03:
                String result = (String) msg.obj;
                if(result.equals("用户未注册")){
                    showToast(R.string.change_activity_update_account_unavailable_error_msg);
                }else if(result.equals("验证码不匹配或已失效")){
                    showToast(R.string.change_activity_update_checkcode_unavailable_error_msg);
                }else{
                    showToast(R.string.change_activity_update_error_msg);
                }
                break;
        }
    }

    private void changeData(String pwd){
        String user = ShareUtils.getInstance(this).getUser();
        String password = pwd;
        String nickname = ShareUtils.getInstance(this).getNickname();
        ShareUtils.getInstance(this).saveUser(user,password,nickname);
    }

    public void onRefreshCode(View v){
        loadCheckCode(codeImage);
    }
}

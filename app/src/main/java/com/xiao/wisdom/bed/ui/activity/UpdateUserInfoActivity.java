package com.xiao.wisdom.bed.ui.activity;

import android.os.Message;
import android.view.View;
import android.widget.EditText;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.utils.ShareUtils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2018/12/4.
 */

public class UpdateUserInfoActivity extends BaseActivity {

    public EditText nickName;
    @Override
    public int intiLayout() {
        return R.layout.activity_updateuserinfo;
    }

    @Override
    public void initView() {
        nickName = findViewById(R.id.nickname_new);
    }

    @Override
    public void initData() {
        nickName.setText(getNickName());
    }

    public void onExit(View v){
        this.finish();
    }

    public void onUpdate(View v){
        if(bedService == null){
            return;
        }
        String strname = nickName.getText().toString().trim();
        if(strname == null || strname.length()<=0 || strname.length()>12){
            showToast(R.string.register_activity_nickname_error_msg);
            return;
        }
        if(strname.equals(getNickName())){
            showToast(R.string.update_userinfo_activity_success_msg);
            this.finish();
        }
        strname = BedUtils.getUtf8String(strname);
        showWaitMsg(getResString(R.string.scan_activity_check_msg));
        bedService.changeUserInfo(strname,mHandler);
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
                String user = ShareUtils.getInstance(mContext).getUser();
                String password = ShareUtils.getInstance(mContext).getPassword();
                ShareUtils.getInstance(mContext).saveUser(user,password,nickName.getText().toString().trim());
                showToast(R.string.update_userinfo_activity_success_msg);
                EventBus.getDefault().post(new Event.UpdateNickNameEvent());
                this.finish();
                break;
            case 0x03:
                break;
        }
    }

    public String getNickName(){
        String strname = ShareUtils.getInstance(mContext).getNickname();
        if(strname == null || strname.length()<=0){
            strname = ShareUtils.getInstance(mContext).getUser();
        }
        return strname;
    }


}

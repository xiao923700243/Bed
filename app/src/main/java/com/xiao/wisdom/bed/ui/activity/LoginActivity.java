package com.xiao.wisdom.bed.ui.activity;

import android.Manifest;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.adapter.AccountAdapter;
import com.xiao.wisdom.bed.base.BaseActivity;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.LoginAccountBean;
import com.xiao.wisdom.bed.bean.QuickRegiserResult;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.utils.SQLiteUtils;
import com.xiao.wisdom.bed.utils.ShareUtils;
import com.xiao.wisdom.bed.view.CommonPopupWindow.CommonPopupWindow;
import com.xiao.wisdom.bed.view.QuickRegisterAlert;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;


/**
 * Created by Administrator on 2018/8/3.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText userEdit,passwordEdit;
    private Button loginBtn;
    private TextView registerText,registerQuickText;
    private CheckBox accountBox,passwordBox,autoLoginBox;

    private List<LoginAccountBean> accountList;
    private CommonPopupWindow popupWindow;
    private AccountAdapter accountAdapter;
    private QuickRegisterAlert quickRegisterAlert;
    private String quickName = "";
    private String quickPassword = "";

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
        accountBox = findViewById(R.id.login_account_remember_box);
        passwordBox = findViewById(R.id.login_password_remember_box);
        autoLoginBox = findViewById(R.id.login_auto_remember_box);
        //updateText = findViewById(R.id.update_);
        registerQuickText = findViewById(R.id.register_quick);

    }

    @Override
    public void initData() {
        EventBus.getDefault().register(this);

        boolean auto = (boolean) ShareUtils.getInstance(this).getValue("auto_login",true);
        autoLoginBox.setChecked(auto);

        accountList = SQLiteUtils.getInstance().selectAccounts();
        for(LoginAccountBean ac:accountList){
            if(ac.account.equals(ShareUtils.getInstance(this).getUser())){
                if(ac.account!=null && ac.account.length()>0){
                    userEdit.setText(ac.account);
                }
                if(ac.password!=null && ac.password.length()>0){
                    passwordEdit.setText(ac.password);
                }
                break;
            }
        }
    }

    @Override
    public void onBind() {
        super.onBind();
        registerText.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        registerQuickText.setOnClickListener(this);
        //updateText.setOnClickListener(this);
        userEdit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int hf = 0;
                if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    if(accountList!=null && accountList.size()>0){
                        showAccountPopupView();
                    }
                }
                return false;
            }
        });
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
            case R.id.register_quick:
                showWaitMsg(getResString(R.string.login_register_quick_msg));
                bedService.quickRegiser(BedUtils.getImei(mContext),mHandler);
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
                String account = "";
                String password = "";
                if(accountBox.isChecked()){
                    account = userEdit.getText().toString().trim();
                }
                if(passwordBox.isChecked()){
                    password = passwordEdit.getText().toString().trim();
                }
                SQLiteUtils.getInstance().insertAccount(account,password);
                ShareUtils.getInstance(this).putValue("auto_login",autoLoginBox.isChecked());
                ShareUtils.getInstance(this).putValue("exit_account",false);
                showToast(R.string.login_activity_toast_login_success);
                startActivity(HomeActivity.class,true);
                break;
            case 0x03:
                showToast(R.string.login_activity_toast_login_error);
                break;
            case 0x04:
                QuickRegiserResult result = (QuickRegiserResult) msg.obj;
                if(result.status.equals("ok")){
                    quickName = result.data.name;
                    quickPassword = result.data.password;
                    showQuickAlert(quickName,quickPassword);
                }else{
                    showToast(R.string.login_register_quick_error_1_text);
                }
                break;
        }

    }

    private void showQuickAlert(final String u,final String p){
        if(quickRegisterAlert !=null){
            quickRegisterAlert.dismiss();
            quickRegisterAlert = null;
        }
        if(quickRegisterAlert == null){
            quickRegisterAlert = new QuickRegisterAlert(mContext, R.style.Dialog, u, p, new QuickRegisterAlert.onQuickCallBack() {
                @Override
                public void onConfirm() {
                    if(u!=null && u.length()>0){
                        SQLiteUtils.getInstance().insertAccount(u,p);
                        accountList = SQLiteUtils.getInstance().selectAccounts();
                        if(accountAdapter!=null){
                            accountAdapter.setData(accountList);
                        }
                    }
                }

                @Override
                public void onQuickLogin() {
                    if(u!=null && u.length()>0){
                        userEdit.setText(u);
                        autoLoginBox.setChecked(true);
                        passwordEdit.setText(p);
                        passwordBox.setChecked(true);
                        autoLoginBox.setChecked(true);
                        loginBtn.performClick();
                    }
                }
            });
        }
        if(quickRegisterAlert!=null){
            quickRegisterAlert.show();
        }
    }

    private void showAccountPopupView(){
        if(popupWindow == null){
            popupWindow = new CommonPopupWindow.Builder(this)
                    .setView(R.layout.popup_account)
                    .setWidthAndHeight(userEdit.getWidth(),ViewGroup.LayoutParams.WRAP_CONTENT)
                    .setBackGroundLevel(1.0f)
                    .setOutsideTouchable(true)
                    .create();
            popupWindow.setFocusable(false);
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            ListView listView = popupWindow.getContentView().findViewById(R.id.list_view);
            accountAdapter = new AccountAdapter(accountList,LoginActivity.this);
            listView.setAdapter(accountAdapter);
        }
        if(!popupWindow.isShowing()){
            popupWindow.showAsDropDown(userEdit);
        }
    }

    @Subscribe
    public void onDeleteEvent(Event.DeleteAccountEvent event){
        if(event.accountBean!=null){
            SQLiteUtils.getInstance().deleteAccount(event.accountBean);
            if(event.accountBean.account.equals(ShareUtils.getInstance(this).getUser())){
                ShareUtils.getInstance(this).cleanUser();
            }
        }
        accountList = SQLiteUtils.getInstance().selectAccounts();
        accountAdapter.setData(accountList);
    }

    @Subscribe
    public void onSwitchEvent(Event.SwitchInputAccountEvent event){
        if(event.accountBean!=null){
            if(event.accountBean.account!=null && event.accountBean.account.length()>0){
                userEdit.setText(event.accountBean.account);
            }
            if(event.accountBean.password!=null && event.accountBean.password.length()>0){
                passwordEdit.setText(event.accountBean.password);
            }
        }
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

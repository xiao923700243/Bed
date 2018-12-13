package com.xiao.wisdom.bed.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;

/**
 * Created by Administrator on 2018/11/29.
 */

public class QuickRegisterAlert extends Dialog {
    private TextView userName,userPasswrod;
    private TextView confirm,quickLogin;
    private onQuickCallBack callBack;

    private String u,p;

    public QuickRegisterAlert(@NonNull Context context) {
        super(context);
    }

    public QuickRegisterAlert(@NonNull Context context, int themeResId,String u,String p,onQuickCallBack callBack) {
        super(context, themeResId);
        this.u = u;
        this.p = p;
        this.callBack = callBack;
    }

    protected QuickRegisterAlert(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(R.layout.dialog_quickregister);
        userName = findViewById(R.id.user_);
        userName.setText(u);
        userPasswrod = findViewById(R.id.password_);
        userPasswrod.setText(p);
        confirm = findViewById(R.id.confirm_);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onConfirm();
                QuickRegisterAlert.this.dismiss();
            }
        });
        quickLogin = findViewById(R.id.quick_login);
        quickLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callBack.onQuickLogin();
                QuickRegisterAlert.this.dismiss();
            }
        });
    }

    public interface onQuickCallBack{
        void onConfirm();
        void onQuickLogin();
    }
}

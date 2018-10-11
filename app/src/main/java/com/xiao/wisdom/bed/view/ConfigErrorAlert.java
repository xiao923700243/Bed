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
 * Created by Administrator on 2018/9/17.
 */

public class ConfigErrorAlert extends Dialog {
    private TextView title,message,cancel,confirm;
    public ConfigErrorAlert(@NonNull Context context) {
        super(context);
    }

    public ConfigErrorAlert(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ConfigErrorAlert(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);
        initView();
    }

    private void initView(){
        title = findViewById(R.id.title_);
        title.setText(R.string.config_alert_error_title);
        message = findViewById(R.id.message_);
        message.setText(R.string.ap_activity_device_online_error);
        cancel = findViewById(R.id.cancel_);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigErrorAlert.this.dismiss();
            }
        });
        confirm = findViewById(R.id.confirm_);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfigErrorAlert.this.dismiss();
            }
        });
    }

}


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
 * Created by Administrator on 2018/11/27.
 */

public class AutoAlert extends Dialog {
    private TextView cancel,confirm;
    public AutoAlert(@NonNull Context context) {
        super(context);
    }

    public AutoAlert(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected AutoAlert(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(R.layout.dialog_auto_message);
        cancel = findViewById(R.id.cancel_);
        confirm = findViewById(R.id.confirm_);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoAlert.this.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutoAlert.this.dismiss();
            }
        });
    }
}

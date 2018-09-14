package com.xiao.wisdom.bed.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;

/**
 * Created by Administrator on 2018/8/29.
 */

public class WaitDialog extends Dialog {
    private TextView msgView;
    public WaitDialog(@NonNull Context context) {
        super(context);
    }

    public WaitDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WaitDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_wait);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        msgView = findViewById(R.id.msg);
    }

    public void showMessage(String msg){
        if(msgView!=null){
            msgView.setText(msg);
        }
    }
}

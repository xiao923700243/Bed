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
 * Created by Administrator on 2018/8/22.
 */

public class UnBindAlert extends Dialog {
    private TextView title,message,cancel,confirm;
    private Context context;
    private UnBindEvent event;

    public UnBindAlert(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public UnBindAlert(@NonNull Context context, int themeResId,UnBindEvent event) {
        super(context, themeResId);
        this.context = context;
        this.event = event;
    }

    protected UnBindAlert(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_message);
        initView();
    }

    private void initView(){
        title = findViewById(R.id.title_);
        title.setText(R.string.unbind_alert_title_msg);
        title.setText(R.string.unbind_alert_title_msg);
        message = findViewById(R.id.message_);
        message.setText(R.string.unbind_alert_content_msg);
        cancel = findViewById(R.id.cancel_);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.onCancel();
            }
        });
        confirm = findViewById(R.id.confirm_);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                event.onConfirm();
            }
        });
    }


    public interface UnBindEvent{
        void onCancel();
        void onConfirm();
    }
}

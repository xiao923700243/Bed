package com.xiao.wisdom.bed.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.utils.BedUtils;

/**
 * Created by Administrator on 2018/9/26.
 */

public class InputAlert extends Dialog {
    private TextView title,cancel,confirm;
    private EditText message;
    public InputAlertEvent event;

    public InputAlert(@NonNull Context context) {
        super(context);
    }

    public InputAlert(@NonNull Context context, int themeResId,InputAlertEvent event) {
        super(context, themeResId);
        this.event = event;
    }

    protected InputAlert(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(R.layout.dialog_input);
        initView();

    }

    private void initView(){
        title = findViewById(R.id.title_);
        title.setText(R.string.input_alert_title_msg);
        message = findViewById(R.id.message_);
        cancel = findViewById(R.id.cancel_);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputAlert.this.dismiss();
            }
        });
        confirm = findViewById(R.id.confirm_);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pwd = message.getText().toString().trim();
                if(TextUtils.isEmpty(pwd)){
                    event.onInputAlertEvent(InputAlertEvent.EVENT_NULL);
                    return;
                }
                if(pwd.equals(BedUtils.CHECK_PASSWORD)){
                    event.onInputAlertEvent(InputAlertEvent.EVENT_SUCCESS);
                    InputAlert.this.dismiss();
                }else{
                    event.onInputAlertEvent(InputAlertEvent.EVENT_ERROR);
                }
            }
        });
    }

    public interface InputAlertEvent{
        public int EVENT_NULL = 0x01;
        public int EVENT_ERROR = 0x02;
        public int EVENT_SUCCESS = 0x03;
        public void onInputAlertEvent(int event);
    }
}

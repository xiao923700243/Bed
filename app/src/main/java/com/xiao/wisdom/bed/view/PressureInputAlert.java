package com.xiao.wisdom.bed.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xiao.wisdom.bed.R;

/**
 * Created by Administrator on 2018/11/27.
 */

public class PressureInputAlert extends Dialog {
    private PressureInputEvent event;
    private EditText inputText;
    private TextView cancel,confirm;
    private Context mContext;
    public interface PressureInputEvent{
        void onPressureInputEvent(int code);
    }

    public PressureInputAlert(@NonNull Context context) {
        super(context);
    }

    public PressureInputAlert(@NonNull Context context, int themeResId,PressureInputEvent event) {
        super(context, themeResId);
        this.event = event;
        this.mContext = context;
    }

    protected PressureInputAlert(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        setContentView(R.layout.dialog_pressure_input);
        inputText = findViewById(R.id.message_);
        cancel = findViewById(R.id.cancel_);
        confirm = findViewById(R.id.confirm_);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PressureInputAlert.this.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputText.getText().toString().trim();
                if(input==null || input.length()<=0){
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.dialog_pressure_input_error_1_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                int strInput = Integer.valueOf(input);
                if(strInput<160 || strInput > 720){
                    inputText.setText("");
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.dialog_pressure_input_error_2_msg), Toast.LENGTH_SHORT).show();
                    return;
                }
                event.onPressureInputEvent(strInput);
                PressureInputAlert.this.dismiss();
            }
        });
    }


}

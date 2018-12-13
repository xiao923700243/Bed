package com.xiao.wisdom.bed.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.LoginAccountBean;
import com.xiao.wisdom.bed.utils.SQLiteUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;


/**
 * Created by Administrator on 2018/11/25.
 */

public class AccountAdapter extends BaseAdapter {
    private List<LoginAccountBean> data;
    private Context context;
    private LayoutInflater mInflater;
    public AccountAdapter(List<LoginAccountBean> data, Context context){
        this.data = data;
        this.context = context;
        this.mInflater =  LayoutInflater.from(context);
    }

    public void setData(List<LoginAccountBean> data){
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if(data!=null && data.size()>0){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.popup_account_item, viewGroup, false); //加载布局
            holder = new ViewHolder();
            holder.pupupAccount = convertView.findViewById(R.id.popup_account);
            holder.accountDelete = convertView.findViewById(R.id.popup_account_delete);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.pupupAccount.setText(data.get(i).account);
        holder.pupupAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new Event.SwitchInputAccountEvent(data.get(i)));
            }
        });
        holder.accountDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new Event.DeleteAccountEvent(data.get(i)));
            }
        });
        return convertView;
    }

    public class ViewHolder{
        TextView pupupAccount;
        TextView accountDelete;
    }
}

package com.xiao.wisdom.bed.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xiao.wisdom.bed.R;
import com.xiao.wisdom.bed.bean.Event;
import com.xiao.wisdom.bed.bean.GetUserAllDevStatsResult;
import com.xiao.wisdom.bed.bean.LampBean;
import com.xiao.wisdom.bed.bean.MoveAction;
import com.xiao.wisdom.bed.ui.activity.BedDetailsActivity;
import com.xiao.wisdom.bed.utils.BedUtils;
import com.xiao.wisdom.bed.utils.ShareUtils;

import org.greenrobot.eventbus.EventBus;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2018/8/9.
 */

public class HomeAdapter extends BaseAdapter{

    public GetUserAllDevStatsResult result;
    private LayoutInflater mInflater;
    public Context context;
    private boolean isClick = true;
    public HomeAdapter(Context context,GetUserAllDevStatsResult result){
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.result = result;
    }


    @Override
    public int getCount() {
        if(result!=null && result.data!=null && result.data.size()>0){
            return result.data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return result.data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setData(GetUserAllDevStatsResult result){
        this.result = result;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.activity_home_item, viewGroup, false); //加载布局
            holder = new ViewHolder();
            holder.rootView = convertView;
            holder.devNameIcon = convertView.findViewById(R.id.devidename_);
            holder.cstname = convertView.findViewById(R.id.cstname_);
            holder.up = convertView.findViewById(R.id.up_);
            holder.down = convertView.findViewById(R.id.down_);
            holder.stop = convertView.findViewById(R.id.stop_);
            holder.lamp = convertView.findViewById(R.id.lamp_);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.devNameIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("devid",result.data.get(i).devid);
                bundle.putString("cstname",result.data.get(i).cstname);
                bundle.putString("devname",result.data.get(i).devname);
                bundle.putString("devtype",result.data.get(i).devtype);
                bundle.putFloat("temper",result.data.get(i).temper);
                bundle.putFloat("humidity",result.data.get(i).humidity);
                intent.putExtras(bundle);
                intent.setClass(context,BedDetailsActivity.class);
                context.startActivity(intent);
            }
        });
        holder.devNameIcon.setImageResource(getSouresIdByName(result.data.get(i).devname));
        holder.cstname.setText(result.data.get(i).cstname+" "+getOnline(result.data.get(i).online));

        LampBean lamp = ShareUtils.getInstance(context).getLampAction(result.data.get(i).devid);
        if(lamp != null){
            if(System.currentTimeMillis() - lamp.lasttime > 2*1000){
                holder.lamp.setImageResource(result.data.get(i).lock == 0?R.mipmap.ic_lock:R.mipmap.ic_lock_focus);
            }else{
                holder.lamp.setImageResource(lamp.status == 0?R.mipmap.ic_lock:R.mipmap.ic_lock_focus);
            }
        }else{
            holder.lamp.setImageResource(result.data.get(i).lock == 0?R.mipmap.ic_lock:R.mipmap.ic_lock_focus);
        }
        holder.lamp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LampBean itemlamp = ShareUtils.getInstance(context).getLampAction(result.data.get(i).devid);
                if(result.data.get(i).online == 1){
                    if(itemlamp!=null && System.currentTimeMillis() - itemlamp.lasttime < 2*1000){
                        if(itemlamp.status == 0){
                            sendCmd(BedUtils.CMD_LED_ON,result.data.get(i).devid);
                            ShareUtils.getInstance(context).setLampAction(result.data.get(i).devid,obtainLampAction(result.data.get(i).devid,1));
                        }else{
                            sendCmd(BedUtils.CMD_LED_OFF,result.data.get(i).devid);
                            ShareUtils.getInstance(context).setLampAction(result.data.get(i).devid,obtainLampAction(result.data.get(i).devid,0));
                        }
                    }else{
                        sendCmd(result.data.get(i).lock==0?BedUtils.CMD_LED_ON:BedUtils.CMD_LED_OFF,result.data.get(i).devid);
                        ShareUtils.getInstance(context).setLampAction(result.data.get(i).devid,obtainLampAction(result.data.get(i).devid,result.data.get(i).lock==0?1:0));
                    }
                }else{
                    Toast.makeText(context,getResString(R.string.home_activbity_send_error_off_line_msg),Toast.LENGTH_SHORT).show();
                }

            }
        });

        MoveAction action = ShareUtils.getInstance(context).getMoveAction(result.data.get(i).devid);
        if(action!=null){
            if(System.currentTimeMillis() - action.lasttime >2*1000){
                holder.up.setImageResource(result.data.get(i).motor==1?R.mipmap.ic_up_focus:R.mipmap.ic_up);
            }else{
                holder.up.setImageResource(action.action.equals("U")?R.mipmap.ic_up_focus:R.mipmap.ic_up);
            }
        }else{
            holder.up.setImageResource(result.data.get(i).motor==1?R.mipmap.ic_up_focus:R.mipmap.ic_up);
        }
        holder.up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.data.get(i).lock == 1){
                    Toast.makeText(context,getResString(R.string.homeadapter_error_msg),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(result.data.get(i).online == 1){
                    //在线
                    ShareUtils.getInstance(context).setMoveAction(result.data.get(i).devid,obtainAction(result.data.get(i).devid,"U"));
                }
                sendCmd(BedUtils.CMD_MOTOR_UP,result.data.get(i).devid);
            }
        });

        if(action!=null){
            if(System.currentTimeMillis() - action.lasttime >2*1000){
                holder.down.setImageResource(result.data.get(i).motor==2?R.mipmap.ic_down_focus:R.mipmap.ic_down);
            }else{
                holder.down.setImageResource(action.action.equals("D")?R.mipmap.ic_down_focus:R.mipmap.ic_down);
            }
        }else{
            holder.down.setImageResource(result.data.get(i).motor==2?R.mipmap.ic_down_focus:R.mipmap.ic_down);
        }
        holder.down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.data.get(i).lock == 1){
                    Toast.makeText(context,getResString(R.string.homeadapter_error_msg),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(result.data.get(i).online == 1){
                    //在线
                    ShareUtils.getInstance(context).setMoveAction(result.data.get(i).devid,obtainAction(result.data.get(i).devid,"D"));
                }
                sendCmd(BedUtils.CMD_MOTOR_DOWN,result.data.get(i).devid);
            }
        });

        if(action!=null){
            if(System.currentTimeMillis() - action.lasttime >2*1000){
                holder.stop.setImageResource(result.data.get(i).motor==0?R.mipmap.ic_stop_focus:R.mipmap.ic_stop);
            }else{
                holder.stop.setImageResource(action.action.equals("S")?R.mipmap.ic_stop_focus:R.mipmap.ic_stop);
            }
        }else{
            holder.stop.setImageResource(result.data.get(i).motor==0?R.mipmap.ic_stop_focus:R.mipmap.ic_stop);
        }
        holder.stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(result.data.get(i).lock == 1){
                    Toast.makeText(context,getResString(R.string.homeadapter_error_msg),Toast.LENGTH_SHORT).show();
                    return;
                }
                if(result.data.get(i).online == 1){
                    //在线
                    ShareUtils.getInstance(context).setMoveAction(result.data.get(i).devid,obtainAction(result.data.get(i).devid,"S"));
                }
                sendCmd(BedUtils.CMD_MOTOR_STOP,result.data.get(i).devid);
            }
        });

        return convertView;
    }

    private void sendCmd(String cmd,String devid){
        EventBus.getDefault().post(new Event.CmdEvent(cmd,devid));
    }

    public int getSouresIdByName(String devName){
        if(devName.equals("smartbed")){
            return R.mipmap.ic_dev_bed;
        }else if(devName.equals("smartlock")){
            return R.mipmap.ic_dev_lock;
        }else if(devName.equals("smartcabinet")){
            return R.mipmap.ic_dev_cabinet;
        }else{
            return R.mipmap.ic_dev_bed;
        }
    }

    public String getOnline(int online){
        if(online == 1){
            return getResString(R.string.home_adapter_device_status_on_line_msg);
        }
        return getResString(R.string.home_adapter_device_status_off_line_msg);
    }

    public MoveAction obtainAction(String devid,String action){
        MoveAction a = new MoveAction();
        a.action = action;
        a.devid = devid;
        a.lasttime = System.currentTimeMillis();
        return a;
    }

    public LampBean obtainLampAction(String devid,int status){
        LampBean lamp = new LampBean();
        lamp.devid = devid;
        lamp.lasttime = System.currentTimeMillis();
        lamp.status = status;
        return lamp;
    }

    public String getResString(int id){
        return context.getResources().getString(id);
    }

    public class ViewHolder{
        View rootView;
        CircleImageView devNameIcon;
        TextView cstname;
        ImageView up;
        ImageView down;
        ImageView stop;
        ImageView lamp;
    }
}

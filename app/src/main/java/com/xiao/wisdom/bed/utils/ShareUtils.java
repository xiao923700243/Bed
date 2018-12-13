package com.xiao.wisdom.bed.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import com.xiao.wisdom.bed.bean.LampBean;
import com.xiao.wisdom.bed.bean.LoginUserResult;
import com.xiao.wisdom.bed.bean.MoveAction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/8/2.
 */

public class ShareUtils {
    private Context context;
    private static WeakReference<ShareUtils> weakReference;
    private static SharedPreferences sharedPreferences;
    private ShareUtils(Context context){
        this.context = context;
        this.sharedPreferences = context.getSharedPreferences("bed_config",Context.MODE_PRIVATE);
    }

    /**
     * 获取实例
     * @param context
     * @return
     */
    public static ShareUtils getInstance(Context context){
        if(weakReference == null || weakReference.get() == null){
            weakReference = new WeakReference<ShareUtils>(new ShareUtils(context));
        }
        return weakReference.get();
    }

    public String getIMEI(){
        return sharedPreferences.getString("IMEI","");
    }
    public void saveIMEI(String IMEI){
        sharedPreferences.edit().putString("IMEI",IMEI).commit();
    }


    public void saveUser(String user,String password,String nickname){
        sharedPreferences.edit().putString("user_",user).putString("password_",password).putString("nickname_",nickname).commit();
    }
    public String getUser(){
        return sharedPreferences.getString("user_","");
    }
    public String getPassword(){
        return sharedPreferences.getString("password_","");
    }
    public String getNickname(){
        return sharedPreferences.getString("nickname_","");
    }


    public void cleanUser(){
        sharedPreferences.edit().remove("user_").remove("password_").commit();
    }

    public void setMoveAction(String devid, MoveAction action){
        if(action==null){
            return;
        }
        sharedPreferences.edit().putString(devid+"_m",Object2String(action)).commit();
    }

    public MoveAction getMoveAction(String devid){
        String strAction = sharedPreferences.getString(devid+"_m","");
        if(TextUtils.isEmpty(strAction)){
            return null;
        }
        return (MoveAction) String2Object(strAction);
    }

    public void setLampAction(String devid, LampBean lamp){
        if(lamp == null){
            return;
        }
        sharedPreferences.edit().putString(devid+"_l",Object2String(lamp)).commit();
    }

    public LampBean getLampAction(String devid){
        String lamp = sharedPreferences.getString(devid+"_l","");
        if(TextUtils.isEmpty(lamp)){
            return null;
        }
        return (LampBean)String2Object(lamp);
    }

    public void putValue(String key,Object value){
        String className = value.getClass().getSimpleName();
        if(className.equals("Integer")){
            sharedPreferences.edit().putInt(key, (Integer) value).commit();
        }else if(className.equals("String")){
            sharedPreferences.edit().putString(key, (String) value).commit();
        }else if(className.equals("Boolean")){
            sharedPreferences.edit().putBoolean(key, (Boolean) value).commit();
        }else if(className.equals("Float")){
            sharedPreferences.edit().putFloat(key, (Float) value).commit();
        }else if(className.equals("Long")){
            sharedPreferences.edit().putLong(key, (Long) value).commit();
        }
    }

    public Object getValue(String key,Object defValue){
        String className = defValue.getClass().getSimpleName();
        if(className.equals("Integer")){
            return sharedPreferences.getInt(key, (Integer) defValue);
        }else if(className.equals("String")){
            return sharedPreferences.getString(key, (String) defValue);
        }else if(className.equals("Boolean")){
            return sharedPreferences.getBoolean(key, (Boolean) defValue);
        }else if(className.equals("Float")){
            return sharedPreferences.getFloat(key, (Float) defValue);
        }else if(className.equals("Long")){
            return sharedPreferences.getLong(key, (Long) defValue);
        }else{
            return null;
        }
    }

    /**
     * 对象转String
     * @param object
     * @return
     */
    private String Object2String(Object object) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            String string = new String(Base64.encode(byteArrayOutputStream.toByteArray(), Base64.DEFAULT));
            objectOutputStream.close();
            return string;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * String转对象
     * @param objectString
     * @return
     */
    private Object String2Object(String objectString) {
        byte[] mobileBytes = Base64.decode(objectString.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mobileBytes);
        ObjectInputStream objectInputStream = null;
        try {
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            Object object = objectInputStream.readObject();
            objectInputStream.close();
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}

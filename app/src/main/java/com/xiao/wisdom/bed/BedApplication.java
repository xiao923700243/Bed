package com.xiao.wisdom.bed;

import android.app.Application;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/8/2.
 */

public class BedApplication extends Application {

    public static final boolean isDebug = true;
    public static boolean needRefresh = true;
    @Override
    public void onCreate() {
        super.onCreate();
    }
}

package com.xiao.wisdom.bed;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.xiao.wisdom.bed.bean.DaoMaster;
import com.xiao.wisdom.bed.bean.DaoSession;

import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2018/8/2.
 */

public class BedApplication extends Application {

    //
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    //
    private static BedApplication instances;

    public static final boolean isDebug = true;
    public static boolean needRefresh = true;
    @Override
    public void onCreate() {
        super.onCreate();
        instances = this;
        initDatabase();
    }

    public static BedApplication getBedAppLication(){
        return instances;
    }

    private void initDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "com_xiao_windom_bed_account_db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}

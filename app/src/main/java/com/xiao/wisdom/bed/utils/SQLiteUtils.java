package com.xiao.wisdom.bed.utils;

import com.xiao.wisdom.bed.BedApplication;
import com.xiao.wisdom.bed.bean.DaoSession;
import com.xiao.wisdom.bed.bean.LoginAccountBean;
import com.xiao.wisdom.bed.bean.LoginAccountBeanDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/11/25.
 */

public class SQLiteUtils {
    private static SQLiteUtils instance;
    private LoginAccountBeanDao accountDao;
    private DaoSession daoSession;

    private SQLiteUtils() {
        accountDao = BedApplication.getBedAppLication().getDaoSession().getLoginAccountBeanDao();
        daoSession = BedApplication.getBedAppLication().getDaoSession();
    }

    public static SQLiteUtils getInstance() {
        if (instance == null) {
            synchronized (SQLiteUtils.class) {
                if (instance == null) {
                    instance = new SQLiteUtils();
                }
            }
        }
        return instance;
    }

    public void insertAccount(String account,String password){
        if(account == null || account.length()<=0){
            return;
        }
        List<LoginAccountBean> list = selectAccounts();
        int index = -1;
        for(int i=0;i<list.size();i++){
            if(list.get(i).getAccount().equals(account)){
                index = i;
                break;
            }
        }
        if(index == -1){
            LoginAccountBean ins = new LoginAccountBean();
            ins.account = account;
            if(password!=null && password.length()>0){
                ins.password = password;
            }
            accountDao.insert(ins);
        }else{
            LoginAccountBean up = list.get(index);
            up.setPassword(password);
            accountDao.update(up);
        }
    }

    public void deleteAccount(LoginAccountBean account){
        accountDao.delete(account);
    }

    public List selectAccounts() {
        accountDao.detachAll();//清除缓存
        List list1 = accountDao.queryBuilder().list();
        return list1 == null ? new ArrayList() : list1;
    }

}

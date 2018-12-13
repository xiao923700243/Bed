package com.xiao.wisdom.bed.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Administrator on 2018/11/25.
 */

@Entity
public class LoginAccountBean {

    @Id(autoincrement = true)
    private Long id;

    @NotNull
    @Unique
    public String account = "";
    public String password = "";
    @Generated(hash = 2119833226)
    public LoginAccountBean(Long id, @NotNull String account, String password) {
        this.id = id;
        this.account = account;
        this.password = password;
    }
    @Generated(hash = 1505034319)
    public LoginAccountBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getAccount() {
        return this.account;
    }
    public void setAccount(String account) {
        this.account = account;
    }
    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}

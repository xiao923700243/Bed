package com.xiao.wisdom.bed.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2018/8/2.
 */

public class LoginUserResult implements Serializable{
    public String status;
    public String msg;
    public Data data;
    public class Data{
        public String name;
        public String nickname;
        public int ret;
    }
}

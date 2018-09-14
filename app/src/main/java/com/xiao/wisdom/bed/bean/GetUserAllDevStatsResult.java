package com.xiao.wisdom.bed.bean;

import java.util.List;

/**
 * Created by Administrator on 2018/8/9.
 */

public class GetUserAllDevStatsResult {
    public String status;
    public String msg;
    public List<Device> data;
    public class Device {
        public String devid;
        public String devname;
        public String devtype;
        public String cstname;
        public int online;
        public int motor;
        public int nlight;
        public float temper;
        public float humidity;
        public long lefttsetnum;
        public String lastcmd;
        public int cmdstats;
        public String cmdtime;
    }
}

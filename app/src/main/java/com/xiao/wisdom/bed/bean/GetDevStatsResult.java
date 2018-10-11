package com.xiao.wisdom.bed.bean;

/**
 * Created by Administrator on 2018/8/2.
 */

public class GetDevStatsResult {
    public String status;
    public String msg;
    public Data data;
    public class Data{
        public String devid;
        public String devname;
        public String devtype;
        public String cstname;
        public int online;
        public int motor;
        public int nlight;
        public float temper;
        public float humidity;
        public int lefttestflag;
        public String lastcmd;
        public int cmdstats;
        public String cmdtime;
    }
}

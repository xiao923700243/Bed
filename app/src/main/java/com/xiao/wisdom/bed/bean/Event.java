package com.xiao.wisdom.bed.bean;

/**
 * Created by Administrator on 2018/8/7.
 */

public class Event {
    public static class BindEvent{
        public int what;
    }

    public static class CmdEvent{
        public CmdEvent(String cmd,String devid){
            this.cmd = cmd;
            this.devid = devid;
        }
        public String cmd;
        public String devid;
    }
}

package com.intfocus.yonghuitest.subject.template_v2.bean;

import java.io.Serializable;

/**
 * Created by zbaoliang on 17-5-14.
 */

public class MDRPUnitSingleValue {

    public State state;
    public MainData main_data;
    public MainData sub_data;


    public static class State implements Serializable {
        public int color;
    }

    public static class MainData implements Serializable {
        public String name;
        public float data;
        public String format;
        public String percentage;
    }

}

package com.intfocus.yonghuitest.bean.tablechart;

/**
 * Created by CANC on 2017/4/20.
 */

public class Head {

    /**
     * value : 销量
     */

    private String value;
    public boolean isShow = true;//是展示，默认全部展示
    public boolean isKeyColumn;//是否为关键列
    public int originPosition;
    public String sort = "default";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

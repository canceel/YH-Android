package com.intfocus.yonghuitest.bean.table;

/**
 * Created by CANC on 2017/4/20.
 */

public class Head {

    /**
     * value : 销量
     */

    private String value;
    private boolean show;
    public int originPosition;
    public boolean isKeyColumn;//是否为关键列
    public String sort = "default";

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}

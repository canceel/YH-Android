package com.intfocus.yonghuitest.bean.tablechart;

/**
 * Created by CANC on 2017/4/24.
 */

public class FilterItem {

    /**
     * value : 区域A
     */

    private String value;
    //是否选中，默认选中
    public boolean isSelected = true;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

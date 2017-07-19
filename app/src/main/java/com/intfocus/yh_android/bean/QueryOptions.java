package com.intfocus.yh_android.bean;

import java.util.List;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/07/17 下午2:43
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public class QueryOptions {

    /**
     * type : text_input
     * data : []
     */

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        private String type;
        private List<String> data;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }
}

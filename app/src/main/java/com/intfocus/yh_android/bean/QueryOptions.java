package com.intfocus.yh_android.bean;

import java.util.List;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/07/17 下午2:43
 * e-mail: PassionateWsj@outlook.com
 * name: 查询选项数据类
 * desc:
 * ****************************************************
 */

public class QueryOptions {

    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * typeName : text_input
         * typeId : 1
         * data : [""]
         */

        private String typeName;
        private String typeId;
        private List<String> data;

        public String getTypeName() {
            return typeName;
        }

        public void setTypeName(String typeName) {
            this.typeName = typeName;
        }

        public String getTypeId() {
            return typeId;
        }

        public void setTypeId(String typeId) {
            this.typeId = typeId;
        }

        public List<String> getData() {
            return data;
        }

        public void setData(List<String> data) {
            this.data = data;
        }
    }
}

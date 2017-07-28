package com.intfocus.yonghuitest.bean.dashboard;

import java.util.List;

/**
 * Created by liuruilin on 2017/6/12.
 */

public class testbean {

    /**
     * code : 200
     * message : 获取数据成功
     * data : [{"id":7,"name":"天天赛马JAY","category":"1","group_name":"赛马专题","link_path":"/mobile/v2/group/%@/template/2/report/102","publicly":false,"icon":"icon-default.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-default.png","group_id":7,"health_value":0,"group_order":11,"item_order":1,"created_at":"2017-06-19T10:27:46.000+08:00"},{"id":8,"name":"商行赛马","category":"1","group_name":"赛马专题","link_path":"http://123.59.75.85:8080/yhportal/appClientReport/horse/horseByShopReport.jsp","publicly":null,"icon":"icon-3门店赛马结果.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-3门店赛马结果.png","group_id":8,"health_value":0,"group_order":11,"item_order":2,"created_at":"2016-05-18T18:05:00.000+08:00"},{"id":9,"name":"赛马成绩","category":"1","group_name":"赛马专题","link_path":"/mobile/v2/group/%@/template/2/report/103","publicly":false,"icon":"icon-default.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-default.png","group_id":9,"health_value":0,"group_order":11,"item_order":3,"created_at":"2017-06-19T10:27:46.000+08:00"},{"id":10,"name":"新店实时销售","category":"1","group_name":"新店开业","link_path":"http://123.59.75.85:8080/yhportal/appClientReport/newShopSale.pdf","publicly":false,"icon":"icon-default.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-default.png","group_id":10,"health_value":0,"group_order":12,"item_order":2,"created_at":"2017-06-19T10:30:33.000+08:00"},{"id":30,"name":"第二集群各部门费用","category":"1","group_name":"经营分析","link_path":"http://123.59.75.85:8080/yhportal/appClientReport/departmentCost.pdf","publicly":false,"icon":"icon-default.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-default.png","group_id":30,"health_value":0,"group_order":14,"item_order":5,"created_at":"2017-06-19T10:36:28.000+08:00"},{"id":11,"name":"生鲜单品实时销售(同片区)","category":"1","group_name":"生鲜单品实时","link_path":"http://123.59.75.85:8080/yhportal/backstage/app/report/shopRealtimeSale.jsp","publicly":null,"icon":"icon-4生鲜单品实时销售.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-4生鲜单品实时销售.png","group_id":11,"health_value":0,"group_order":33,"item_order":1,"created_at":"2016-12-13T14:59:02.000+08:00"}]
     */

    private int code;
    private String message;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 7
         * name : 天天赛马JAY
         * category : 1
         * group_name : 赛马专题
         * link_path : /mobile/v2/group/%@/template/2/report/102
         * publicly : false
         * icon : icon-default.png
         * icon_link : http://yonghui-test.idata.mobi/images/icon-default.png
         * group_id : 7
         * health_value : 0
         * group_order : 11
         * item_order : 1
         * created_at : 2017-06-19T10:27:46.000+08:00
         */

        private int id;
        private String name;
        private String category;
        private String group_name;
        private String link_path;
        private boolean publicly;
        private String icon;
        private String icon_link;
        private int group_id;
        private int health_value;
        private int group_order;
        private int item_order;
        private String created_at;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getLink_path() {
            return link_path;
        }

        public void setLink_path(String link_path) {
            this.link_path = link_path;
        }

        public boolean isPublicly() {
            return publicly;
        }

        public void setPublicly(boolean publicly) {
            this.publicly = publicly;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getIcon_link() {
            return icon_link;
        }

        public void setIcon_link(String icon_link) {
            this.icon_link = icon_link;
        }

        public int getGroup_id() {
            return group_id;
        }

        public void setGroup_id(int group_id) {
            this.group_id = group_id;
        }

        public int getHealth_value() {
            return health_value;
        }

        public void setHealth_value(int health_value) {
            this.health_value = health_value;
        }

        public int getGroup_order() {
            return group_order;
        }

        public void setGroup_order(int group_order) {
            this.group_order = group_order;
        }

        public int getItem_order() {
            return item_order;
        }

        public void setItem_order(int item_order) {
            this.item_order = item_order;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
}

package com.intfocus.yonghuitest.bean.dashboard;

import java.util.List;

/**
 * Created by liuruilin on 2017/6/12.
 */

public class testbean {

    /**
     * code : 200
     * message : 获取专题列表成功
     * data : [{"category":null,"data":[{"group_name":"赛马结果","data":[{"id":8,"name":"门店赛马结果","group_name":"赛马结果","link_path":"http://123.59.75.85:8080/yhportal/appClientReport/horse/horseByShopReport.jsp","publicly":null,"icon":"icon-3门店赛马结果.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-3门店赛马结果.png","group_id":8,"health_value":0,"group_order":43,"item_order":1,"created_at":"2016-05-18T18:05:00.000+08:00"}]}]}]
     */

    private int code;
    private String message;
    private List<DataBeanXX> data;

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

    public List<DataBeanXX> getData() {
        return data;
    }

    public void setData(List<DataBeanXX> data) {
        this.data = data;
    }

    public static class DataBeanXX {
        /**
         * category : null
         * data : [{"group_name":"赛马结果","data":[{"id":8,"name":"门店赛马结果","group_name":"赛马结果","link_path":"http://123.59.75.85:8080/yhportal/appClientReport/horse/horseByShopReport.jsp","publicly":null,"icon":"icon-3门店赛马结果.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-3门店赛马结果.png","group_id":8,"health_value":0,"group_order":43,"item_order":1,"created_at":"2016-05-18T18:05:00.000+08:00"}]}]
         */

        private Object category;
        private List<DataBeanX> data;

        public Object getCategory() {
            return category;
        }

        public void setCategory(Object category) {
            this.category = category;
        }

        public List<DataBeanX> getData() {
            return data;
        }

        public void setData(List<DataBeanX> data) {
            this.data = data;
        }

        public static class DataBeanX {
            /**
             * group_name : 赛马结果
             * data : [{"id":8,"name":"门店赛马结果","group_name":"赛马结果","link_path":"http://123.59.75.85:8080/yhportal/appClientReport/horse/horseByShopReport.jsp","publicly":null,"icon":"icon-3门店赛马结果.png","icon_link":"http://yonghui-test.idata.mobi/images/icon-3门店赛马结果.png","group_id":8,"health_value":0,"group_order":43,"item_order":1,"created_at":"2016-05-18T18:05:00.000+08:00"}]
             */

            private String group_name;
            private List<DataBean> data;

            public String getGroup_name() {
                return group_name;
            }

            public void setGroup_name(String group_name) {
                this.group_name = group_name;
            }

            public List<DataBean> getData() {
                return data;
            }

            public void setData(List<DataBean> data) {
                this.data = data;
            }

            public static class DataBean {
                /**
                 * id : 8
                 * name : 门店赛马结果
                 * group_name : 赛马结果
                 * link_path : http://123.59.75.85:8080/yhportal/appClientReport/horse/horseByShopReport.jsp
                 * publicly : null
                 * icon : icon-3门店赛马结果.png
                 * icon_link : http://yonghui-test.idata.mobi/images/icon-3门店赛马结果.png
                 * group_id : 8
                 * health_value : 0
                 * group_order : 43
                 * item_order : 1
                 * created_at : 2016-05-18T18:05:00.000+08:00
                 */

                private int id;
                private String name;
                private String group_name;
                private String link_path;
                private Object publicly;
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

                public Object getPublicly() {
                    return publicly;
                }

                public void setPublicly(Object publicly) {
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
    }
}

package com.intfocus.yonghuitest.bean;

import java.util.List;

/**
 * Created by liuruilin on 2017/6/12.
 */

public class testbean {

    /**
     * current_page : 1
     * page_size : 10
     * total_count : 8
     * total_page : 1
     * data : [{"createTime":"2017-07-08 20:48:49","id":71,"title":"超市如何监管收银漏洞","favorite":"0","tagInfo":""},{"createTime":"2017-07-08 20:45:51","id":70,"title":"降低生鲜品损耗的6大环节","favorite":"1","tagInfo":""},{"createTime":"2017-07-08 20:35:17","id":69,"title":"门店商品组合的\u2018基本\u2019方法","favorite":"0","tagInfo":"新零售"},{"createTime":"2017-07-08 20:30:03","id":68,"title":"门店补救单对移动均价的影响","favorite":"0","tagInfo":"分析师"},{"createTime":"2017-07-08 20:24:48","id":67,"title":"生鲜进销存精细化管理介绍","favorite":"0","tagInfo":"分析师"},{"createTime":"2017-07-08 20:18:33","id":66,"title":"常用促销手段都有哪些利弊附操作建议？","favorite":"0","tagInfo":""},{"createTime":"2017-07-08 20:13:06","id":65,"title":"零售数据分析#价格带定位分析","favorite":"1","tagInfo":"分析师,大数据"},{"createTime":"2017-06-30 17:14:10","id":60,"title":"618永辉销售数据分析","favorite":"1","tagInfo":"分析师,新零售,大数据"}]
     * message : successfully
     * code : 200
     */

    private int current_page;
    private int page_size;
    private int total_count;
    private int total_page;
    private String message;
    private int code;
    private List<DataBean> data;

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * createTime : 2017-07-08 20:48:49
         * id : 71
         * title : 超市如何监管收银漏洞
         * favorite : 0
         * tagInfo :
         */

        private String createTime;
        private int id;
        private String title;
        private String favorite;
        private String tagInfo;

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getFavorite() {
            return favorite;
        }

        public void setFavorite(String favorite) {
            this.favorite = favorite;
        }

        public String getTagInfo() {
            return tagInfo;
        }

        public void setTagInfo(String tagInfo) {
            this.tagInfo = tagInfo;
        }
    }
}

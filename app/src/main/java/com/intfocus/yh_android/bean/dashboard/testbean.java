package com.intfocus.yh_android.bean.dashboard;

/**
 * Created by liuruilin on 2017/6/12.
 */

public class testbean {

    /**
     * type : report
     * title : 第二集群销售额
     * url : /mobile/v2/group/%@/template/4/report/8
     * obj_id : 8
     * obj_type : 1
     * debug_timestamp : 2017-07-14 11:35:47 +0800
     */

    private String type;
    private String title;
    private String url;
    private int obj_id;
    private int obj_type;
    private String debug_timestamp;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getObj_id() {
        return obj_id;
    }

    public void setObj_id(int obj_id) {
        this.obj_id = obj_id;
    }

    public int getObj_type() {
        return obj_type;
    }

    public void setObj_type(int obj_type) {
        this.obj_type = obj_type;
    }

    public String getDebug_timestamp() {
        return debug_timestamp;
    }

    public void setDebug_timestamp(String debug_timestamp) {
        this.debug_timestamp = debug_timestamp;
    }
}

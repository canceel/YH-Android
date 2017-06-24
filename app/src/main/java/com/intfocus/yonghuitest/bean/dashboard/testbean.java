package com.intfocus.yonghuitest.bean.dashboard;

/**
 * Created by liuruilin on 2017/6/12.
 */

public class testbean {

    /**
     * user_num : 13162726850
     * user_name : 刘锐麟
     * login_duration : 50
     * browse_report_count : 6
     * surpass_percentage : 37.4
     * login_count : 1197
     * browse_count : 19
     * browse_distinct_count : 6
     * code : 200
     */

    private String user_num;
    private String user_name;
    private String login_duration;
    private String browse_report_count;
    private double surpass_percentage;
    private String login_count;
    private String browse_count;
    private String browse_distinct_count;
    private int code;

    public String getUser_num() {
        return user_num;
    }

    public void setUser_num(String user_num) {
        this.user_num = user_num;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getLogin_duration() {
        return login_duration;
    }

    public void setLogin_duration(String login_duration) {
        this.login_duration = login_duration;
    }

    public String getBrowse_report_count() {
        return browse_report_count;
    }

    public void setBrowse_report_count(String browse_report_count) {
        this.browse_report_count = browse_report_count;
    }

    public double getSurpass_percentage() {
        return surpass_percentage;
    }

    public void setSurpass_percentage(double surpass_percentage) {
        this.surpass_percentage = surpass_percentage;
    }

    public String getLogin_count() {
        return login_count;
    }

    public void setLogin_count(String login_count) {
        this.login_count = login_count;
    }

    public String getBrowse_count() {
        return browse_count;
    }

    public void setBrowse_count(String browse_count) {
        this.browse_count = browse_count;
    }

    public String getBrowse_distinct_count() {
        return browse_distinct_count;
    }

    public void setBrowse_distinct_count(String browse_distinct_count) {
        this.browse_distinct_count = browse_distinct_count;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

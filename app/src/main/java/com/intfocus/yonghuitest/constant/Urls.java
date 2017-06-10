package com.intfocus.yonghuitest.constant;

import android.support.v7.appcompat.BuildConfig;

/**
 * URL常量类
 **/
public class Urls {

    /**
     * 是否为生产环境
     */
    public static boolean isRelease = BuildConfig.DEBUG;

    public static String URL_ROOT = isRelease ? "http://api.lhjrc.com:81/webapi/api/" : "http://139.129.28.188:8084/api/";

}

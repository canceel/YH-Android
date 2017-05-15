package com.intfocus.yonghuitest.kpi.constant;

/**
 * @description:零时数据存储类
 * @author: baoliang.zhang  zbaoliang@outlook.com
 * @date: 2016/8/12 14:04
 **/
public class TeamDataUtil {

    private static TeamDataUtil instance;

    private TeamDataUtil() {
    }

    public static TeamDataUtil getInstance() {
        if (instance == null)
            instance = new TeamDataUtil();
        return instance;
    }
}

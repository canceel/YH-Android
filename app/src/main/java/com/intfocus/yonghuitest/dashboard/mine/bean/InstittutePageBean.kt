package com.intfocus.yonghuitest.dashboard.mine.bean

/**
 * Created by CANC on 2017/7/25.
 */
class InstittutePageBean {

    /**
    "currPage":1,
    "pageSize":10,
    "totalCount":11,
    "totalPage":2
     * code : 200
     */


    var currPage: Int = 0
    var pageSize: Int = 0
    var totalCount: Int = 0
    var totalPage: Int = 0
    var code: Int = 0
    var list: List<InstituteDataBean> = listOf()
}

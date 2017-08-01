package com.intfocus.yonghuitest.dashboard.report.mode

import com.intfocus.yonghuitest.dashboard.report.mode.CategoryBean

/**
 * Created by liuruilin on 2017/6/16.
 */
class ReportListPageRequest(var isSuccess: Boolean, var state: Int) {
    var categroy_list: List<CategoryBean>? = null
}

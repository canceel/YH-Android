package com.intfocus.yonghuitest.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.yonghuitest.dashboard.report.mode.CategoryBean
import com.intfocus.yonghuitest.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class ReportListResult: BaseResult() {
    @SerializedName("data")
    var data:  List<CategoryBean>? = null
}
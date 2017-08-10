package com.intfocus.yonghuitest.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.yonghuitest.data.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */
class HomeMsgResult : BaseResult() {
    @SerializedName("data")
    var data: List<KpiGroupItem>? = null
}
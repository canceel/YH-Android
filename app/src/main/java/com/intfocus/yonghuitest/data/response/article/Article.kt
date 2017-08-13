package com.intfocus.yonghuitest.data.response.article

import com.google.gson.annotations.SerializedName
import com.intfocus.yonghuitest.dashboard.mine.bean.InstittutePageBean
import com.intfocus.yonghuitest.dashboard.mine.bean.InstituteDataBean
import com.intfocus.yonghuitest.data.Pagination
import com.intfocus.yonghuitest.data.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */

class ArticleResult : BaseResult() {
    var current_page: Int = 0
    var page_size: Int = 0
    var total_count: Int = 0
    var total_page: Int = 0

    @SerializedName("data")
    var data: List<InstituteDataBean>? = null
}
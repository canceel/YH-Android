package com.intfocus.yonghuitest.data.response.article

import com.google.gson.annotations.SerializedName
import com.intfocus.yonghuitest.dashboard.mine.bean.InstittutePageBean
import com.intfocus.yonghuitest.data.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */

class ArticleResult : BaseResult() {
    @SerializedName("page")
    var data: InstittutePageBean? = null
}
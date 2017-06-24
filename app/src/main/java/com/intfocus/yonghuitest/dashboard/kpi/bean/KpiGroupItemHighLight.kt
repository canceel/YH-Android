package com.intfocus.yonghuitest.dashboard.kpi.bean

import java.io.Serializable

/**
 * Created by liuruilin on 2017/6/21.
 */
class KpiGroupItemHighLight: Serializable {
    var percentage: Boolean = false
    var number: String? = null
    var compare: String? = null
    var arrow: Int = 0
}
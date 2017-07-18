package com.intfocus.yh_android.dashboard.kpi.bean

import java.io.Serializable

/**
 * Created by liuruilin on 2017/6/21.
 */
class KpiResultData: Serializable {
    var code: Int = 0
    var message: String? = null
    var data: List<KpiGroup>? = null
}

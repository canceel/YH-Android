package com.intfocus.yonghuitest.dashboard.kpi.bean

import java.io.Serializable

/**
 * Created by liuruilin on 2017/6/21.
 */
class KpiGroupItem: Serializable {
    var title: String? = null
    var dashboard_type: String? = null
    var target_url: String? = null
    var unit: String? = null
    var memo1: String? = null
    var memo2: String? = null
    var data: KpiGroupItemData? = null
    var group_name: String? = null
    var is_stick: Boolean = false
}

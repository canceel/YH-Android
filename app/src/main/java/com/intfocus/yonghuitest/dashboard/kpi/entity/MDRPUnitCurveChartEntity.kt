package com.intfocus.yonghuitest.dashboard.kpi.entity

import java.io.Serializable
import java.util.ArrayList

/**
 * 仪表盘实体对象
 * Created by zbaoliang on 17-4-28.
 */
class MDRPUnitCurveChartEntity : Serializable {
    var chart_type: String? = null
    var legend: Array<String>? = null
    var series: ArrayList<SeriesEntity>? = null
    var title: String? = null
    var xAxis: Array<String>? = null
    var yAxis: Array<String>? = null
    var stateCode = 200

    inner class SeriesEntity : Serializable {
        var data: String? = null
        var name: String? = null
        var type: String? = null
    }
}
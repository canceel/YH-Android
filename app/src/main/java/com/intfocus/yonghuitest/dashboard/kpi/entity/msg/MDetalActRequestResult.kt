package com.intfocus.yonghuitest.dashboard.kpi.entity.msg

import com.intfocus.yonghuitest.dashboard.kpi.entity.MererDetalEntity

import java.util.ArrayList

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
class MDetalActRequestResult(var isSuccess: Boolean, var stateCode: Int, var datas: ArrayList<MererDetalEntity>)

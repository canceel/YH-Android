package com.intfocus.yonghuitest.dashboard.App

import android.content.Context
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.mode.ListPageMode
import com.zbl.lib.baseframe.core.Subject

/**
 * 主页 - 专题
 * Created by liuruilin on 2017/6/15.
 */
class AppFragment(): BaseModeFragment<ListPageMode>() {
    lateinit var ctx: Context

    override fun setSubject(): Subject {
        ctx = act.applicationContext
        return ListPageMode(ctx)
    }
}
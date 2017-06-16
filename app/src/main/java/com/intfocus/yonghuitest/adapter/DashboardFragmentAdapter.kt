package com.intfocus.yonghuitest.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup

import com.intfocus.yonghuitest.dashboard.DashboardActivity
import com.intfocus.yonghuitest.dashboard.fragment.AnalysisFragment
import com.intfocus.yonghuitest.dashboard.fragment.AppFragment
import com.intfocus.yonghuitest.dashboard.fragment.KpiFragment
import com.intfocus.yonghuitest.dashboard.message.MessageFragment
import com.intfocus.yonghuitest.dashboard.kpi.MeterFragment

/**
 * Created by liuruilin on 2017/3/23.
 */

class DashboardFragmentAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val PAGER_COUNT = 4
    private var mKpiFragment = KpiFragment()
    private var mMeterFragment = MeterFragment()
    private var mAnalysisFragment = AnalysisFragment()
    private var mAppFragment = AppFragment()
    private var mMessageFragment = MessageFragment()

    override fun getCount(): Int {
        return PAGER_COUNT
    }

    override fun instantiateItem(vg: ViewGroup, position: Int): Any {
        return super.instantiateItem(vg, position)
    }

    override fun destroyItem(container: ViewGroup?, position: Int, `object`: Any) {
        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            DashboardActivity.PAGE_KPI -> return mMeterFragment
            DashboardActivity.PAGE_ANALYSIS -> return mAnalysisFragment
            DashboardActivity.PAGE_APP -> return mAppFragment
            DashboardActivity.PAGE_MESSAGE -> return mMessageFragment
        }
        return mMeterFragment
    }
}

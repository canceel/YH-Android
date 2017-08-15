package com.intfocus.yonghuitest.dashboard

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.ViewGroup
import com.intfocus.yonghuitest.dashboard.kpi.HomeFragment

import com.intfocus.yonghuitest.dashboard.mine.MinePageFragment
import com.intfocus.yonghuitest.dashboard.report.ReportFragment
import com.intfocus.yonghuitest.dashboard.work_box.WorkBoxFragment

/**
 * Created by liuruilin on 2017/3/23.
 */

class DashboardFragmentAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    private val PAGER_COUNT = 4
//    private var mKpiFragment = KpiFragment()
    private var mMeterFragment = HomeFragment()
    private var mAnalysisFragment = ReportFragment()
    private var mAppFragment = WorkBoxFragment()
    private var mMessageFragment = MinePageFragment()

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
            DashboardActivity.PAGE_REPORTS -> return mAnalysisFragment
            DashboardActivity.PAGE_APP -> return mAppFragment
            DashboardActivity.PAGE_MINE -> return mMessageFragment
        }
        return mMeterFragment
    }
}

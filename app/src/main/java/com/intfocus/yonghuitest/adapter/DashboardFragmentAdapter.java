package com.intfocus.yonghuitest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.intfocus.yonghuitest.dashboard.DashboardActivity;
import com.intfocus.yonghuitest.dashboard.fragment.AnalysisFragment;
import com.intfocus.yonghuitest.dashboard.fragment.AppFragment;
import com.intfocus.yonghuitest.dashboard.fragment.KpiFragment;
import com.intfocus.yonghuitest.dashboard.fragment.MessageFragment;
import com.intfocus.yonghuitest.dashboard.kpi.ui.MeterFragment;

/**
 * Created by liuruilin on 2017/3/23.
 */

public class DashboardFragmentAdapter extends FragmentPagerAdapter {

    private final int PAGER_COUNT = 4;
    private MeterFragment mMeterFragment = null;
    private KpiFragment mKpiFragment = null;
    private AnalysisFragment mAnalysisFragment = null;
    private AppFragment mAppFragment = null;
    private MessageFragment mMessageFragment = null;

    public DashboardFragmentAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        mMeterFragment = new MeterFragment();
        mKpiFragment = new KpiFragment();
        mAnalysisFragment = new AnalysisFragment();
        mAppFragment = new AppFragment();
        mMessageFragment = new MessageFragment();
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case DashboardActivity.PAGE_KPI:
//                fragment = mKpiFragment;
                fragment = mMeterFragment;
                break;
            case DashboardActivity.PAGE_ANALYSIS:
                fragment = mAnalysisFragment;
                break;
            case DashboardActivity.PAGE_APP:
                fragment = mAppFragment;
                break;
            case DashboardActivity.PAGE_MESSAGE:
                fragment = mMessageFragment;
                break;
        }
        return fragment;
    }
}
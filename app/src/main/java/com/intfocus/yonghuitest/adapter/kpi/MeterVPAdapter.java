package com.intfocus.yonghuitest.adapter.kpi;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.intfocus.yonghuitest.bean.dashboard.kpi.MererEntity;
import com.intfocus.yonghuitest.dashboard.kpi.BarFragment_Bottom;
import com.intfocus.yonghuitest.dashboard.kpi.CurveLineFragment;
import com.intfocus.yonghuitest.dashboard.kpi.NumberFragment;
import com.intfocus.yonghuitest.dashboard.kpi.RingFragment;

import java.util.ArrayList;

/**
 * 实时数据适配器
 * Created by zbaoliang on 17-4-28.
 */
public class MeterVPAdapter extends FragmentStatePagerAdapter {

    Context ctx;
    ArrayList<MererEntity> topDatas;

    public MeterVPAdapter(Context ctx, FragmentManager fm, ArrayList<MererEntity> topDatas) {
        super(fm);
        this.ctx = ctx;
        this.topDatas = topDatas;
    }

    @Override
    public int getCount() {
        if (topDatas == null)
            return 0;
        else
            return topDatas.size();
    }

    @Override
    public Fragment getItem(int position) {
        MererEntity entity = topDatas.get(position);
        Fragment fragment = null;
        switch (entity.getDashboard_type()) {
            case "line"://折线图
                fragment = CurveLineFragment.newInstance(entity);
                break;

            case "bar"://柱状图
                fragment = BarFragment_Bottom.newInstance(entity);
                break;

            case "ring"://环形图
                fragment = RingFragment.newInstance(entity);
                break;

            case "number"://纯文本
                fragment = NumberFragment.newInstance(entity);
                break;
        }

        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        // 返回发生改变，让系统重新加载（强制刷新）
        // 系统默认返回的是     POSITION_UNCHANGED，未改变
        return POSITION_NONE;
    }

}


package com.intfocus.yonghuitest.base;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.kpi.mode.MeterMode;

/**
 * Created by liuruilin on 2017/5/11.
 */

public abstract class BaseSwipeFragment extends BaseTableFragment<MeterMode> implements SwipeRefreshLayout.OnRefreshListener {
    public SwipeRefreshLayout mSwipeLayout;

    public void initSwipeLayout(View view) {
        mSwipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light);
        mSwipeLayout.setDistanceToTriggerSync(300);// 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeLayout.setSize(SwipeRefreshLayout.DEFAULT);
    }

    @Override
    public void onRefresh() {
        getModel().requestData();
    }
}

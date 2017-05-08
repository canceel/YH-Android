package com.intfocus.yonghuitest.dashboard.kpi;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intfocus.yonghuitest.BaseTableFragment;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.adapter.kpi.MarginDecoration;
import com.intfocus.yonghuitest.adapter.kpi.MeterVPAdapter;
import com.intfocus.yonghuitest.adapter.kpi.SaleDataAdapter;
import com.intfocus.yonghuitest.bean.kpi.MererEntity;
import com.intfocus.yonghuitest.bean.kpi.MeterClickEventEntity;
import com.intfocus.yonghuitest.bean.kpi.msg.MeterRequestResult;
import com.intfocus.yonghuitest.listen.CustPagerTransformer;
import com.intfocus.yonghuitest.listen.RealtimeTitleClickListener;
import com.intfocus.yonghuitest.mode.MeterMode;
import com.intfocus.yonghuitest.util.DisplayUtil;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.StringUtil;
import com.zbl.lib.baseframe.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import sumimakito.android.advtextswitcher.AdvTextSwitcher;
import sumimakito.android.advtextswitcher.Switcher;

/**
 * 仪表盘
 * Created by zbaoliang on 17-4-26.
 */
public class MeterFragment extends BaseTableFragment<MeterMode> {

    Context ctx;

    View rootView;

    @ViewInject(R.id.tv_realtime_title_meter)
    AdvTextSwitcher tv_realtime_title;

    @ViewInject(R.id.vp_meter)
    ViewPager vp_meter;
    MeterVPAdapter vpAdapter;
    ArrayList<MererEntity> topDatas = new ArrayList<>();


    HashMap<String, ArrayList<MererEntity>> gropsName = new HashMap();


    @ViewInject(R.id.meter_group)
    LinearLayout ll_meterGroupContainer;

//    @ViewInject(R.id.recyclerview_meter)
//    RecyclerView recyclerView;
//    SaleDataAdapter recycleAdapter;
//    ArrayList<MererEntity> bodyDatas = new ArrayList<>();

    @Override
    public Subject setSubject() {
        ctx = act.getApplicationContext();
        return new MeterMode(ctx);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.meter_fragment, container, false);
            x.view().inject(this, rootView);
            initView();
            getModel().requestData();
        }
        return rootView;
    }

    /**
     * 初始化View
     */
    private void initView() {
        initAffiche();

        initViewPager();

//        initRecycleView();
    }

    /**
     * 初始化公告控件
     */
    private void initAffiche() {
        String[] texts = getResources().getStringArray(R.array.array_affiche);
        tv_realtime_title.setTexts(texts);
        tv_realtime_title.setCallback(new RealtimeTitleClickListener());
        new Switcher().attach(tv_realtime_title).setDuration(3000).start();
    }

    /**
     * 初始化滑动区域模块
     */
    private void initViewPager() {
        vp_meter.setPageTransformer(false, new CustPagerTransformer(act.getApplicationContext()));
        vp_meter.setOffscreenPageLimit(3);
        vpAdapter = new MeterVPAdapter(ctx, getActivity().getSupportFragmentManager(), topDatas);
        vp_meter.setAdapter(vpAdapter);
        vp_meter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                vp_meter.requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


/*        vp_meter.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (lastSelectIndex == position)
                    return;

                if (lastSelectIndex > position)
                    tv_realtime_title.previous();
                else
                    tv_realtime_title.next();

                lastSelectIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });*/
    }

    /**
     * 数据请求回调方法
     *
     * @param result
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MeterRequestResult result) {
        if (!result.isSuccress) {
            ToastUtil.showToast(ctx, "数据请求失败，errorCode:" + result.stateCode);
            return;
        }
        topDatas.clear();
        topDatas.addAll(result.topDatas);
        vpAdapter.notifyDataSetChanged();

        gropsName.clear();
        ll_meterGroupContainer.removeAllViews();

        Iterator<MererEntity> iterator = result.bodyDatas.iterator();
        while (iterator.hasNext()) {
            MererEntity entity = iterator.next();
            String group_name = entity.group_name;
            if (!StringUtil.isEmpty(group_name)) {
                ArrayList<MererEntity> list = gropsName.get(group_name);
                if (list == null || list.isEmpty()) {
                    list = new ArrayList();
                    gropsName.put(group_name, list);
                }
                list.add(entity);
            }
        }

        Set<String> set = gropsName.keySet();
        for (String s : set) {
            ArrayList<MererEntity> list = gropsName.get(s);
            LayoutInflater inflater = LayoutInflater.from(ctx);
            View view = inflater.inflate(R.layout.item_meter_group, null);
            TextView tv_title = (TextView) view.findViewById(R.id.tv_item_meter_group);
            tv_title.setText(list.get(0).group_name);
            RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview_meter);
            initRecycleView(recyclerView, list);
            ll_meterGroupContainer.addView(view);
        }

        rootView.invalidate();
    }

    /**
     * 初始化销售数据模块
     */
    private void initRecycleView(RecyclerView recyclerView, ArrayList<MererEntity> bodyDatas) {
        int offset = DisplayUtil.dip2px(ctx, 3);
        recyclerView.setPadding(offset, offset, offset, offset);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
//        layoutManager.setAutoMeasureEnabled(false);
        //设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置Adapter
        SaleDataAdapter recycleAdapter = new SaleDataAdapter(ctx, getActivity().getSupportFragmentManager(), bodyDatas);
        recyclerView.setAdapter(recycleAdapter);
        //设置分隔线
        recyclerView.addItemDecoration(new MarginDecoration(ctx));
        //设置增加或删除条目的动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    /**
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MeterClickEventEntity entity) {
        if (entity != null) {
            ToastUtil.showToast(ctx, entity.entity.toString());
        } else
            ToastUtil.showToast(ctx, "数据实体为空");
    }
}

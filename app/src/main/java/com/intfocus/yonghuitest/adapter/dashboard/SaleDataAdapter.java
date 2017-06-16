package com.intfocus.yonghuitest.adapter.dashboard;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.intfocus.yonghuitest.bean.dashboard.kpi.MererEntity;
import com.intfocus.yonghuitest.dashboard.kpi.BarFragment_Top;
import com.intfocus.yonghuitest.dashboard.kpi.BrokenLineFragment;
import com.intfocus.yonghuitest.dashboard.kpi.NumberFragment;
import com.intfocus.yonghuitest.dashboard.kpi.RingFragment;
import com.zbl.lib.baseframe.utils.PhoneUtil;

import java.util.ArrayList;
import java.util.Random;

/**
 * 销售数据适配器
 * Created by zbaoliang on 17-4-28.
 */
public class SaleDataAdapter extends RecyclerView.Adapter<SaleDataAdapter.SaleDataHolder> {

    Context ctx;
    FragmentManager fm;
    ArrayList<MererEntity> bodyDatas;

    int viewWidth;
    int viewHeigt;

    public SaleDataAdapter(Context ctx, FragmentManager fm, ArrayList<MererEntity> bodyDatas) {
        this.fm = fm;
        this.bodyDatas = bodyDatas;

        int sw = PhoneUtil.getScreenWidth(ctx);
        viewWidth = sw / 2;
        viewHeigt = (int) (viewWidth * 0.8);
    }

    @Override
    public SaleDataHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout layout = new FrameLayout(parent.getContext());
        Random random = new Random();
        int id = random.nextInt(201505);
        layout.setId(id);
        SaleDataHolder holder = new SaleDataHolder(layout, viewType);
        return holder;
    }

    @Override
    public void onBindViewHolder(SaleDataHolder holder, int position) {
//        ViewGroup.LayoutParams lparams = holder.layout.getLayoutParams();
//        lparams.width = 100;
//        lparams.height = 200;
        ViewGroup.LayoutParams lparams = null;
        switch (holder.entity.getDashboard_type()) {
            case "line"://折线图
            case "bar"://柱状图
                lparams = new ViewGroup.LayoutParams(viewWidth, viewHeigt*2);
                break;

            case "ring"://环形图
            case "number"://纯文本
                lparams = new ViewGroup.LayoutParams(viewWidth, viewHeigt);
                break;
        }
        holder.layout.setLayoutParams(lparams);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (bodyDatas == null)
            return 0;
        return bodyDatas.size();
    }

    class SaleDataHolder extends RecyclerView.ViewHolder {

        FrameLayout layout;
        MererEntity entity;

        public SaleDataHolder(View view, int viewType) {
            super(view);
            this.layout = (FrameLayout) view;
            entity = bodyDatas.get(viewType);
            Fragment fragment = null;
            switch (entity.getDashboard_type()) {
                case "line"://折线图
                    fragment = BrokenLineFragment.newInstance(entity);
                    break;

                case "bar"://柱状图
                    fragment = BarFragment_Top.newInstance(entity);
                    break;

                case "ring"://环形图
                    fragment = RingFragment.newInstance(entity);
                    break;

                case "number"://纯文本
                    fragment = NumberFragment.newInstance(entity);
                    break;
            }
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(view.getId(), fragment);
            ft.commit();
        }
    }
}

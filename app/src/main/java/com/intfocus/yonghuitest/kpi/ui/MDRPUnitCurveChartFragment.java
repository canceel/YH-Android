package com.intfocus.yonghuitest.kpi.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseFragment;
import com.intfocus.yonghuitest.base.BaseTableFragment;
import com.intfocus.yonghuitest.kpi.entity.MDRPUitlSeries;
import com.intfocus.yonghuitest.kpi.entity.MDRPUnitCurveChartEntity;
import com.intfocus.yonghuitest.kpi.mode.MDRPUnitCurveChartMode;
import com.intfocus.yonghuitest.kpi.view.CustomCurveChart;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.StringUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 仪表盘-详情页面-根页签-曲线图单元
 */
public class MDRPUnitCurveChartFragment extends BaseTableFragment<MDRPUnitCurveChartMode> {
    private static final String ARG_PARAM1 = "param1";
    private String targetID;
    private String mParam1;
    private Context ctx;
    private View rootView;

    @ViewInject(R.id.ll_mdrpUnit_curvechart)
    private LinearLayout ll_curvechart;


    @Override
    public Subject setSubject() {
        targetID = getTag();
        ctx = act.getApplicationContext();
        return new MDRPUnitCurveChartMode(ctx, targetID);
    }

    public static MDRPUnitCurveChartFragment newInstance(String param1) {
        MDRPUnitCurveChartFragment fragment = new MDRPUnitCurveChartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdunit_curve_chart, container, false);
            x.view().inject(this, rootView);
            getModel().analysisData(mParam1);
        }
        return rootView;
    }


    /**
     * 图表点击事件统一处理方法
     */
    public void onMessageEvent(final MDRPUnitCurveChartEntity entity) {
        if (entity != null && entity.stateCode == 200) {
            dealData(entity);
        }
    }

    /**
     * 绑定数据
     */
    private void dealData(MDRPUnitCurveChartEntity result) {
        final String[] xLabel = result.xAxis;
        String[] yLabel = new String[5];
        int[] color = null;
        int YMaxValue;
        ArrayList<Float> series1 = new ArrayList<>();
        ArrayList<Float> series2 = new ArrayList<>();
        ArrayList<Float> seriesA = new ArrayList<>();

        ArrayList<MDRPUnitCurveChartEntity.SeriesEntity> arrays = result.series;
        if (arrays.size() == 2) {
            String datas = arrays.get(0).data;
            if (datas.startsWith("[{")) {
                ArrayList<MDRPUitlSeries> list = (ArrayList<MDRPUitlSeries>) JSON.parseArray(datas, MDRPUitlSeries.class);
                color = new int[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    MDRPUitlSeries series = list.get(i);
                    color[i] = series.color;
                    series1.add(series.value);
                }
            }
            String datass = arrays.get(1).data;
            datass = datass.trim().substring(1, datass.length() - 1).trim();
            String[] topW = datass.trim().split("[^\\d]");
            for (String s : topW) {
                if (StringUtil.isEmpty(s))
                    continue;
                series2.add(Float.valueOf(s));
            }
            seriesA.addAll(series1);
            seriesA.addAll(series2);
            Collections.sort(seriesA);
            YMaxValue = seriesA.get(seriesA.size() - 1).intValue();
            while (YMaxValue % 4 != 0)
                YMaxValue++;

            int part = YMaxValue / 4;
            for (int i = 0; i < 5; i++) {
                yLabel[i] = String.valueOf(part * i);
            }
            act.runOnUiThread(new UIRunnable(xLabel, yLabel, color, series1, series2));

            Log.d("TAG", seriesA.get(0) + ":" + seriesA.get(seriesA.size() - 1));
        }
    }

    class UIRunnable implements Runnable {
        String[] xLabel;
        String[] yLabel;
        int[] color;
        ArrayList<Float> series1 = new ArrayList<>();
        ArrayList<Float> series2 = new ArrayList<>();
        List<Float[]> data = new ArrayList<>();

        public UIRunnable(String[] xLabel, String[] yLabel, int[] color, ArrayList<Float> series1, ArrayList<Float> series2) {
            this.xLabel = xLabel;
            this.yLabel = yLabel;
            this.color = color;
            this.series1 = series1;
            this.series2 = series2;
            data.add(series1.toArray(new Float[series1.size()]));
            data.add(series2.toArray(new Float[series2.size()]));
        }

        @Override
        public void run() {
            CustomCurveChart chart = new CustomCurveChart(act, xLabel, yLabel, data, color);
            ll_curvechart.addView(chart);
        }
    }


    class SeriesComparator implements Comparator {
        public int compare(Object obj1, Object obj2) {
            MDRPUitlSeries u1 = (MDRPUitlSeries) obj1;
            MDRPUitlSeries u2 = (MDRPUitlSeries) obj2;
            if (u1.value > u2.value) {
                return 1;
            } else if (u1.value < u2.value) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}


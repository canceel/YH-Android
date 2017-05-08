package com.intfocus.yonghuitest.dashboard.kpi;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intfocus.yonghuitest.BaseFragment;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.bean.kpi.MererEntity;
import com.intfocus.yonghuitest.bean.kpi.MeterClickEventEntity;
import com.intfocus.yonghuitest.view.MeterCursor;
import com.jonas.jgraph.graph.NChart;
import com.jonas.jgraph.models.NExcel;
import com.zbl.lib.baseframe.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


/**
 * 柱状图
 */
public class BarFragment_Bottom extends BaseFragment {
    private MererEntity entity;

    private View rootView;

    @ViewInject(R.id.ll_ragment_bar)
    private View ll_ragmentlayout;
    @ViewInject(R.id.tv_title_vpitem)
    private TextView tv_title;
    @ViewInject(R.id.tv_number_vpitem)
    private TextView tv_number;
    @ViewInject(R.id.tv_compare_vpitem)
    private TextView tv_compare;
    @ViewInject(R.id.tv_vpitem_unit)
    private TextView tv_unit;

    @ViewInject(R.id.img_vpitem)
    private MeterCursor img_cursor;

    @ViewInject(R.id.nchart_linefragment)
    private NChart mChart;

    public BarFragment_Bottom() {
    }

    public static BarFragment_Bottom newInstance(MererEntity entity) {
        BarFragment_Bottom fragment = new BarFragment_Bottom();
        Bundle args = new Bundle();
        args.putSerializable("Entity", entity);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            entity = (MererEntity) getArguments().getSerializable("Entity");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_bar_bottom, container, false);
            x.view().inject(this, rootView);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    init();
                }
            }, 100);
        }
        return rootView;
    }


    private void init() {
        if (entity.is_stick == 0) {
            rootView.post(new Runnable() {
                @Override
                public void run() {
                    int vpWidth = rootView.getWidth();
                    int height = vpWidth * 2;
                    ViewGroup.LayoutParams lp = ll_ragmentlayout.getLayoutParams();
                    lp.height = height;
                    ll_ragmentlayout.setLayoutParams(lp);
                    Log.d("Log", ll_ragmentlayout.getWidth() + ":" + ll_ragmentlayout.getHeight());
                }
            });
        }else {
            mChart.setInterceptAnimate(true);
        }

        tv_title.setText(entity.title);
        String number = entity.data.high_light.number;
        if (!StringUtil.isEmpty(number)) {
            tv_number.setText(number);
            tv_unit.setText(entity.unit);
        }

        MererEntity.LineEntity.HighLight high_light = entity.data.high_light;
        if (high_light.percentage == 1) {//显示
            img_cursor.setCursorState(high_light.arrow);
            if (high_light.arrow < 3) {//上箭头
                tv_compare.setText("+" + high_light.compare + "%");
            } else {
                tv_compare.setText("-" + high_light.compare + "%");
            }
        } else {
            img_cursor.setVisibility(View.GONE);
        }

        int[] chart_data = entity.data.chart_data;
        int size = chart_data.length;

        if (size > 0) {
            List<NExcel> nExcelList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                nExcelList.add(new NExcel(chart_data[i], ""));
            }
            mChart.setScrollAble(false);
            mChart.setFixedWidth(16);
            mChart.setNormalColor(Color.parseColor("#4EB5D7"));
            mChart.setTextPrintColor(Color.parseColor("#4EB5D7"));
            mChart.setDrawTextUnit(entity.unit);
//            mChart.setBarStanded(10);
            mChart.setBarAniStyle(NChart.ChartAniStyle.BAR_UP);//TODO 动画效果
            mChart.setChartStyle(NChart.ChartStyle.BAR);//TODO 曲线图
            mChart.setLinePointRadio(16);//设置折线转则点圆点半径
            mChart.setHCoordinate(0);
            mChart.setAbove(0);
            mChart.setSelectedModed(NChart.SelectedMode.selectedActivated);
            mChart.setSelectedItem(size - 1);//设置最后一条数据为选中状态
            mChart.setInterceptTouchEvent(true);
            mChart.setInterceptDrawText(true);
            mChart.cmdFill(nExcelList);
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MeterClickEventEntity(entity));
            }
        });
    }
}

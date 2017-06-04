package com.intfocus.yonghuitest.dashboard.kpi.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseTableFragment;
import com.intfocus.yonghuitest.dashboard.kpi.constant.Constant;
import com.intfocus.yonghuitest.dashboard.kpi.entity.MererEntity;
import com.intfocus.yonghuitest.dashboard.kpi.entity.MeterClickEventEntity;
import com.intfocus.yonghuitest.dashboard.kpi.view.MeterCursor;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;

/**
 * 纯数字文本表
 */
public class NumberFragment extends BaseTableFragment {
    private MererEntity entity;
    private int[] colors = Constant.colorsRGY;
    private View rootView;

    @ViewInject(R.id.ll_ragment_number)
    private View ll_ragmentlayout;

    @ViewInject(R.id.tv_title_vpitem)
    private TextView tv_title;
    @ViewInject(R.id.tv_number_vpitem)
    private TextView tv_number;
    @ViewInject(R.id.tv_vpitem_unit)
    private TextView tv_unit;
    @ViewInject(R.id.tv_compare_vpitem)
    private TextView tv_compare;
    @ViewInject(R.id.img_vpitem)
    private MeterCursor img_cursor;
    @ViewInject(R.id.ll_vp)
    private LinearLayout ll_vp;

    DecimalFormat df = new DecimalFormat("#.00");

    public NumberFragment() {
    }

    public static NumberFragment newInstance(MererEntity entity) {

        NumberFragment fragment = new NumberFragment();
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
            rootView = inflater.inflate(R.layout.fragment_number, container, false);
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
        tv_title.setText(entity.getTitle());
        double number = entity.getData().getHigh_light().getNumber();
        if (number == 0.123456789) {
//            tv_title.setTextSize(20);
            tv_number.setVisibility(View.GONE);
            ll_vp.setVisibility(View.GONE);
            tv_unit.setVisibility(View.GONE);
        }
        else {
            tv_number.setText(formatNumber(String.valueOf(df.format(number))));
        }

        tv_unit.setText(entity.getUnit());

            MererEntity.LineEntity.HighLight high_light = entity.getData().getHigh_light();
            if (high_light.getCompare() != 0) {//显示百分比
            float compare = (float) ((high_light.getNumber() - high_light.getCompare()) / high_light.getCompare() * 100);
            if (high_light.getNumber() - high_light.getCompare() > 0) {//上箭头
                tv_compare.setText("+" + df.format(compare) + "%");
            } else {
                tv_compare.setText("" + df.format(compare) + "%");
            }
        }

        if (high_light.getArrow() >= 0) {
            img_cursor.setVisibility(View.VISIBLE);
            tv_compare.setTextColor(colors[high_light.getArrow()]);
            tv_number.setTextColor(colors[high_light.getArrow()]);
        } else {
            img_cursor.setVisibility(View.GONE);
            tv_number.setTextColor(Color.BLACK);
        }

        img_cursor.setCursorState(high_light.getArrow());

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MeterClickEventEntity(entity));
            }
        });
    }
}

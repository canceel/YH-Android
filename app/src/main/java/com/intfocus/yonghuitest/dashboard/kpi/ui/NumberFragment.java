package com.intfocus.yonghuitest.dashboard.kpi.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        tv_title.setText(entity.title);
        double number = entity.data.high_light.number;
        if (number==0)
            tv_number.setText("0");
        else
            tv_number.setText(formatNumber(String.valueOf(df.format(number))));

        tv_number.setTextColor(colors[entity.data.high_light.arrow]);
        tv_unit.setText(entity.unit);

        MererEntity.LineEntity.HighLight high_light = entity.data.high_light;
        if (high_light.compare != 0) {//显示百分比
            float compare = (float) ((high_light.number - high_light.compare) / high_light.compare * 100);
            if (high_light.number-high_light.compare>0) {//上箭头
                tv_compare.setText("+" + df.format(compare) + "%");
            } else {
                tv_compare.setText("" + df.format(compare) + "%");
            }
        }

        if (high_light.arrow >= 0) {
            img_cursor.setVisibility(View.VISIBLE);
            tv_compare.setTextColor(colors[high_light.arrow]);
            tv_number.setTextColor(colors[high_light.arrow]);
        } else {
            img_cursor.setVisibility(View.GONE);
        }

        img_cursor.setCursorState(high_light.arrow);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MeterClickEventEntity(entity));
            }
        });
    }
}

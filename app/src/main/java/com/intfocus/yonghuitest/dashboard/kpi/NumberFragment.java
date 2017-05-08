package com.intfocus.yonghuitest.dashboard.kpi;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.intfocus.yonghuitest.BaseFragment;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.bean.kpi.MererEntity;
import com.intfocus.yonghuitest.bean.kpi.MeterClickEventEntity;
import com.zbl.lib.baseframe.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 纯数字文本表
 */
public class NumberFragment extends BaseFragment {
    private MererEntity entity;

    private View rootView;

    @ViewInject(R.id.ll_ragment_number)
    private View ll_ragmentlayout;

    @ViewInject(R.id.tv_title_vpitem)
    private TextView tv_title;
    @ViewInject(R.id.tv_number_vpitem)
    private TextView tv_number;
    @ViewInject(R.id.tv_vpitem_unit)
    private TextView tv_unit;

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
        String number = entity.data.high_light.number;
        if (!StringUtil.isEmpty(number)) {
            tv_number.setText(number);
            tv_unit.setText(entity.unit);
        }

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new MeterClickEventEntity(entity));
            }
        });
    }
}

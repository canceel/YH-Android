package com.intfocus.yonghuitest.dashboard.kpi;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseModeFragment;
import com.intfocus.yonghuitest.bean.dashboard.kpi.MDetalUnitEntity;
import com.intfocus.yonghuitest.bean.dashboard.kpi.MDetalRootPageRequestResult;
import com.intfocus.yonghuitest.mode.MDetalRootPageMode;
import com.zbl.lib.baseframe.core.Subject;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Random;


/**
 * 仪表盘详情页面根标签页面
 */
public class MDetalRootPageFragment extends BaseModeFragment<MDetalRootPageMode> {
    private static final String ARG_PARAM = "param";
    private String mParam;

    private Context ctx;
    private View rootView;

    private FragmentManager fm;

    @ViewInject(R.id.ll_mdrp_container)
    private LinearLayout ll_mdrp_container;

    @Override
    public Subject setSubject() {
        ctx = act.getApplicationContext();
        return new MDetalRootPageMode(ctx);
    }

    public static MDetalRootPageFragment newInstance(String param) {
        MDetalRootPageFragment fragment = new MDetalRootPageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fm = getFragmentManager();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_mdetal, container, false);
            x.view().inject(this, rootView);
            getModel().analysisData(mParam);
        }
        this.getId();
        return rootView;
    }

    /**
     * 图表点击事件统一处理方法
     */
    public void onMessageEvent(final MDetalRootPageRequestResult entity) {
        if (entity != null && entity.getStateCode() == 200) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bindData(entity);
                }
            });
        }
    }

    /**
     * 绑定数据
     */
    private void bindData(MDetalRootPageRequestResult result) {
        ArrayList<MDetalUnitEntity> datas = result.getDatas();
        int size = datas.size();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            Fragment fragment = null;
            MDetalUnitEntity entity = datas.get(i);
            switch (entity.getType()) {
                case "banner"://标题栏
                    fragment = MDRPUnitBannerFragment.newInstance(entity.getConfig());
                    break;

                case "chart"://曲线图表
                    fragment = MDRPUnitCurveChartFragment.newInstance(entity.getConfig());
                    break;

                case "info"://一般标签(附标题)
                    break;

                case "single_value"://单值组件
                    break;

                case "line-or-bar"://柱状图(竖)
                    break;

                case "bargraph"://条状图(横)
                    break;

                case "tables#v3"://类Excel冻结横竖首列表格
                    break;
            }

            if (fragment != null) {
                FrameLayout layout = new FrameLayout(ctx);
                int id = random.nextInt(201705);
                layout.setId(id);
                ll_mdrp_container.addView(layout);
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(layout.getId(), fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }
}

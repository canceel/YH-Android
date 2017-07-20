package com.intfocus.yonghuitest.subject.template_v2;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseModeFragment;
import com.intfocus.yonghuitest.subject.template_v2.bean.MDetalRootPageRequestResult;
import com.intfocus.yonghuitest.subject.template_v2.bean.MDetalUnitEntity;
import com.intfocus.yonghuitest.subject.template_v2.mode.MDetalRootPageMode;
import com.zbl.lib.baseframe.core.Subject;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Random;


/**
 * 模块二根标签页面
 */
public class ModularTwo_RootPageFragment extends BaseModeFragment<MDetalRootPageMode> {
    private static final String SU_ROOTID = "suRootID";
    private static final String ARG_PARAM = "param";
    private String mParam;

    private View rootView;

    private FragmentManager fm;

    @ViewInject(R.id.ll_mdrp_container)
    private LinearLayout ll_mdrp_container;

    /**
     * 最上层跟跟标签ID
     */
    private int suRootID;

    @Override
    public Subject setSubject() {
        return new MDetalRootPageMode(ctx);
    }

    public static ModularTwo_RootPageFragment newInstance(int suRootID,String param) {
        ModularTwo_RootPageFragment fragment = new ModularTwo_RootPageFragment();
        Bundle args = new Bundle();
        args.putInt(SU_ROOTID, suRootID);
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            suRootID = getArguments().getInt(SU_ROOTID);
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
        return rootView;
    }

    /**
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final MDetalRootPageRequestResult entity) {
        if (entity != null && entity.stateCode == 200) {
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
        ArrayList<MDetalUnitEntity> datas = result.datas;
        int size = datas.size();
        Random random = new Random();
        for (int i = 0; i < size; i++) {
            Fragment fragment = null;
            MDetalUnitEntity entity = datas.get(i);
            switch (entity.type) {
                case "banner"://标题栏
                    fragment = ModularTwo_UnitBannerFragment.newInstance(entity.config);
                    break;

                case "chart"://曲线图表/柱状图(竖)
                    fragment = ModularTwo_UnitCurveChartFragment.newInstance(entity.config);
                    break;

                case "info"://一般标签(附标题)
                    try {
                        View view = LayoutInflater.from(ctx).inflate(R.layout.item_info_layout, null);
                        TextView tv = (TextView) view.findViewById(R.id.tv_info);
                        String info = new JSONObject(entity.config).getString("title");
                        tv.setText(info);
                        ll_mdrp_container.addView(view);
                    } catch (Exception e) {
                    }
                    break;

                case "single_value"://单值组件
                    fragment = ModularTwo_UnitSingleValueFragment.newInstance(entity.config);
                    break;

                case "bargraph"://条状图(横)
                    fragment = ModularTwo_UnitPlusMinusChartFragment.newInstance(entity.config);
                    break;

                case "tables"://类Excel冻结横竖首列表格
                    fragment = ModularTwo_UnitTablesFragment.newInstance(suRootID,entity.config);
                    break;
            }

            if (fragment != null) {
                FrameLayout layout = new FrameLayout(ctx);
                AppBarLayout.LayoutParams params = new AppBarLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL
                        | AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                layout.setLayoutParams(params);
                int id = random.nextInt(Integer.MAX_VALUE);
                layout.setId(id);
                ll_mdrp_container.addView(layout);
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.replace(layout.getId(), fragment);
                ft.commitNow();
            }
        }
    }
}

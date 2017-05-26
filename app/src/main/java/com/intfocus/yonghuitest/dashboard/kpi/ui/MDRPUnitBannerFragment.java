package com.intfocus.yonghuitest.dashboard.kpi.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseTableFragment;
import com.zbl.lib.baseframe.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 标题栏 负责显示标题、日期、帮助说明
 */
public class MDRPUnitBannerFragment extends BaseTableFragment {
    private static final String ARG_PARAM1 = "param1";
    private String mParam1;

    private View rootView;


    @ViewInject(R.id.tv_mdrp_unit_banner_title)
    private TextView tv_title;

    @ViewInject(R.id.tv_mdrp_unit_banner_time)
    private TextView tv_time;

    @ViewInject(R.id.imgb_mdrp_unit_banner_info)
    private ImageButton imgb_info;
    private String info;

    public MDRPUnitBannerFragment() {
    }

    public static MDRPUnitBannerFragment newInstance(String param1) {
        MDRPUnitBannerFragment fragment = new MDRPUnitBannerFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_banner, container, false);
            x.view().inject(this, rootView);
            bindData();
        }
        return rootView;
    }

    private void bindData() {
        try {
            JSONObject jsonObject = new JSONObject(mParam1);
            if (jsonObject.has("title"))
                tv_title.setText(jsonObject.getString("title"));
            if (jsonObject.has("date")) {
                String date = jsonObject.getString("date");
                if (date.length() == 8) {
                    String yy = date.substring(0, 4);
                    String mm = date.substring(4, 6);
                    String dd = date.substring(6, 8);
                    tv_time.setText(yy + "/" + mm + "/" + dd);
                }
            }
            if (jsonObject.has("info"))
                this.info = jsonObject.getString("info");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Event(R.id.imgb_mdrp_unit_banner_info)
    private void onViewClick(View view) {
        ToastUtil.showToast(act, "showPopupWindow(); info=" + info);
    }
}

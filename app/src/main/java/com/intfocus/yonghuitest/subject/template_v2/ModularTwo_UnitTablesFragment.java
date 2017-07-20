package com.intfocus.yonghuitest.subject.template_v2;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseModeFragment;
import com.intfocus.yonghuitest.subject.template_v2.bean.MDetalRootPageRequestResult;
import com.intfocus.yonghuitest.subject.template_v2.mode.ModularTwo_UnitTablesParentMode;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.ToastUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Random;

/**
 * 表格根
 */
public class ModularTwo_UnitTablesFragment extends BaseModeFragment<ModularTwo_UnitTablesParentMode> {
    private String fragmentTag;
    private static final String SU_ROOTID = "suRootID";
    private static final String ARG_PARAM1 = "TablesParam";

    private String mParam;

    private View rootView;

    @ViewInject(R.id.fl_mdetal_table_title_container)
    private FrameLayout fl_titleContainer;
    @ViewInject(R.id.fl_mdetal_table_cont_container)
    private FrameLayout fl_contContainer;

    private FragmentManager fm;
    private FragmentTransaction ft;
    public static int lastCheckId;
    private BaseModeFragment currFragment;
    private BaseModeFragment toFragment;
    private String currentFtName;

    private RadioGroup radioGroup;
    private RootTableCheckedChangeListener rootTableListener;
    private MDetalRootPageRequestResult entity;

    /**
     * 最上层跟跟标签ID
     */
    public int suRootID;

    public static ModularTwo_UnitTablesFragment newInstance(int suRootID, String param) {
        ModularTwo_UnitTablesFragment fragment = new ModularTwo_UnitTablesFragment();
        Bundle args = new Bundle();
        args.putInt(SU_ROOTID, suRootID);
        args.putString(ARG_PARAM1, param);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public Subject setSubject() {
        return new ModularTwo_UnitTablesParentMode(ctx);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Random random = new Random();
        int cuttentFTID = random.nextInt(Integer.MAX_VALUE);
        fragmentTag = "android:switcher:" + cuttentFTID + ":";
        fm = getChildFragmentManager();
        if (getArguments() != null) {
            suRootID = getArguments().getInt(SU_ROOTID);
            mParam = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_modular_two_unit_tables, container, false);
            x.view().inject(this, rootView);
            init();
        }
        return rootView;
    }


    private void init() {
        rootTableListener = new RootTableCheckedChangeListener();
        getModel().analysisData(mParam);
    }


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

    private void bindData(MDetalRootPageRequestResult entity) {
        this.entity = entity;
        if (entity != null) {
            int dataSize = entity.datas.size();
            if (dataSize != 0) {
                View scroll_title = LayoutInflater.from(ctx)
                        .inflate(R.layout.item_mdetal_scroll_title, null);
                int cl = getResources().getColor(R.color.co8);
                scroll_title.setBackgroundColor(cl);
                fl_titleContainer.addView(scroll_title);
                radioGroup = (RadioGroup) scroll_title.findViewById(R.id.radioGroup);

                int marg = getResources().getDimensionPixelSize(R.dimen.space_default);
                for (int i = 0; i < dataSize; i++) {
                    RadioButton rbtn = new RadioButton(ctx);
                    RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT);
                    rbtn.setTag(i);
                    rbtn.setPadding(marg, 0, marg, 0);
                    Bitmap a=null;
                    rbtn.setButtonDrawable(new BitmapDrawable(a));
                    rbtn.setBackgroundResource(R.drawable.selector_mdetal_table_rbtn);
                    rbtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
                    ColorStateList colorStateList = getResources().getColorStateList(R.color.color_mdetal_table_rbtn);
                    rbtn.setTextColor(colorStateList);
                    rbtn.setText(entity.datas.get(i).type);
                    radioGroup.addView(rbtn, params_rb);
                    rbtn.setOnCheckedChangeListener(rootTableListener);
                    if (i == 0)
                        rbtn.setChecked(true);
                }
            }
        } else
            ToastUtil.showToast(ctx, "数据实体为空");
    }


    /**
     * 切换页面的重载，优化了fragment的切换
     */
    public void switchFragment(int checkId) {
        lastCheckId = checkId;
        currentFtName = fragmentTag + checkId;
        toFragment = (BaseModeFragment) fm.findFragmentByTag(currentFtName);
        Log.i(this.getClass().getSimpleName(), "currentFtName:" + currentFtName);
        if (currFragment != null && currFragment == toFragment)
            return;

        if (toFragment == null)
            toFragment = ModularTwo_UnitTablesContFragment.newInstance(entity.datas.get(checkId).config);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (!toFragment.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            if (currFragment == null)
                ft.add(R.id.fl_mdetal_table_cont_container,
                        toFragment, currentFtName).commitAllowingStateLoss();
            else
                ft.hide(currFragment).add(R.id.fl_mdetal_table_cont_container,
                        toFragment, currentFtName)
                        .commitAllowingStateLoss();
        } else {
            // 隐藏当前的fragment，显示下一个
            if (currFragment == null)
                ft.show(toFragment).commitAllowingStateLoss();
            else
                ft.hide(currFragment).show(toFragment).commitAllowingStateLoss();
        }
        currFragment = toFragment;
    }

    class RootTableCheckedChangeListener implements RadioButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                int tag = (Integer) buttonView.getTag();
                switchFragment(tag);
            }
        }
    }
}

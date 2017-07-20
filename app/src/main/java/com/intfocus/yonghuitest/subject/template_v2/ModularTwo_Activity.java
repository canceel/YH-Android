package com.intfocus.yonghuitest.subject.template_v2;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseModeActivity;
import com.intfocus.yonghuitest.base.BaseModeFragment;
import com.intfocus.yonghuitest.subject.template_v2.bean.EventRefreshTableRect;
import com.intfocus.yonghuitest.subject.template_v2.bean.MDetalActRequestResult;
import com.intfocus.yonghuitest.subject.template_v2.mode.MeterDetalActMode;
import com.intfocus.yonghuitest.view.RootScrollView;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 模块二页面
 */
public class ModularTwo_Activity extends BaseModeActivity<MeterDetalActMode> {
    private String TAG = ModularTwo_Activity.class.getSimpleName();
    private static final String fragmentTag = "android:switcher:" + R.layout.actvity_meter_detal + ":";

    @ViewInject(R.id.rootScrollView)
    public RootScrollView rScrollView;

    @ViewInject(R.id.fl_mdetal_top_suspend_container)
    public FrameLayout suspendContainer;

    private FragmentManager fm;
    private FragmentTransaction ft;
    public static int lastCheckId;
    private BaseModeFragment currFragment;
    private BaseModeFragment toFragment;
    private String currentFtName;

    @ViewInject(R.id.fl_mdetal_title_container)
    private FrameLayout fl_titleContainer;

    private RadioGroup radioGroup;
    private RootTableCheckedChangeListener rootTableListener;

    private TextView tv_single_title;

    /**
     * 数据实体
     */
    private MDetalActRequestResult entity;


    @Override
    public int setLayoutRes() {
        return R.layout.actvity_meter_detal;
    }

    @Override
    public Subject setSubject() {
        EventBus.getDefault().register(this);
        return new MeterDetalActMode(ctx);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateFinish(Bundle bundle) {
        getSupportActionBar().hide();
        initHeader();
        fm = getSupportFragmentManager();
        x.view().inject(this);
        init();
    }

    private void init() {
        rootTableListener = new RootTableCheckedChangeListener();
        setACTitle("标题");
        showProgress();
        getModel().requestData();
    }

    /**
     * 切换页面的重载，优化了fragment的切换
     */
    public void switchFragment(int checkId) {
        lastCheckId = checkId;
        currentFtName = fragmentTag + checkId;
        toFragment = (BaseModeFragment) getSupportFragmentManager().findFragmentByTag(currentFtName);

        if (currFragment != null && currFragment == toFragment)
            return;

        if (toFragment == null)
            toFragment = ModularTwo_RootPageFragment.newInstance(checkId, entity.datas.data.get(checkId).parts);

        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (!toFragment.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            if (currFragment == null)
                ft.add(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commitAllowingStateLoss();
            else
                ft.hide(currFragment).add(R.id.fl_mdetal_cont_container, toFragment, currentFtName)
                        .commitAllowingStateLoss();
        } else {
            // 隐藏当前的fragment，显示下一个
            if (currFragment == null)
                ft.show(toFragment).commitAllowingStateLoss();
            else
                ft.hide(currFragment).show(toFragment).commitAllowingStateLoss();
        }

        currFragment = toFragment;

        EventBus.getDefault().post(new EventRefreshTableRect(checkId));
    }

    /**
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MDetalActRequestResult entity) {
        dismissProgress();
        this.entity = entity;
        setACTitle(entity.datas.name);
        if (entity != null) {
            int dataSize = entity.datas.data.size();
            if (dataSize > 1) {//TODO 多个根页签
                View scroll_title = LayoutInflater.from(ctx)
                        .inflate(R.layout.item_mdetal_scroll_title, null);
                fl_titleContainer.addView(scroll_title);
                radioGroup = (RadioGroup) scroll_title.findViewById(R.id.radioGroup);

                for (int i = 0; i < dataSize; i++) {
                    RadioButton rbtn = new RadioButton(this);
                    RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(
                            RadioGroup.LayoutParams.WRAP_CONTENT,
                            RadioGroup.LayoutParams.WRAP_CONTENT);
                    if (i == 0)
                        params_rb.setMargins(50, 0, 0, 0);
                    rbtn.setTag(i);
                    rbtn.setPadding(35, 10, 35, 10);
                    Bitmap a=null;
                    rbtn.setButtonDrawable(new BitmapDrawable(a));
                    rbtn.setBackgroundResource(R.drawable.selector_mdetal_act_rbtn);
                    rbtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
                    ColorStateList colorStateList = getResources().getColorStateList(R.color.black);
                    rbtn.setTextColor(colorStateList);
                    rbtn.setText(entity.datas.data.get(i).title);
                    radioGroup.addView(rbtn, params_rb);
                    rbtn.setOnCheckedChangeListener(rootTableListener);
                    if (i == 0)
                        rbtn.setChecked(true);
                }

            } else if (dataSize == 1) {//TODO 只有一个根页签
                View single_title = LayoutInflater.from(ctx)
                        .inflate(R.layout.item_mdetal_single_title, null);
                tv_single_title = (TextView) single_title.findViewById(R.id.tv_mdetal_single_title);
                fl_titleContainer.addView(single_title);
                tv_single_title.setText(entity.datas.data.get(0).title);
                switchFragment(0);
            }
        } else
            ToastUtil.showToast(ctx, "数据实体为空");
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

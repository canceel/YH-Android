package com.intfocus.yonghuitest.dashboard.kpi;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.intfocus.yonghuitest.BaseFragment;
import com.intfocus.yonghuitest.BaseTableActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.bean.kpi.msg.MeterDetalRequestResult;
import com.intfocus.yonghuitest.mode.MeterDetalMode;
import com.intfocus.yonghuitest.view.MyScrollView;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.TimeUtil;
import com.zbl.lib.baseframe.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.SoftReference;
import java.util.ArrayList;

/**
 * 图标详情页面
 */
public class MeterDetalActivity extends BaseTableActivity<MeterDetalMode> {

    private FragmentManager fm;
    private FragmentTransaction ft;
    public static int lastCheckId;
    private BaseFragment currFragment;
    private BaseFragment toFragment;
    private String currentFtName;

    @ViewInject(R.id.myScrollView)
    private MyScrollView fatScrollView;
    @ViewInject(R.id.radioGroup)
    private RadioGroup radioGroup;

    private ArrayList titles = new ArrayList();
    private ArrayList<Integer> ids = new ArrayList();

    private ArrayMap<Integer, SoftReference<BaseFragment>> ftPagers = new ArrayMap<>();
    private String TAG = MeterDetalActivity.class.getSimpleName();

    @Override
    public int setLayoutRes() {
        return R.layout.actvity_meter_detal;
    }

    @Override
    public Subject setSubject() {
        EventBus.getDefault().register(this);
        return new MeterDetalMode(ctx);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateFinish(Bundle bundle) {
        initHeader();
        fm = getSupportFragmentManager();
        x.view().inject(this);
        init();
    }


    private void init() {
        showProgress();
        getModel().requestData();
    }


    /**
     * 按钮选中事件
     *
     * @param checkId
     * @param index
     */
    private void checked(int checkId, int index) {
        lastCheckId = checkId;
        toFragment = getFragment(checkId);
        switchFragment(currFragment, toFragment);
        currFragment = toFragment;
    }

    /**
     * 获取页面
     *
     * @param checkId
     * @return
     */
    private BaseFragment getFragment(int checkId) {
        SoftReference<BaseFragment> softView = null;
        softView = ftPagers.get(checkId);
        if (softView == null || !softView.isEnqueued()) {
            softView = new SoftReference<BaseFragment>(new BarFragment_Top());
            ftPagers.put(checkId, softView);
        }
        return softView.get();
    }

    /**
     * 切换页面的重载，优化了fragment的切换
     *
     * @param from
     * @param to
     */
    public void switchFragment(BaseFragment from, BaseFragment to) {
        if (from == to)
            return;

        currentFtName = to.getClass().getName();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        if (from == null) {
            if (!to.isAdded())
                ft.add(R.id.fl_mdetal_container, toFragment, currentFtName).commitAllowingStateLoss();
            else
                ft.show(to).commitAllowingStateLoss();
            return;
        }

        if (!to.isAdded()) {
            // 隐藏当前的fragment，add下一个到Activity中
            ft.hide(from).add(R.id.fl_mdetal_container, to, currentFtName)
                    .commitAllowingStateLoss();
        } else {
            // 隐藏当前的fragment，显示下一个
            ft.hide(from).show(to).commitAllowingStateLoss();
        }
    }

    /**
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MeterDetalRequestResult entity) {
        dismissProgress();
        Log.i(TAG, "EndTime:" + TimeUtil.getNowTime());
        if (entity != null) {
            int dataSize = entity.datas.size();
//            ftPagers radioGroup
            for (int i = 0; i < dataSize; i++) {
                RadioButton rbtn = new RadioButton(this);
                RadioGroup.LayoutParams params_rb = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT);
                params_rb.setMargins(30, 0, 30, 0);
                rbtn.setTag(i);
                rbtn.setPadding(20, 0, 20, 0);
                rbtn.setButtonDrawable(0);
                rbtn.setBackgroundResource(R.drawable.selector_mdetal_rbtn);
                rbtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.font_medium));
                ColorStateList colorStateList = getResources().getColorStateList(R.color.color_mdetal_rbtn);
                rbtn.setTextColor(colorStateList);
                rbtn.setText(entity.datas.get(i).name);
                radioGroup.addView(rbtn, params_rb);
                if (i == 0) {
                    radioGroup.check(rbtn.getId());
                }

            }
            radioGroup.invalidate();
        } else
            ToastUtil.showToast(ctx, "数据实体为空");
    }
}

package com.intfocus.yonghuitest.base;

import android.app.Activity;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.RelativeLayout;

import com.intfocus.yonghuitest.R;
import com.zbl.lib.baseframe.core.AbstractFragment;
import com.zbl.lib.baseframe.core.Subject;

/**
 * Created by liuruilin on 2017/5/8.
 */

public abstract class BaseTableFragment<Target extends Subject> extends AbstractFragment<Target> {
    public Activity act;
    public RelativeLayout mAnimLoading;

    @Override
    public void onAttach(Context context) {
        act = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


/*    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_hold, R.anim.activity_fade);


    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.activity_hold, R.anim.activity_fade);
    }*/
}

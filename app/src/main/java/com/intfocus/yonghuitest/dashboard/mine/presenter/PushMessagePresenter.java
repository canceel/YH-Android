package com.intfocus.yonghuitest.dashboard.mine.presenter;

import android.content.Context;

import com.intfocus.yonghuitest.dashboard.mine.bean.PushMessageBean;
import com.intfocus.yonghuitest.dashboard.mine.model.OnPushMessageDataResultListener;
import com.intfocus.yonghuitest.dashboard.mine.model.PushMessageModelImpl;
import com.intfocus.yonghuitest.dashboard.mine.view.PushMessageView;

import java.util.List;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/01 下午4:55
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public class PushMessagePresenter {
    private PushMessageView mView;
    private PushMessageModelImpl mModel;
    private Context mContext;

    public PushMessagePresenter(Context context, PushMessageView pushMessageView) {
        mContext = context;
        mView = pushMessageView;
        mModel = new PushMessageModelImpl();
    }

    public void loadData() {
        mModel.loadData(mContext,new OnPushMessageDataResultListener() {
            @Override
            public void onPushMessageDataResultSuccess(List<PushMessageBean> data) {
                mView.onResultSuccess(data);
            }

            @Override
            public void onPushMessageDataResultFailure() {
                mView.onResultFailure();
            }

        });
    }
}

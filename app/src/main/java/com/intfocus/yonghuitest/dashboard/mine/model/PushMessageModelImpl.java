package com.intfocus.yonghuitest.dashboard.mine.model;

import android.content.Context;

import com.intfocus.yonghuitest.dashboard.mine.bean.PushMessageBean;
import com.intfocus.yonghuitest.db.OrmDBHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/01 下午4:56
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public class PushMessageModelImpl implements PushMessageModel {
    @Override
    public void loadData(Context context, final OnPushMessageDataResultListener listener) {
        // TODO: RxJava 异步读取数据库
        try {
            final Dao<PushMessageBean, Long> pushMessageBeen = OrmDBHelper.getInstance(context).getPushMessageDao();
            Observable.create(new Observable.OnSubscribe<List<PushMessageBean>>() {
                @Override
                public void call(Subscriber<? super List<PushMessageBean>> subscriber) {
                    List<PushMessageBean> data = null;
                    try {
                        data = pushMessageBeen.queryForAll();
                    } catch (SQLException e) {
                        e.printStackTrace();
                        listener.onPushMessageDataResultFailure();
                        return;
                    }
                    subscriber.onNext(data);
                    subscriber.onCompleted();
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<PushMessageBean>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            listener.onPushMessageDataResultFailure();
                        }

                        @Override
                        public void onNext(List<PushMessageBean> pushMessageBeen) {
                            listener.onPushMessageDataResultSuccess(pushMessageBeen);

                        }
                    });
        } catch (SQLException e) {
            listener.onPushMessageDataResultFailure();
        }

    }
}

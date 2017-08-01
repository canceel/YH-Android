package com.intfocus.yonghuitest.dashboard.mine.model;

import android.content.Context;

import com.intfocus.yonghuitest.dashboard.mine.bean.PushMessageBean;
import com.intfocus.yonghuitest.db.OrmDBHelper;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

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
    public void loadData(Context context, OnPushMessageDataResultListener listener) {
        // TODO: RxJava 异步读取数据库
        try {
            Dao<PushMessageBean, Long> pushMessageBeen = OrmDBHelper.getInstance(context).getPersonDao();
            List<PushMessageBean> data = pushMessageBeen.queryForAll();
            listener.onPushMessageDataResultSuccess(data);
        } catch (SQLException e) {
            listener.onPushMessageDataResultFailure();
        }

    }
}

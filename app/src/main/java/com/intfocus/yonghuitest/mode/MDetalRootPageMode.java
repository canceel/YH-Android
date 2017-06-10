package com.intfocus.yonghuitest.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONReader;
import com.intfocus.yonghuitest.bean.dashboard.kpi.MDetalUnitEntity;
import com.intfocus.yonghuitest.bean.dashboard.kpi.MDetalRootPageRequestResult;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MDetalRootPageMode extends AbstractMode {

    String TAG = MDetalRootPageMode.class.getSimpleName();

    Context ctx;

    public MDetalRootPageMode(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void requestData() {
    }

    /**
     * 解析数据
     *
     * @param result
     */
    public void analysisData(final String result) {
        Log.i(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<MDetalUnitEntity> datas = new ArrayList<>();
                    StringReader isr = new StringReader(result);
                    JSONReader reader = new JSONReader(isr);
                    reader.startObject();
                    MDetalUnitEntity entity = null;
                    while (reader.hasNext()) {
                        String key = reader.readString();
                        if ("parts".equals(key)) {
                            reader.startArray();
                            while (reader.hasNext()) {
                                reader.startObject();
                                while (reader.hasNext()) {
                                    String entityKey = reader.readString();
                                    if ("type".equals(entityKey)) {
                                        String name = reader.readObject().toString();
                                        entity.setType(name);
                                        datas.add(entity);
                                    } else if ("config".equals(entityKey)) {
                                        entity = new MDetalUnitEntity();
                                        String data = reader.readObject().toString();
                                        entity.setConfig(data);
                                    }
                                }
                                reader.endObject();
                            }
                            reader.endArray();
                        } else if ("title".equals(key)) {
                            String title = reader.readObject().toString();
                            Log.d(TAG, "title:" + title);
                        }
                    }
                    reader.endObject();
                    MDetalRootPageRequestResult RequestResult = new MDetalRootPageRequestResult(true, 200, datas);
                    dataCallback(RequestResult, "onMessageEvent");
                    Log.i(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime());
                } catch (Exception e) {
                    dataCallback(new MDetalRootPageRequestResult(true, 400, null), "onMessageEvent");
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

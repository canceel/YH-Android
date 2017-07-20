package com.intfocus.yonghuitest.subject.template_v2.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONReader;
import com.intfocus.yonghuitest.subject.template_v2.bean.MDetalActRequestResult;
import com.intfocus.yonghuitest.subject.template_v2.bean.MererDetalEntity;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static com.intfocus.yonghuitest.YHApplication.threadPool;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MeterDetalActMode extends AbstractMode {

    String TAG = MeterDetalActMode.class.getSimpleName();

    Context ctx;

    MererDetalEntity entity;

    public MeterDetalActMode(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void requestData() {
        entity = null;
        Log.i(TAG, "StartTime:" + TimeUtil.getNowTime());
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    InputStream is = ctx.getAssets().open("kpi_detaldata.json");
                    InputStreamReader isr = new InputStreamReader(is);
                    JSONReader reader = new JSONReader(isr);
                    reader.startArray();
                    reader.startObject();

                    entity = new MererDetalEntity();
                    entity.data = new ArrayList<>();

                    while (reader.hasNext()) {
                        String key = reader.readString();
                        switch (key) {
                            case "name":
                                String name = reader.readObject().toString();
                                entity.name = name;
                                break;

                            case "data":
                                reader.startArray();
                                while (reader.hasNext()) {
                                    reader.startObject();
                                    MererDetalEntity.PageData data = new MererDetalEntity.PageData();
                                    while (reader.hasNext()) {
                                        String dataKey = reader.readString();
                                        switch (dataKey) {
                                            case "parts":
                                                String parts = reader.readObject().toString();
                                                data.parts = parts;
                                                break;

                                            case "title":
                                                String title = reader.readObject().toString();
                                                data.title = title;
                                                break;
                                        }
                                    }
                                    reader.endObject();
                                    entity.data.add(data);
                                }
                                reader.endArray();
                                break;
                        }
                    }
                    reader.endObject();
                    reader.endArray();
                    EventBus.getDefault().post(new MDetalActRequestResult(true, 200, entity));
                    Log.i(TAG, "EndTime:" + TimeUtil.getNowTime());
                } catch (IOException e) {
                    e.printStackTrace();
                    MDetalActRequestResult result1 = new MDetalActRequestResult(true, 400, null);
                    EventBus.getDefault().post(result1);
                }
            }
        });
    }

/*    *//**
     * 解析数据
     *
     * @param result
     *//*
    private void analysisData(String result) {
        ArrayList<MererDetalEntity> datas = (ArrayList<MererDetalEntity>) JSON.parseArray(result, MererDetalEntity.class);
        EventBus.getDefault().post(new MDetalActRequestResult(true, 200, datas));
    }*/
}

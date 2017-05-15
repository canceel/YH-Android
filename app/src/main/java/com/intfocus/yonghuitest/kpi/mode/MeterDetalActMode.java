package com.intfocus.yonghuitest.kpi.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONReader;
import com.intfocus.yonghuitest.kpi.entity.MererDetalEntity;
import com.intfocus.yonghuitest.kpi.entity.msg.MDetalActRequestResult;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MeterDetalActMode extends AbstractMode {

    String TAG = MeterDetalActMode.class.getSimpleName();

    Context ctx;

    public MeterDetalActMode(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void requestData() {
        Log.i(TAG, "StartTime:" + TimeUtil.getNowTime());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<MererDetalEntity> datas = new ArrayList<>();
                    InputStream is = ctx.getAssets().open("kpi_detaldata.json");
                    InputStreamReader isr = new InputStreamReader(is);
                    JSONReader reader = new JSONReader(isr);
                    reader.startArray();
                    MererDetalEntity entity = null;
                    while (reader.hasNext()) {
                        reader.startObject();
                        while (reader.hasNext()) {
                            String key = reader.readString();
                            if ("name".equals(key)) {
                                String name = reader.readObject().toString();
                                entity.name = name;
                                datas.add(entity);
                            } else if ("data".equals(key)) {
                                entity = new MererDetalEntity();
                                reader.startArray();
                                String data = reader.readObject().toString();
                                entity.data = data;
                                reader.endArray();
                            }
                        }
                        reader.endObject();
                    }
                    reader.endArray();
                    EventBus.getDefault().post(new MDetalActRequestResult(true, 200, datas));

                } catch (IOException e) {
                    e.printStackTrace();
                    MDetalActRequestResult result1 = new MDetalActRequestResult(true, 400, null);
                    EventBus.getDefault().post(result1);
                }
            }
        }).start();
    }

    /**
     * 解析数据
     *
     * @param result
     */
    private void analysisData(String result) {
        ArrayList<MererDetalEntity> datas = (ArrayList<MererDetalEntity>) JSON.parseArray(result, MererDetalEntity.class);
        EventBus.getDefault().post(new MDetalActRequestResult(true, 200, datas));
    }
}

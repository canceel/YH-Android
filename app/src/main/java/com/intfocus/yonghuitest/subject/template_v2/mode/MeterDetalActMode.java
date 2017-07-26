package com.intfocus.yonghuitest.subject.template_v2.mode;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.alibaba.fastjson.JSONReader;
import com.intfocus.yonghuitest.subject.template_v2.entity.MererDetalEntity;
import com.intfocus.yonghuitest.subject.template_v2.entity.msg.MDetalActRequestResult;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
                    String urlString = "https://development.shengyiplus.com/api/v1/group/165/template/1/report/1/json";
                    Map<String, String> response = HttpUtil.httpGet(urlString, new HashMap<String, String>());

                    String jsonFileName = String.format("group_%s_template_%s_report_%s.json", String.format("%d", 165), 1, "1");
                    String jsonFilePath = FileUtil.dirPath(ctx, K.kCachedDirName, jsonFileName);
                    if (response.get("code").equals("200") || response.get("code").equals("304")) {
                        try {
                            FileUtil.writeFile(jsonFilePath, "[{\"data\":" + response.get("body") + ", \"name\": \"标题\"}]");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    InputStream is = new FileInputStream(jsonFilePath);
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

package com.intfocus.yonghuitest.subject.template_v2.mode;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.intfocus.yonghuitest.subject.template_v2.entity.MererEntity;
import com.intfocus.yonghuitest.subject.template_v2.entity.msg.MeterRequestResult;
import com.intfocus.yonghuitest.util.FileUtil;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.intfocus.yonghuitest.YHApplication.threadPool;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MeterMode extends AbstractMode {

    Context ctx;

    public MeterMode(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void requestData() {
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                String result = FileUtil.readAssetsFile(ctx, "kpi_data.json");
                if (StringUtil.isEmpty(result)) {
                    MeterRequestResult result1 = new MeterRequestResult(true, 400);
                    EventBus.getDefault().post(result1);
                    return;
                }
                analysisData(result);
            }
        });
    }

    /**
     * 解析数据
     *
     * @param result
     */
    private MeterRequestResult analysisData(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            if (jsonObject.has("code")) {
                int code = jsonObject.getInt("code");
                if (code != 200) {
                    MeterRequestResult result1 = new MeterRequestResult(true, code);
                    EventBus.getDefault().post(result1);
                    return result1;
                }
            }

            if (jsonObject.has("data")) {
                String data = jsonObject.getString("data");
                data = data.replace("null","0");
                ArrayList<MererEntity> datas = (ArrayList<MererEntity>) JSON.parseArray(data, MererEntity.class);
                ArrayList<MererEntity> topData = new ArrayList<>();
                Iterator<MererEntity> iterator = datas.iterator();
                while (iterator.hasNext()) {
                    MererEntity entity = iterator.next();
                    if (entity.is_stick) {
                        topData.add(entity);
                        iterator.remove();
                    }
                }
                MeterRequestResult result1 = new MeterRequestResult(true, 200);
                result1.setTopDatas(topData);
                result1.setBodyDatas(datas);
                EventBus.getDefault().post(result1);
                return result1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            MeterRequestResult result1 = new MeterRequestResult(true, -1);
            EventBus.getDefault().post(result1);
        }
        MeterRequestResult result1 = new MeterRequestResult(true, 0);
        EventBus.getDefault().post(result1);
        return result1;
    }
}

package com.intfocus.yonghuitest.mode;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.intfocus.yonghuitest.bean.kpi.MererEntity;
import com.intfocus.yonghuitest.bean.kpi.msg.MeterRequestResult;
import com.intfocus.yonghuitest.util.FileUtil;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Iterator;

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
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
                String result = FileUtil.assetsFileContent(ctx, "kpi_data.json");
                if (StringUtil.isEmpty(result)) {
                    MeterRequestResult result1 = new MeterRequestResult(true, 400);
                    EventBus.getDefault().post(result1);
                    return;
                }
                analysisData(result);
//            }
//        }).start();
    }

    /**
     * 解析数据
     *
     * @param result
     */
    private void analysisData(String result) {
        ArrayList<MererEntity> datas = (ArrayList<MererEntity>) JSON.parseArray(result, MererEntity.class);

        ArrayList<MererEntity> topData = new ArrayList<>();
        Iterator<MererEntity> iterator = datas.iterator();
        while (iterator.hasNext()) {
            MererEntity entity = iterator.next();
            int key = entity.is_stick;
            if (key == 1) {
                topData.add(entity);
                iterator.remove();
            }
        }
        MeterRequestResult result1 = new MeterRequestResult(true, 200);
        result1.setTopDatas(topData);
        result1.setBodyDatas(datas);
        EventBus.getDefault().post(result1);
    }
}

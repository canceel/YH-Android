package com.intfocus.yonghuitest.mode;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.intfocus.yonghuitest.bean.kpi.MererDetalEntity;
import com.intfocus.yonghuitest.bean.kpi.msg.MeterDetalRequestResult;
import com.intfocus.yonghuitest.util.FileUtil;
import com.zbl.lib.baseframe.core.AbstractMode;
import com.zbl.lib.baseframe.utils.TimeUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 仪表盘-数据处理模块
 * Created by zbaoliang on 17-4-28.
 */
public class MeterDetalMode extends AbstractMode {

    String TAG = MeterDetalMode.class.getSimpleName();

    Context ctx;

    public MeterDetalMode(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public void requestData() {
        Log.i(TAG,"StartTime:"+ TimeUtil.getNowTime());
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = FileUtil.assetsFileContent(ctx, "kpi_detaldata.json");
//        if (StringUtil.isEmpty(result)) {
//            MeterDetalRequestResult result1 = new MeterDetalRequestResult(true,400);
//            EventBus.getDefault().post(result1);
//            return;
//        }
                analysisData(result);
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

        EventBus.getDefault().post(new MeterDetalRequestResult(true, 200, datas));
    }
}

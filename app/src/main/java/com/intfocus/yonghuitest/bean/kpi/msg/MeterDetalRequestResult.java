package com.intfocus.yonghuitest.bean.kpi.msg;

import com.intfocus.yonghuitest.bean.kpi.MererDetalEntity;

import java.util.ArrayList;

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
public class MeterDetalRequestResult {
    public boolean isSuccress ;
    public int stateCode ;
    public ArrayList<MererDetalEntity> datas;

    public MeterDetalRequestResult(boolean isSuccress, int stateCode, ArrayList<MererDetalEntity> datas) {
        this.isSuccress = isSuccress;
        this.stateCode = stateCode;
        this.datas = datas;
    }
}

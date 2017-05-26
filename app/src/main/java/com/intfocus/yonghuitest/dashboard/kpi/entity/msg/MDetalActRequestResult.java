package com.intfocus.yonghuitest.dashboard.kpi.entity.msg;

import com.intfocus.yonghuitest.dashboard.kpi.entity.MererDetalEntity;

import java.util.ArrayList;

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
public class MDetalActRequestResult {
    public boolean isSuccress ;
    public int stateCode ;
    public ArrayList<MererDetalEntity> datas;

    public MDetalActRequestResult(boolean isSuccress, int stateCode, ArrayList<MererDetalEntity> datas) {
        this.isSuccress = isSuccress;
        this.stateCode = stateCode;
        this.datas = datas;
    }
}

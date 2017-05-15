package com.intfocus.yonghuitest.kpi.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 仪表盘实体对象
 * Created by zbaoliang on 17-4-28.
 */
public class MDRPUnitCurveChartEntity implements Serializable {
    public String chart_type;
    public String[] legend;
    public ArrayList<SeriesEntity> series;
    public String title;
    public String[] xAxis;
    public String[] yAxis;
    public int stateCode = 200;

    public class SeriesEntity implements Serializable {
        public String data;
        public String name;
        public String type;
    }
}

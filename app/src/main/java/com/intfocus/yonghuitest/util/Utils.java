package com.intfocus.yonghuitest.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.intfocus.yonghuitest.bean.table.SortData;
import com.intfocus.yonghuitest.bean.table.TableBarChart;

import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by CANC on 2017/4/19.
 */

public class Utils {
    /**
     * 从asset路径下读取对应文件转String输出
     *
     * @param mContext
     * @return
     */
    public static String getJson(Context mContext, String fileName) {
        String newString = "";
        try {
            InputStreamReader isr = new InputStreamReader(mContext.getAssets().open(fileName), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
//            JSONObject testjson = new JSONObject(builder.toString());//builder读取了JSON中的数据。
            newString = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newString;
    }


    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int pxToDp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dpValue / scale) - 0.5f);
    }

    /**
     * 图片ID
     *
     * @param context
     * @param id
     * @return
     */
    public static Drawable returnDrawable(Context context, int id) {
        Drawable drawable = context.getResources().getDrawable(id);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        return drawable;
    }

    /**
     * 排序
     * <p>
     * 排序后返回排序编号
     *
     * @param datas 待排序数据
     * @param isAsc 降序升序
     */
    public static List<Integer> sortData(List<SortData> datas, boolean isAsc) {
        List<Integer> integers = new ArrayList<>();
        if (isAsc) {
            for (int i = 0; i < datas.size(); i++) {
                for (int j = 0; j < datas.size() - i - 1; j++) {
                    if (datas.get(j).getValue()
                            > datas.get(j + 1).getValue()) {
                        SortData sortData = datas.get(j);
                        datas.set(j, datas.get(j + 1));
                        datas.set(j + 1, sortData);
                    }
                }
            }
        } else {
            for (int i = 0; i < datas.size(); i++) {
                for (int j = 0; j < datas.size() - i - 1; j++) {
                    if (datas.get(j).getValue()
                            < datas.get(j + 1).getValue()) {
                        SortData sortData = datas.get(j);
                        datas.set(j, datas.get(j + 1));
                        datas.set(j + 1, sortData);
                    }
                }
            }
        }
        for (SortData sortData : datas) {
            integers.add(sortData.originPosition);
        }
        return integers;
    }

    public static Double getMaxValue(List<TableBarChart> datas) {
        List<Double> doubles = new ArrayList<>();
        for (TableBarChart tableBarChart : datas) {
            String mainDataStr = tableBarChart.getData();
            if (mainDataStr.contains("%")) {
                mainDataStr = mainDataStr.replace("%", "");
            }
            if (Utils.isNumber(mainDataStr)) {
                doubles.add(Double.parseDouble(mainDataStr));
            }
            else {
                return null;
            }
        }
        return Collections.max(doubles);
    }

    public static boolean isNumber(String str) {
        Pattern p = Pattern.compile("-?[0-9]+.*[0-9]*");
        Matcher m = p.matcher(str);
        if(m.matches() ){
            return true;
        }
        p=Pattern.compile("[a-zA-Z]");
        m=p.matcher(str);
        if(m.matches()){
            return false;
        }
        p=Pattern.compile("[\u4e00-\u9fa5]");
        m=p.matcher(str);
        if(m.matches()){
            return false;
        }

        return false;
    }
}

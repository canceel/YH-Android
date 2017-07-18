package com.intfocus.yh_android.util;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.intfocus.yh_android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuruilin on 2017/3/30.
 */

public class WidgetUtil {
    public static Toast toast;

    public static void showToastShort(Context context, String info) {
        try {
            if (null == toast) {
                toast = Toast.makeText(context, info, Toast.LENGTH_SHORT);
            } else {
                toast.setText(info); //若当前已有 Toast 在显示,则直接修改当前 Toast 显示的内容
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showToastLong(Context context, String info) {
        try {
            if (null == toast) {
                toast = Toast.makeText(context, info, Toast.LENGTH_LONG);
            } else {
                toast.setText(info); //若当前已有 Toast 在显示,则直接修改当前 Toast 显示的内容
            }
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

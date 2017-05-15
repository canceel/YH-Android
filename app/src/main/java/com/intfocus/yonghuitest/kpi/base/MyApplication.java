package com.intfocus.yonghuitest.kpi.base;

import android.app.Application;

import com.zbl.lib.baseframe.crash.CrashHandlerUtil;

import org.xutils.BuildConfig;
import org.xutils.x;

public class MyApplication extends Application {
    /**
     * 缓存目录
     */
    public static String CACHEDIR;

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandlerUtil.getInstance().init(this);
        CACHEDIR = getExternalCacheDir().getPath();

        initXutils();
    }

    private void initXutils() {
        x.Ext.init(this);
        if (!BuildConfig.DEBUG) {
            x.Ext.setDebug(BuildConfig.DEBUG);
        }
    }
}

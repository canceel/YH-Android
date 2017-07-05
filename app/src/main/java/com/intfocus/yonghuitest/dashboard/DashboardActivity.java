package com.intfocus.yonghuitest.dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.intfocus.yonghuitest.bean.User;
import com.intfocus.yonghuitest.scanner.BarCodeScannerActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.ResetPasswordActivity;
import com.intfocus.yonghuitest.YHApplication;
import com.intfocus.yonghuitest.adapter.DashboardFragmentAdapter;
import com.intfocus.yonghuitest.setting.SettingActivity;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.URLs;
import com.intfocus.yonghuitest.view.TabView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import sumimakito.android.advtextswitcher.AdvTextSwitcher;

public class DashboardActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, AdvTextSwitcher.Callback {
    private DashboardFragmentAdapter mDashboardFragmentAdapter;
    private SharedPreferences mSharedPreferences;
    private TabView[] mTabView;
    private User user;
    private int userID;
    private YHApplication mApp;
    private ViewPager mViewPager;
    private TabView mTabKPI, mTabAnalysis, mTabAPP, mTabMessage;
    private Context mContext, mAppContext;
    private Gson mGson;

    public static final int PAGE_KPI = 0;
    public static final int PAGE_REPORTS = 1;
    public static final int PAGE_APP = 2;
    public static final int PAGE_MINE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mApp = (YHApplication) this.getApplication();
        mAppContext = mApp.getAppContext();
        mContext = this;
        mGson = new Gson();
        initUser();
        mSharedPreferences = getSharedPreferences("DashboardPreferences", MODE_PRIVATE);
        mDashboardFragmentAdapter = new DashboardFragmentAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.content_view);
        initTabView();
        initViewPaper(mDashboardFragmentAdapter);
        HttpUtil.checkAssetsUpdated(mContext);
        checkUserModifiedInitPassword(); // 检测用户密码
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Intent intent = getIntent();
        if (intent.getBooleanExtra("fromMessage", false)) {
            mViewPager.setCurrentItem(PAGE_MINE);
            mTabView[mViewPager.getCurrentItem()].setActive(true);
        }
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示")
                .setMessage(String.format("确认退出【%s】？", getResources().getString(R.string.app_name)))
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mApp.setCurrentActivity(null);
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 返回DashboardActivity
                    }
                });
        builder.show();
    }

    /*
     * 初始化用户信息
     */
    private void initUser() {
        String userConfigPath = String.format("%s/%s", FileUtil.basePath(mAppContext), K.kUserConfigFileName);
        if ((new File(userConfigPath)).exists()) {
                user = mGson.fromJson(FileUtil.readConfigFile(userConfigPath).toString(), User.class);
                if (user.isIs_login()) {
                    userID = user.getUser_id();
                }
        }
    }

    public void startBarCodeActivity(View v) {
            if (ContextCompat.checkSelfPermission(DashboardActivity.this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setTitle("温馨提示")
                        .setMessage("相机权限获取失败，是否到本应用的设置界面设置权限")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                goToAppSetting();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 返回DashboardActivity
                            }
                        });
                builder.show();
                return;
            }
            else if (user.getStore_ids().size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
                builder.setTitle("温馨提示")
                        .setMessage("抱歉, 您没有扫码权限")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                return;
            }
            else {
                Intent barCodeScannerIntent = new Intent(mContext, BarCodeScannerActivity.class);
                mContext.startActivity(barCodeScannerIntent);
            }
    }

    private void startSettingActivity() {
        Intent settingIntent = new Intent(mContext, SettingActivity.class);
        settingIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        mContext.startActivity(settingIntent);
    }

    /*
     * 跳转系统设置页面
     */
    private void goToAppSetting() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void initTabView() {
        mTabKPI = (TabView) findViewById(R.id.tab_kpi);
        mTabAnalysis = (TabView) findViewById(R.id.tab_analysis);
        mTabAPP = (TabView) findViewById(R.id.tab_app);
        mTabMessage = (TabView) findViewById(R.id.tab_message);
        mTabView = new TabView[]{mTabKPI, mTabAnalysis, mTabAPP, mTabMessage};

        mTabKPI.setOnClickListener(mTabChangeListener);
        mTabAnalysis.setOnClickListener(mTabChangeListener);
        mTabAPP.setOnClickListener(mTabChangeListener);
        mTabMessage.setOnClickListener(mTabChangeListener);
    }

    /**
     * @param dashboardFragmentAdapter
     */
    private void initViewPaper(DashboardFragmentAdapter dashboardFragmentAdapter) {
        mViewPager.setAdapter(dashboardFragmentAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(mSharedPreferences.getInt("LastTab", 0));
        mTabView[mViewPager.getCurrentItem()].setActive(true);
        mViewPager.addOnPageChangeListener(this);
    }

    /*
     * Tab 栏按钮监听事件
     */
    private final View.OnClickListener mTabChangeListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tab_kpi:
                    mViewPager.setCurrentItem(PAGE_KPI);
                    break;
                case R.id.tab_analysis:
                    mViewPager.setCurrentItem(PAGE_REPORTS);
                    break;
                case R.id.tab_app:
                    mViewPager.setCurrentItem(PAGE_APP);
                    break;
                case R.id.tab_message:
                    mViewPager.setCurrentItem(PAGE_MINE);
                    break;
                default:
                    break;
            }
            refreshTabView();
        }
    };

    //重写ViewPager页面切换的处理方法
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 2) {
            switch (mViewPager.getCurrentItem()) {
                case PAGE_KPI:
                    mTabKPI.setActive(true);
                    break;

                case PAGE_REPORTS:
                    mTabAnalysis.setActive(true);
                    break;

                case PAGE_APP:
                    mTabAPP.setActive(true);
                    break;

                case PAGE_MINE:
                    mTabMessage.setActive(true);
                    break;
            }
        }
        refreshTabView();
        mSharedPreferences.edit().putInt("LastTab", mViewPager.getCurrentItem()).commit();
    }

    // 公告栏点击事件
    @Override
    public void onItemClick(int position) {
        mViewPager.setCurrentItem(PAGE_MINE);
        refreshTabView();
    }

    /*
     * 刷新 TabView 高亮状态
     */
    private void refreshTabView() {
        mTabView[mViewPager.getCurrentItem()].setActive(true);
        for (int i = 0; i < mTabView.length; i++) {
            if (i != mViewPager.getCurrentItem()) {
                mTabView[i].setActive(false);
            }
        }
    }

    /*
     * 用户编号
     */
    public void checkUserModifiedInitPassword() {
            if (!user.getPassword().equals(URLs.MD5(K.kInitPassword))) {
                return;
            }
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(DashboardActivity.this);
            alertDialog.setTitle("温馨提示");
            alertDialog.setMessage("安全起见，请在【个人信息】-【基本信息】-【修改登录密码】页面修改初始密码");

            alertDialog.setPositiveButton("立即前往", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(DashboardActivity.this, ResetPasswordActivity.class);
                    startActivity(intent);
                }
            }).setNegativeButton("稍后修改", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 返回DashboardActivity
                }
            });
            alertDialog.show();
    }
}

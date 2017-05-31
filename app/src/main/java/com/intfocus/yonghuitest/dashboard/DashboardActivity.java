package com.intfocus.yonghuitest.dashboard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.intfocus.yonghuitest.BarCodeScannerActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.ResetPasswordActivity;
import com.intfocus.yonghuitest.SubjectActivity;
import com.intfocus.yonghuitest.YHApplication;
import com.intfocus.yonghuitest.adapter.DashboardFragmentAdapter;
import com.intfocus.yonghuitest.adapter.MenuAdapter;
import com.intfocus.yonghuitest.setting.SettingActivity;
import com.intfocus.yonghuitest.setting.SettingListActivity;
import com.intfocus.yonghuitest.setting.ThursdaySayActivity;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.URLs;
import com.intfocus.yonghuitest.util.WidgetUtil;
import com.intfocus.yonghuitest.view.TabView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import sumimakito.android.advtextswitcher.AdvTextSwitcher;

public class DashboardActivity extends FragmentActivity implements ViewPager.OnPageChangeListener, AdvTextSwitcher.Callback{
    private DashboardFragmentAdapter mDashboardFragmentAdapter;
    private PopupWindow popupWindow;
    private SharedPreferences mSharedPreferences;
    private ArrayList<HashMap<String, Object>> listItem;
    private TabView[] mTabView;
    private JSONObject user;
    private int userID;
    private MenuAdapter mSimpleAdapter;
    private YHApplication mApp;
    private ViewPager mViewPager;
    private TabView mTabKPI, mTabAnalysis, mTabAPP, mTabMessage;
    private Context mContext, mAppContext;

    public static final int PAGE_KPI = 0;
    public static final int PAGE_ANALYSIS = 1;
    public static final int PAGE_APP = 2;
    public static final int PAGE_MESSAGE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        mApp = (YHApplication) this.getApplication();
        mAppContext = mApp.getAppContext();
        mContext = this;
        mSharedPreferences = getSharedPreferences("DashboardPreferences", MODE_PRIVATE);
        mDashboardFragmentAdapter = new DashboardFragmentAdapter(getSupportFragmentManager());
        initUserIDColorView();
        bindFragment();
        HttpUtil.checkAssetsUpdated(mContext);
        checkUserModifiedInitPassword(); // 检测用户密码
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        dealSendMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    private void initUserIDColorView() {
        String userConfigPath = String.format("%s/%s", FileUtil.basePath(mAppContext), K.kUserConfigFileName);
        if ((new File(userConfigPath)).exists()) {
            try {
                user = FileUtil.readConfigFile(userConfigPath);
                if (user.has(URLs.kIsLogin) && user.getBoolean(URLs.kIsLogin)) {
                    userID = user.getInt("user_id");
                }
                WidgetUtil.initUserIDColorView(getWindow().getDecorView(), userID);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 标题栏点击设置按钮显示下拉菜单
     */
    public void launchDropMenuActivity(View v) {
        showComplaintsPopWindow(v);
    }

    /**
     * 显示菜单
     *
     * @param clickView
     */
    void showComplaintsPopWindow(View clickView) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu_dashboard, null);
        //设置弹出框的宽度和高度
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        //进入退出的动画
//        popupWindow.setAnimationStyle(R.style.AnimationPopupwindow);
        popupWindow.showAsDropDown(clickView);

        contentView.findViewById(R.id.ll_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startBarCodeActivity();
            }
        });
        contentView.findViewById(R.id.ll_sound).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WidgetUtil.showToastShort(mContext, "功能正在开发中");
                popupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.ll_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WidgetUtil.showToastShort(mContext, "功能正在开发中");
                popupWindow.dismiss();
            }
        });
        contentView.findViewById(R.id.ll_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSettingActivity();
                popupWindow.dismiss();
            }
        });
    }

    private void startBarCodeActivity() {
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
        } else {
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

    /*
     * 初始化 TabView 和 ViewPaper
     */
    private void bindFragment() {
        mTabKPI = (TabView) findViewById(R.id.tab_kpi);
        mTabAnalysis = (TabView) findViewById(R.id.tab_analysis);
        mTabAPP = (TabView) findViewById(R.id.tab_app);
        mTabMessage = (TabView) findViewById(R.id.tab_message);
        mTabView = new TabView[]{mTabKPI, mTabAnalysis, mTabAPP, mTabMessage};

        mTabKPI.setOnClickListener(mTabChangeListener);
        mTabAnalysis.setOnClickListener(mTabChangeListener);
        mTabAPP.setOnClickListener(mTabChangeListener);
        mTabMessage.setOnClickListener(mTabChangeListener);

        mViewPager = (ViewPager) findViewById(R.id.content_view);
        mViewPager.setAdapter(mDashboardFragmentAdapter);
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
                    mViewPager.setCurrentItem(PAGE_ANALYSIS);
                    break;
                case R.id.tab_app:
                    mViewPager.setCurrentItem(PAGE_APP);
                    break;
                case R.id.tab_message:
                    mViewPager.setCurrentItem(PAGE_MESSAGE);
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

                case PAGE_ANALYSIS:
                    mTabAnalysis.setActive(true);
                    break;

                case PAGE_APP:
                    mTabAPP.setActive(true);
                    break;

                case PAGE_MESSAGE:
                    mTabMessage.setActive(true);
                    break;
            }
        }
        refreshTabView();
        mSharedPreferences.edit().putInt("LastTab", mViewPager.getCurrentItem()).commit();
    }

    @Override
    public void onItemClick(int position) {
        mViewPager.setCurrentItem(PAGE_MESSAGE);
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

    private void dealSendMessage() {
        String pushMessagePath = String.format("%s/%s", FileUtil.basePath(mAppContext), K.kPushMessageFileName);
        JSONObject pushMessageJSON = FileUtil.readConfigFile(pushMessagePath);
        try {
            if (pushMessageJSON.has("state") && pushMessageJSON.getBoolean("state")) {
                return;
            }
            if (pushMessageJSON.has("type")) {
                String type = pushMessageJSON.getString("type");
                switch (type) {
                    case "report":
                        Intent subjectIntent = new Intent(this, SubjectActivity.class);
                        subjectIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        try{
                            subjectIntent.putExtra(URLs.kLink, pushMessageJSON.getString("url"));
                            subjectIntent.putExtra(URLs.kBannerName, pushMessageJSON.getString("title"));
                            subjectIntent.putExtra(URLs.kObjectId, pushMessageJSON.getInt("obj_id"));
                            subjectIntent.putExtra(URLs.kObjectType, pushMessageJSON.getInt("obj_type"));
                            startActivity(subjectIntent);
                        }catch (Exception e){
                            Toast.makeText(DashboardActivity.this, "推送消息的格式有误", Toast.LENGTH_SHORT);
                            e.printStackTrace();
                        }
                        break;
                    case "analyse":
                        mViewPager.setCurrentItem(PAGE_ANALYSIS);
                        mTabView[mViewPager.getCurrentItem()].setActive(true);
                        break;
                    case "app":
                        mViewPager.setCurrentItem(PAGE_APP);
                        mTabView[mViewPager.getCurrentItem()].setActive(true);
                        break;
                    case "message":
                        mViewPager.setCurrentItem(PAGE_MESSAGE);
                        mTabView[mViewPager.getCurrentItem()].setActive(true);
                        break;
                    case "thursday_say":
                        Intent blogLinkIntent = new Intent(DashboardActivity.this, ThursdaySayActivity.class);
                        startActivity(blogLinkIntent);
                }
            }
            refreshTabView();
            pushMessageJSON.put("state", true);
            FileUtil.writeFile(pushMessagePath, pushMessageJSON.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    /*
 * 用户编号
 */
    public void checkUserModifiedInitPassword() {
        try {
            if (!user.getString(URLs.kPassword).equals(URLs.MD5(K.kInitPassword))) {
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
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

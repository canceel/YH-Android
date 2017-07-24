package com.intfocus.yonghuitest.login;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.intfocus.yonghuitest.ForgetPasswordActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.dashboard.DashboardActivity;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.NetWorkUtils;
import com.intfocus.yonghuitest.util.URLs;
import com.intfocus.yonghuitest.util.WidgetUtil;
import com.intfocus.yonghuitest.util.URLs;
import com.pgyersdk.update.PgyUpdateManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends BaseActivity {
    public String kFromActivity = "from_activity";         // APP 启动标识
    public String kSuccess = "success";               // 用户登录验证结果
    private EditText usernameEditText, passwordEditText;
    private String usernameString, passwordString;
    private SharedPreferences mUserSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE);

        // 使背景填满整个屏幕,包括状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ApiHelper.getAMapLocation(mAppContext);

        /*
         *  如果是从触屏界面过来，则直接进入主界面如
         *  不是的话，相当于直接启动应用，则检测是否有设置锁屏
         */
        Intent intent = getIntent();
        if (intent.hasExtra(kFromActivity) && intent.getStringExtra(kFromActivity).equals("ConfirmPassCodeActivity")) {
            intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.putExtra(kFromActivity, intent.getStringExtra(kFromActivity));
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            LoginActivity.this.startActivity(intent);

            finish();
        } else {
            /*
             *  检测版本更新
             *    1. 与锁屏界面互斥；取消解屏时，返回登录界面，则不再检测版本更新；
             *    2. 原因：如果解屏成功，直接进入MainActivity,会在BaseActivity#finishLoginActivityWhenInMainAcitivty中结束LoginActivity,
             *            若此时有AlertDialog，会报错误:
             *            Activity has leaked window com.android.internal.policy.impl.PhoneWindow$DecorView@44f72ff0 that was originally added here
             */
            checkPgyerVersionUpgrade(LoginActivity.this, false);
        }

        setContentView(R.layout.activity_login);

        usernameEditText = (EditText) findViewById(R.id.etUsername);
        passwordEditText = (EditText) findViewById(R.id.etPassword);
        TextView versionTv = (TextView) findViewById(R.id.versionTv);
        findViewById(R.id.forgetPasswordTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.applyRegistTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("温馨提示")
                        .setMessage("请到数据化运营平台申请开通账号")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        /*
         * 显示记住用户名称
         */
        usernameEditText.setText(mUserSP.getString("user_login_name", ""));

        /*
         * 显示当前应用版本号
         */
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionInfo = String.format("a%s(%d)", packageInfo.versionName, packageInfo.versionCode);
            versionTv.setText(versionInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        /*
         *  当用户系统不在我们支持范围内时,发出警告。
         */
        if (Build.VERSION.SDK_INT > K.kMaxSdkVersion || Build.VERSION.SDK_INT < K.kMinSdkVersion) {
            showVersionWarring();
        }

        /*
         * 检测登录界面，版本是否升级
         */
        checkVersionUpgrade(assetsPath);
    }

    protected void onResume() {
        mMyApp.setCurrentActivity(this);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onResume();
    }

    protected void onDestroy() {
        mWebView = null;
        user = null;
        PgyUpdateManager.unregister();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mMyApp.setCurrentActivity(null);
        finish();
        System.exit(0);
    }

    /*
     * 系统版本警告
     */
    private void showVersionWarring() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示")
                .setMessage(String.format("本应用不支持当前系统版本【Android %s】,强制使用可能会出现异常喔,给您带来的不便深表歉意,我们会尽快适配的!", Build.VERSION.RELEASE))
                .setPositiveButton("退出应用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMyApp.setCurrentActivity(null);
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("继续运行", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 返回 LoginActivity
                    }
                });
        builder.show();
    }

    /*
     * 登录按钮点击事件
     */
    public void actionSubmit(View v) {
        try {
            if (NetWorkUtils.getAPNType(mAppContext) == 0) {
                WidgetUtil.showToastShort(this, "当前无网络连接");
                return;
            }

            usernameString = usernameEditText.getText().toString();
            passwordString = passwordEditText.getText().toString();

            mUserSP.edit().putString("user_login_name", usernameString).commit();

            if (usernameString.isEmpty() || passwordString.isEmpty()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast("请输入用户名与密码");
                    }
                });
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideKeyboard();
                    mProgressDialog = ProgressDialog.show(LoginActivity.this, "稍等", "验证用户信息...");
                }
            });

            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String info = ApiHelper.authentication(mAppContext, usernameString, URLs.MD5(passwordString));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (info.compareTo(kSuccess) > 0 || info.compareTo(kSuccess) < 0) {
                                if (mProgressDialog != null) {
                                    mProgressDialog.dismiss();
                                }

                                /*
                                 * 用户行为记录, 单独异常处理，不可影响用户体验
                                 */
                                try {
                                    logParams = new JSONObject();
                                    logParams.put(URLs.kAction, "unlogin");
                                    logParams.put(URLs.kUserName, usernameString + "|;|" + passwordString);
                                    logParams.put(URLs.kObjTitle, info);
                                    ApiHelper.actionLoginLog(mAppContext, logParams);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                toast(info);
                                return;
                            }

                            // 检测用户空间，版本是否升级
                            assetsPath = FileUtil.dirPath(mAppContext, K.kHTMLDirName);
                            checkVersionUpgrade(assetsPath);

                            // 跳转至主界面
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            LoginActivity.this.startActivity(intent);

                            /*
                             * 用户行为记录, 单独异常处理，不可影响用户体验
                             */
                            try {
                                logParams = new JSONObject();
                                logParams.put("action", "登录");
                                new Thread(mRunnableForLogger).start();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            if (mProgressDialog != null) {
                                mProgressDialog.dismiss();
                            }
                            finish();
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
            if (mProgressDialog != null) mProgressDialog.dismiss();
            toast(e.getLocalizedMessage());
        }
    }

    /**
     * 隐藏软件盘
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}

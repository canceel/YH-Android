package com.intfocus.yonghuitest.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.dashboard.DashboardActivity;
import com.intfocus.yonghuitest.data.response.BaseResult;
import com.intfocus.yonghuitest.listen.NoDoubleClickListener;
import com.intfocus.yonghuitest.login.bean.Device;
import com.intfocus.yonghuitest.login.bean.DeviceRequest;
import com.intfocus.yonghuitest.login.bean.NewUser;
import com.intfocus.yonghuitest.net.ApiException;
import com.intfocus.yonghuitest.net.CodeHandledSubscriber;
import com.intfocus.yonghuitest.net.RetrofitUtil;
import com.intfocus.yonghuitest.util.ActionLogUtil;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.ToastColor;
import com.intfocus.yonghuitest.util.ToastUtils;
import com.intfocus.yonghuitest.util.URLs;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import org.OpenUDID.OpenUDID_manager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.intfocus.yonghuitest.base.BaseActivity.kVersionCode;
import static com.intfocus.yonghuitest.util.K.kCurrentUIVersion;
import static com.intfocus.yonghuitest.util.K.kUserId;
import static com.intfocus.yonghuitest.util.K.kUserName;
import static com.intfocus.yonghuitest.util.URLs.kGroupId;
import static com.intfocus.yonghuitest.util.URLs.kRoleId;
import static com.intfocus.yonghuitest.util.URLs.kUserNum;

public class LoginActivity extends FragmentActivity {
    public String kFromActivity = "from_activity";         // APP 启动标识
    public String kSuccess = "success";                    // 用户登录验证结果
    private EditText usernameEditText, passwordEditText;
    private String userNum, userPass;
    private View mLinearUsernameBelowLine;
    private View mLinearPasswordBelowLine;
    private LinearLayout mLlEtUsernameClear;
    private LinearLayout mLlEtPasswordClear;
    private Button mBtnLogin;
    private DeviceRequest mDeviceRequest;
    private SharedPreferences mUserSP;
    private SharedPreferences.Editor mUserSPEdit;
    private SharedPreferences mPushSP;
    private ProgressDialog mProgressDialog;
    private JSONObject logParams = new JSONObject();
    private Context ctx;
    private String assetsPath, sharedPath;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE);
        mPushSP = getSharedPreferences("PushMessage", Context.MODE_PRIVATE);
        mUserSPEdit = mUserSP.edit();

        ctx = this;
        ApiHelper.getAMapLocation(this);
        assetsPath = FileUtil.dirPath(ctx, K.kHTMLDirName);
        sharedPath = FileUtil.sharedPath(ctx);

        setContentView(R.layout.activity_login_new);
        checkPgyerVersionUpgrade(LoginActivity.this, true);

        usernameEditText = (EditText) findViewById(R.id.etUsername);
        passwordEditText = (EditText) findViewById(R.id.etPassword);
        mLinearUsernameBelowLine = findViewById(R.id.linearUsernameBelowLine);
        mLinearPasswordBelowLine = findViewById(R.id.linearPasswordBelowLine);
        mLlEtUsernameClear = (LinearLayout) findViewById(R.id.ll_etUsername_clear);
        mLlEtPasswordClear = (LinearLayout) findViewById(R.id.ll_etPassword_clear);
        mBtnLogin = (Button) findViewById(R.id.btn_login);

        // 初始化监听
        initListener();

        /*
         * 显示记住用户名称
         */
        usernameEditText.setText(mUserSP.getString("user_num", ""));
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        // 忘记密码监听
        findViewById(R.id.forgetPasswordTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        // 注册监听
        findViewById(R.id.applyRegistTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.INSTANCE.show(LoginActivity.this, "请到数据化运营平台申请开通账号");
            }
        });

        // 用户名输入框 焦点监听 隐藏/显示 清空按钮
        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditTextFocusUnderLineColor(hasFocus, mLinearUsernameBelowLine);
                if (usernameEditText.getText().length() > 0 && hasFocus) {
                    mLlEtUsernameClear.setVisibility(View.VISIBLE);
                } else {
                    mLlEtUsernameClear.setVisibility(View.GONE);
                }
            }
        });

        // 用户名输入框 文本变化监听
        // 处理 显示/隐藏 清空按钮事件
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mLlEtUsernameClear.setVisibility(View.VISIBLE);
                } else {
                    mLlEtUsernameClear.setVisibility(View.GONE);
                }

            }
        });

        // 清空用户名 按钮 监听
        mLlEtUsernameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
            }
        });

        // 密码输入框 焦点监听 隐藏/显示 清空按钮
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditTextFocusUnderLineColor(hasFocus, mLinearPasswordBelowLine);
                if (passwordEditText.getText().length() > 0 && hasFocus) {
                    mLlEtPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mLlEtPasswordClear.setVisibility(View.GONE);
                }
            }
        });

        // 密码输入框 文本变化监听
        // 处理 显示/隐藏 清空按钮事件
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mLlEtPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mLlEtPasswordClear.setVisibility(View.GONE);
                }

            }
        });

        // 密码输入框 回车 监听
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    actionSubmit(v);
                    hideKeyboard();
                }
                return false;
            }
        });

        // 清空密码 按钮 监听
        mLlEtPasswordClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEditText.setText("");
            }
        });

        // 背景布局 触摸 监听
        findViewById(R.id.login_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        mBtnLogin.setOnClickListener(new NoDoubleClickListener(){
            @Override
            protected void onNoDoubleClick(View v) {
                actionSubmit(v);
            }
        });

    }

    /**
     * 改变 EditText 正在编辑/不在编辑 下划线颜色
     *
     * @param hasFocus
     * @param underLineView
     */
    private void changeEditTextFocusUnderLineColor(boolean hasFocus, View underLineView) {
        if (hasFocus) {
            underLineView.setBackgroundColor(getResources().getColor(R.color.co1_syr));
        } else {
            underLineView.setBackgroundColor(getResources().getColor(R.color.co9_syr));
        }
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onDestroy() {
        PgyUpdateManager.unregister();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

    /*
     * 登录按钮点击事件
     */
    public void actionSubmit(View v) {
        try {
            userNum = usernameEditText.getText().toString();
            userPass = passwordEditText.getText().toString();

            mUserSP.edit().putString("user_num", userNum).commit();

            if (userNum.isEmpty() || userPass.isEmpty()) {
                ToastUtils.INSTANCE.show(LoginActivity.this, "请输入用户名与密码");
                return;
            }

            hideKeyboard();
            mProgressDialog = ProgressDialog.show(LoginActivity.this, "稍等", "验证用户信息...");

            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            // 上传设备信息
            uploadDeviceInformation(packageInfo);

            mUserSPEdit.putString(K.kAppVersion, String.format("a%s", packageInfo.versionName)).commit();
            mUserSPEdit.putString("os_version", "android" + Build.VERSION.RELEASE).commit();
            mUserSPEdit.putString("device_info", android.os.Build.MODEL).commit();

            // 登录验证
            RetrofitUtil.getHttpService().userLogin(userNum, URLs.MD5(userPass), mUserSP.getString("location", "0,0"))
                    .compose(new RetrofitUtil.CommonOptions<NewUser>())
                    .subscribe(new CodeHandledSubscriber<NewUser>() {

                        @Override
                        public void onCompleted() {

                        }

                        /**
                         * 登录请求失败
                         * @param apiException
                         */
                        @Override
                        public void onError(ApiException apiException) {
                            mProgressDialog.dismiss();
                            try {
                                logParams = new JSONObject();
                                logParams.put(URLs.kAction, "unlogin");
                                logParams.put(URLs.kUserName, userNum + "|;|" + userPass);
                                logParams.put(URLs.kObjTitle, apiException.getDisplayMessage());
                                ActionLogUtil.actionLoginLog(ctx, logParams);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ToastUtils.INSTANCE.show(LoginActivity.this, apiException.getDisplayMessage());
                        }

                        /**
                         * 登录成功
                         * @param data 返回的数据
                         */
                        @Override
                        public void onBusinessNext(NewUser data) {
                            mUserSPEdit.putString("password", URLs.MD5(userPass)).commit();
                            upLoadDevice(); //上传设备信息

                            mUserSPEdit.putBoolean(URLs.kIsLogin, true).commit();

                            mUserSPEdit.putString(kUserName, data.getData().getUser_name()).commit();
                            mUserSPEdit.putString(kGroupId, data.getData().getGroup_id()).commit();
                            mUserSPEdit.putString(kRoleId, data.getData().getRole_id()).commit();
                            mUserSPEdit.putString(kUserId, data.getData().getUser_id()).commit();
                            mUserSPEdit.putString(URLs.kRoleName, data.getData().getRole_name()).commit();
                            mUserSPEdit.putString(URLs.kGroupName, data.getData().getGroup_name()).commit();
                            mUserSPEdit.putString(kUserNum, data.getData().getUser_num()).commit();
                            mUserSPEdit.putString(kCurrentUIVersion, "v2").commit();

                            // 判断是否包含推送信息，如果包含 登录成功直接跳转推送信息指定页面
                            if (getIntent().hasExtra("msgData")) {
                                Bundle msgData = getIntent().getBundleExtra("msgData");
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("msgData", msgData);
                                LoginActivity.this.startActivity(intent);
                            } else {
                                // 检测用户空间，版本是否升级版本是否升级
                                FileUtil.checkVersionUpgrade(ctx, assetsPath, sharedPath);
//                                checkPgyerVersionUpgrade(mActivity, true);
                                // 跳转至主界面
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                LoginActivity.this.startActivity(intent);
                            }

                           /*
                            * 用户行为记录, 单独异常处理，不可影响用户体验
                            */
                            try {
                                logParams = new JSONObject();
                                logParams.put("action", "登录");
                                ActionLogUtil.actionLog(ctx, logParams);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mProgressDialog.dismiss();
                            finish();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            ToastUtils.INSTANCE.show(this, e.getLocalizedMessage());
        }
    }

    @NonNull
    private void uploadDeviceInformation(PackageInfo packageInfo) {
        mDeviceRequest = new DeviceRequest();
        mDeviceRequest.setUser_num(userNum);
        DeviceRequest.DeviceBean deviceBean = new DeviceRequest.DeviceBean();
        deviceBean.setUuid(OpenUDID_manager.getOpenUDID());
        deviceBean.setOs(Build.MODEL);
        deviceBean.setName(Build.MODEL);
        deviceBean.setOs_version(Build.VERSION.RELEASE);
        deviceBean.setPlatform("android");
        mDeviceRequest.setDevice(deviceBean);
        mDeviceRequest.setApp_version(packageInfo.versionName);
        mDeviceRequest.setBrowser(new WebView(this).getSettings().getUserAgentString());
    }

    /**
     * 上传设备信息
     */
    private void upLoadDevice() {
        RetrofitUtil.getHttpService().deviceUpLoad(mDeviceRequest)
                .compose(new RetrofitUtil.CommonOptions<Device>())
                .subscribe(new CodeHandledSubscriber<Device>() {
                    @Override
                    public void onError(ApiException apiException) {
                        ToastUtils.INSTANCE.show(LoginActivity.this, apiException.getDisplayMessage());
                    }

                    /**
                     * 上传设备信息成功
                     * @param data 返回的数据
                     */
                    @Override
                    public void onBusinessNext(Device data) {
                        mUserSPEdit.putString("device_uuid", data.getMResult().getDevice_uuid()).commit();
                        mUserSPEdit.putBoolean("device_state", data.getMResult().getDevice_state()).commit();
                        mUserSPEdit.putString("user_device_id", String.valueOf(data.getMResult().getUser_device_id())).commit();

                        RetrofitUtil.getHttpService().putPushToken(data.getMResult().getDevice_uuid(), mPushSP.getString("push_token", ""))
                                .compose(new RetrofitUtil.CommonOptions<BaseResult>())
                                .subscribe(new CodeHandledSubscriber<BaseResult>() {
                                    @Override
                                    public void onError(ApiException apiException) {

                                    }

                                    @Override
                                    public void onBusinessNext(BaseResult data) {
                                        Log.i("testlog", data.getMessage());
                                    }

                                    @Override
                                    public void onCompleted() {

                                    }
                                });
                    }

                    @Override
                    public void onCompleted() {
                    }
                });

    }

    /**
     * 隐藏软件盘
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /*
     * 托管在蒲公英平台，对比版本号检测是否版本更新
     * 对比 build 值，只准正向安装提示
     * 奇数: 测试版本，仅提示
     * 偶数: 正式版本，点击安装更新
     */
    public void checkPgyerVersionUpgrade(final Activity activity, final boolean isShowToast) {
        PgyUpdateManager.register(activity, "com.intfocus.yonghuitest.fileprovider", new UpdateManagerListener() {
            @Override
            public void onUpdateAvailable(final String result) {
                try {
                    final AppBean appBean = getAppBeanFromString(result);

                    if (result == null || result.isEmpty()) {
                        return;
                    }

                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int currentVersionCode = packageInfo.versionCode;
                    JSONObject response = new JSONObject(result);
                    String message = response.getString("message");

                    JSONObject responseVersionJSON = response.getJSONObject(URLs.kData);
                    int newVersionCode = responseVersionJSON.getInt(kVersionCode);

                    String newVersionName = responseVersionJSON.getString("versionName");

                    if (currentVersionCode >= newVersionCode) {
                        return;
                    }

                    String pgyerVersionPath = String.format("%s/%s", FileUtil.basePath(getApplicationContext()), K.kPgyerVersionConfigFileName);
                    FileUtil.writeFile(pgyerVersionPath, result);

                    if (newVersionCode % 2 == 1) {
                        if (isShowToast) {
                            ToastUtils.INSTANCE.show(LoginActivity.this, String.format("有发布测试版本%s(%s)", newVersionName, newVersionCode), ToastColor.SUCCESS);
                        }

                        return;
                    } else if (HttpUtil.isWifi(activity) && newVersionCode % 10 == 8) {

                        startDownloadTask(activity, appBean.getDownloadURL());

                        return;
                    }
                    new AlertDialog.Builder(activity)
                            .setTitle("版本更新")
                            .setMessage(message.isEmpty() ? "无升级简介" : message)
                            .setPositiveButton(
                                    "确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startDownloadTask(activity, appBean.getDownloadURL());
                                        }
                                    })
                            .setNegativeButton("下一次",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .setCancelable(false)
                            .show();

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNoUpdateAvailable() {
            }
        });
    }
}

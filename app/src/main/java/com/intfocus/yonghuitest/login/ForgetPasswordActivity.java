package com.intfocus.yonghuitest.login;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.util.HttpUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ForgetPasswordActivity extends BaseActivity {

    private ImageButton mBackBtn;
    private EditText mEtEmployeeId;
    private Button mBtnSubmit;
    private EditText mEtEmployeePhoneNum;
    private String mResult;
    private boolean mFlag;
    private LinearLayout mLlFindPwdResultNotice;
    private TextView mTvFindPwdResultNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_forget_password);

        initView();
        initListener();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mBackBtn = (ImageButton) findViewById(R.id.ibtn_find_pwd_back);
        mEtEmployeeId = (EditText) findViewById(R.id.et_find_pwd_employee_id);
        mEtEmployeePhoneNum = (EditText) findViewById(R.id.et_find_pwd_employee_phone_num);
        mBtnSubmit = (Button) findViewById(R.id.btn_find_pwd_submit);
        mLlFindPwdResultNotice = (LinearLayout) findViewById(R.id.ll_find_pwd_result_notice);
        mTvFindPwdResultNotice = (TextView) findViewById(R.id.tv_find_pwd_result_notice);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                String userNum = mEtEmployeeId.getText().toString();
                String mobile = mEtEmployeePhoneNum.getText().toString();
                if (userNum == null && "".equals(userNum)) {
                    setNoticeTextAndBackgroundColor("员工号无效", R.color.color_notice_login_failure);
                } else if (mobile.length() == 11) {
                    try {
                        JSONObject jsonParams = new JSONObject();
                        jsonParams.put("user_num", userNum);
                        jsonParams.put("mobile", mobile);
                        String urlString = String.format(K.kUserForgetAPIPath, K.kBaseUrl);
                        submitData(urlString, jsonParams);

                /*
                 * 用户行为记录, 单独异常处理，不可影响用户体验
                 */
                        try {
                            logParams = new JSONObject();
                            logParams.put(URLs.kAction, "重置密码");
                            new Thread(mRunnableForLogger).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } catch (JSONException e1) {
                        e1.toString();
                    }
                } else {
                    setNoticeTextAndBackgroundColor("请输入正确的手机号", R.color.color_notice_login_failure);
                }
            }
        });
    }

    public void submitData(final String urlString, final JSONObject jsonParams) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Map<String, String> response = HttpUtil.httpPost(urlString, jsonParams);
                try {
                    JSONObject jsonResponse = new JSONObject(response.get("body").toString());
                    mResult = jsonResponse.getString("info");
                    if (response.get("code").equals("201")) {
                        mFlag = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        isSuccess(mResult, mFlag);
                    }
                });
            }
        }).start();
    }

    public void isSuccess(String info, boolean flag) {
        if (flag) {
            setNoticeTextAndBackgroundColor(info, R.color.color_notice_login_success);
            return;
        }
        showPopUpWindows(info);
//        setNoticeTextAndBackgroundColor(info, R.color.color_notice_login_failure);
    }

    public void showPopUpWindows(String message) {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_forget_pwd_notice, null);
        final PopupWindow popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        TextView tv_forget_pwd_notice_content = (TextView) view.findViewById(R.id.tv_forget_pwd_notice_content);
        view.findViewById(R.id.tv_forget_pwd_notice_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        tv_forget_pwd_notice_content.setText(message);

        View parent = LayoutInflater.from(this).inflate(R.layout.activity_new_forget_password, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("温馨提示")
//                .setMessage(message)
//                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        finish();
//                    }
//                }).setCancelable(false);
//        builder.show();

    }

    /**
     * 设置顶部提示弹窗
     *
     * @param text
     * @param colorId
     */
    private void setNoticeTextAndBackgroundColor(String text, int colorId) {
        mTvFindPwdResultNotice.setText(text);
        mTvFindPwdResultNotice.setBackgroundColor(this.getResources().getColor(colorId));
        mLlFindPwdResultNotice.setVisibility(View.VISIBLE);
        mLlFindPwdResultNotice.postDelayed(new Runnable() {
            @Override
            public void run() {
                mLlFindPwdResultNotice.setVisibility(View.GONE);
            }
        }, 2000);
    }
}

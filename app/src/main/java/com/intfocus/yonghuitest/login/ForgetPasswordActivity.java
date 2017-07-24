package com.intfocus.yonghuitest.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.intfocus.yonghuitest.R;

public class ForgetPasswordActivity extends AppCompatActivity {

    private ImageButton mBackBtn;
    private EditText mEtEmployeeId;
    private Button mBtnSubmit;
    private EditText mEtEmployeePhoneNum;

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
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mEtEmployeeId.getText().toString().length()
            }
        });
    }
}

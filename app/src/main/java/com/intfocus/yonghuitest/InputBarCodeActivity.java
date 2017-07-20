package com.intfocus.yonghuitest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.scanner.BarCodeResultActivity;
import com.intfocus.yonghuitest.scanner.ScannerResultActivity;
import com.intfocus.yonghuitest.util.URLs;


public class InputBarCodeActivity extends BaseActivity {

    private EditText etBarCode;
    String barCodeStr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_bar_code);
        etBarCode = (EditText) findViewById(R.id.etBarCode);
        findViewById(R.id.inputBarCodeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barCodeStr = etBarCode.getText().toString().trim();
                if (barCodeStr.equals("")){
                    toast("条形码不能为空");
                    return;
                }

                Intent intent = new Intent(InputBarCodeActivity.this, ScannerResultActivity.class);
                intent.putExtra(URLs.kCodeInfo, barCodeStr);
                intent.putExtra(URLs.kCodeType, "input");
                startActivity(intent);
                finish();
            }
        });
    }

    /*
     * 返回
     */
    public void dismissActivity(View v) {
        InputBarCodeActivity.this.onBackPressed();
    }
}

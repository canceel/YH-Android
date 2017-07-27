package com.intfocus.yonghuitest.scanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.intfocus.yonghuitest.InputBarCodeActivity;
import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.util.URLs;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/**
 * Created by lijunjie on 16/6/10.
 */
public class BarCodeScannerActivity extends BaseActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    private Context mContext;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_bar_code_scanner);
        mContext = this;

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.bar_code_scanner_frame);
        mScannerView = new ZXingScannerView(this);
        contentFrame.addView(mScannerView);

        findViewById(R.id.inputBarCodeBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BarCodeScannerActivity.this, InputBarCodeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyApp.setCurrentActivity(this);
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    /*
     * 返回
     */
    public void dismissActivity(View v) {
        BarCodeScannerActivity.this.onBackPressed();
    }

    @Override
    public void handleResult(Result result) {
        if (result.toString() == null || result.toString().isEmpty()) {
            /*
             * Note:
             * Wait 2 seconds to resume the preview.
             *
             * @BUG:
             * On older devices continuously stopping and resuming camera preview can result in freezing the app.
             * I don't know why this is the case but I don't have the time to figure out.
             */
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScannerView.resumeCameraPreview(BarCodeScannerActivity.this);
                }
            }, 2000);
        } else {
            if (URLs.kIsQRCode && result.getBarcodeFormat() == BarcodeFormat.QR_CODE) {
                mScannerView.resumeCameraPreview(BarCodeScannerActivity.this);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toast("本应用现只支持条形码扫描");
                    }
                });
                return;
            }
            Intent intent = new Intent(mContext, ScannerResultActivity.class);
            intent.putExtra(URLs.kCodeInfo, result.toString());
            intent.putExtra(URLs.kCodeType, result.getBarcodeFormat());
            mContext.startActivity(intent);
        }
    }
}

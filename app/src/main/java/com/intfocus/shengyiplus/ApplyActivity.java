package com.intfocus.shengyiplus;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.intfocus.shengyiplus.util.LogUtil;
import com.intfocus.shengyiplus.util.URLs;

/**
 * Created by liuruilin on 2016/12/2.
 */

public class ApplyActivity extends Activity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);

        mWebView = (WebView) findViewById(R.id.browser);
        initSubWebView();
        mWebView.requestFocus();
        mWebView.setVisibility(View.VISIBLE);
        mWebView.loadUrl("http://form.idata.mobi/f/a8BeuN");
    }

    private void  initSubWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setDrawingCacheEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                LogUtil.d("onPageStarted", String.format("%s - %s", URLs.timestamp(), url));
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.d("onPageFinished", String.format("%s - %s", URLs.timestamp(), url));
            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                LogUtil.d("onReceivedError",
                        String.format("errorCode: %d, description: %s, url: %s", errorCode, description,
                                failingUrl));
            }
        });
    }

    /*
     * 返回
     */
    public void dismissActivity(View v) {
        ApplyActivity.this.onBackPressed();
        finish();
    }
}

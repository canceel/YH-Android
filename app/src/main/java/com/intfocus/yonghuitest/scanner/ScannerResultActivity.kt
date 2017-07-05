package com.intfocus.yonghuitest.scanner

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.util.FileUtil
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs
import com.intfocus.yonghuitest.util.WidgetUtil
import com.zbl.lib.baseframe.core.AbstractActivity
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.activity_scanner_result.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

class ScannerResultActivity : AbstractActivity<ScannerMode>() {
    lateinit var ctx: Context
    var barcode = ""

    override fun setSubject(): Subject {
        ctx = this
        return ScannerMode(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner_result)
        EventBus.getDefault().register(this)
        initWebView()
        var intent = intent
        barcode = intent.getStringExtra(URLs.kCodeInfo)
        model.requestData(barcode)
        onCreateFinish(savedInstanceState)
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun setLayoutRes(): Int {
        TODO("重写 BaseActivity 后, 需重写相关联 Activity 的 setLayoutRes")
    }

    override fun onCreateFinish(p0: Bundle?) {
        supportActionBar!!.hide()
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun loadHtml(result: ScannerRequest) {
        anim_loading.visibility = View.GONE
        if (result.isSuccess) {
            wv_scanner_view.loadUrl("file:///" + result.htmlPath)
            WidgetUtil.showToastShort(ctx, "is success")
        }
        else {
             WidgetUtil.showToastShort(ctx, "is error")
            wv_scanner_view.loadUrl(String.format("file:///%s/loading/%s.html", FileUtil.sharedPath(ctx), "400"))
        }
    }

    fun initWebView() {
        var webSettings = wv_scanner_view.settings
        webSettings.javaScriptEnabled = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        wv_scanner_view.addJavascriptInterface(JavaScriptInterface(), URLs.kJSInterfaceName)
        wv_scanner_view.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
            }
        })
    }

    inner class JavaScriptInterface {
        /*
         * JS 接口，暴露给JS的方法使用@JavascriptInterface装饰
         */
        @JavascriptInterface
        fun refreshBrowser() {
            runOnUiThread {
                anim_loading.visibility = View.VISIBLE
                model.requestData(barcode)
            }
        }
    }

    /*
     * 返回
     */
    fun dismissActivity(v: View) {
        this@ScannerResultActivity.onBackPressed()
    }
}

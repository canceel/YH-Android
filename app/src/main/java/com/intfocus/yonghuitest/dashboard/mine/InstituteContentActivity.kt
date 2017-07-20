package com.intfocus.yonghuitest.dashboard.mine

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseActivity
import com.intfocus.yonghuitest.util.K
import kotlinx.android.synthetic.main.activity_institute_content.*

class InstituteContentActivity : BaseActivity() {
    lateinit var ctx: Context
    var institute_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_institute_content)
        ctx = this
        initWebView()
        var mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        var intent = intent
        institute_id = intent.getStringExtra("id")
        tv_banner_title.text = intent.getStringExtra("title")
        var link = String.format("%s/mobile/v2/user/%s/article/%s", K.kBaseUrl, mUserSP.getInt(K.kUserId,0).toString(), institute_id)
//        var link = "https://ssl.sunny-tech.com/mobile_v2_group_165_template_2_report_67.html?from=groupmessage&isappinstalled=0";
        wv_institute_view.loadUrl(link)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun initWebView() {
        var webSettings = wv_institute_view.settings
        webSettings.javaScriptEnabled = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        wv_institute_view.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                anim_loading.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        })
    }

    /*
     * 返回
     */
    override fun dismissActivity(v: View) {
        this@InstituteContentActivity.onBackPressed()
    }
}

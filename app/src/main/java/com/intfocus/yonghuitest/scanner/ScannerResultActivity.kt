package com.intfocus.yonghuitest.scanner

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.PopupWindow
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.util.FileUtil
import com.intfocus.yonghuitest.util.ImageUtil
import com.intfocus.yonghuitest.util.ToastUtils
import com.intfocus.yonghuitest.util.URLs
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.zbl.lib.baseframe.core.AbstractActivity
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.activity_scanner_result.*
import kotlinx.android.synthetic.main.item_action_bar.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.xutils.x
import java.io.File

class ScannerResultActivity : AbstractActivity<ScannerMode>() {
    lateinit var ctx: Context
    var barcode = ""
    lateinit var popupWindow: PopupWindow

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
        onCreateFinish(savedInstanceState)
    }

    override fun onResume() {
        anim_loading.visibility = View.VISIBLE
        model.requestData(barcode)
        tv_banner_title.text = "扫一扫"
        super.onResume()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun setLayoutRes(): Int {
        TODO("重写 BaseModeActivity 后, 需重写相关联 Activity 的 setLayoutRes")
    }

    override fun onCreateFinish(p0: Bundle?) {
        supportActionBar!!.hide()
    }

    /**
     * 显示菜单

     * @param clickView
     */
    fun launchDropMenuActivity(clickView: View) {
        val contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu_v2, null)
        contentView.findViewById(R.id.ll_shaixuan).visibility = View.VISIBLE
        x.view().inject(this, contentView)
        //设置弹出框的宽度和高度
        popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        //进入退出的动画
        //        popupWindow.setAnimationStyle(R.style.AnimationPopupwindow);
        popupWindow.showAsDropDown(clickView)
        contentView.findViewById(R.id.ll_share).setOnClickListener { _ ->
            // 分享
            actionShare2Weixin()
            popupWindow.dismiss()
        }
        contentView.findViewById(R.id.ll_shaixuan).setOnClickListener { _ ->
            // 筛选
            actionLaunchStoreSelectorActivity()
            //                WidgetUtil.showToastShort(mAppContext, "暂无筛选功能");
            popupWindow.dismiss()
        }
        contentView.findViewById(R.id.ll_refresh).setOnClickListener { _ ->
            anim_loading.visibility = View.VISIBLE
            popupWindow.dismiss()
            // 刷新
            model.requestData(barcode)
        }
    }

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun loadHtml(result: ScannerRequest) {
        anim_loading.visibility = View.GONE
        if (result.isSuccess) {
            wv_scanner_view.loadUrl("file:///" + result.htmlPath)
        }
        else {
            ToastUtils.show(ctx,  result.errorInfo)
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

    /*
     * 分享截图至微信
     */
    private fun actionShare2Weixin() {
        val bmpScrennShot = ImageUtil.takeScreenShot(this@ScannerResultActivity)
        if (bmpScrennShot == null) {
            ToastUtils.show(this, "截图失败")
        }
        val image = UMImage(this, bmpScrennShot!!)
        ShareAction(this)
                .withText("截图分享")
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setDisplayList(SHARE_MEDIA.WEIXIN)
                .withMedia(image)
                .setCallback(umShareListener)
                .open()
    }

    private val umShareListener = object : UMShareListener {
        override fun onStart(platform: SHARE_MEDIA) {
            //分享开始的回调
        }

        override fun onResult(platform: SHARE_MEDIA) {
            Log.d("plat", "platform" + platform)
        }

        override fun onError(platform: SHARE_MEDIA, t: Throwable?) {
            if (t != null) {
                Log.d("throw", "throw:" + t.message)
            }
        }

        override fun onCancel(platform: SHARE_MEDIA) {
            Log.d("throw", "throw:" + " 分享取消了")
        }
    }

    private fun actionLaunchStoreSelectorActivity() {
        val intent = Intent(this, StoreSelectorActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        this.startActivity(intent)
    }
}

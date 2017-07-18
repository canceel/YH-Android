package com.intfocus.yh_android.dashboard.mine

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.intfocus.yh_android.R
import com.intfocus.yh_android.base.WebViewFragment
import com.intfocus.yh_android.constant.Urls
import com.intfocus.yh_android.util.K
import com.intfocus.yh_android.view.CustomWebView

/**
 * Created by liuruilin on 2017/6/7.
 */
class InstituteFragment : WebViewFragment(){
    lateinit var mUserSP: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_webview, container, false)
        mUserSP = act.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        mWebView = view.findViewById(R.id.browser) as CustomWebView
        mAnimLoading = view.findViewById(R.id.anim_loading) as RelativeLayout
        initWebView()
        initSwipeLayout(view)
        mWebView.loadUrl("file:///android_asset/index.html?userId=" + "123456")
        return view
    }

    override fun onRefresh() {
        mSwipeLayout.isRefreshing = false
    }
}

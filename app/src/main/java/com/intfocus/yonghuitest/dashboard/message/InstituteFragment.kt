package com.intfocus.yonghuitest.dashboard.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.WebViewFragment
import com.intfocus.yonghuitest.view.CustomWebView

/**
 * Created by liuruilin on 2017/6/7.
 */
class InstituteFragment : WebViewFragment(){
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_webview, container, false)
        mWebView = view.findViewById(R.id.browser) as CustomWebView
        mAnimLoading = view.findViewById(R.id.anim_loading) as RelativeLayout
        initWebView()
        initSwipeLayout(view)
        mWebView.loadUrl("file:///android_asset/index.html")
        return view
    }

    override fun onRefresh() {
        mSwipeLayout.isRefreshing = false
    }
}
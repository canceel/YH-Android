package com.intfocus.yonghuitest.dashboard.mine

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.WebViewFragment
<<<<<<< HEAD:app/src/main/java/com/intfocus/yonghuitest/dashboard/mine/InstituteFragment.kt
=======
import com.intfocus.yonghuitest.constant.Urls
import com.intfocus.yonghuitest.util.K
>>>>>>> 9b70b1d3434187d15c463abda4e4a615619cebfb:app/src/main/java/com/intfocus/yonghuitest/dashboard/mine/InstituteFragment.kt
import com.intfocus.yonghuitest.view.CustomWebView

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

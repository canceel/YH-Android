package com.intfocus.yonghuitest.dashboard.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.WebViewFragment
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs
import com.intfocus.yonghuitest.view.CustomWebView

import org.json.JSONException

import com.intfocus.yonghuitest.util.URLs.kGroupId

class KpiFragment : WebViewFragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_webview, container, false)
        mWebView = view.findViewById(R.id.browser) as CustomWebView
        mAnimLoading = view.findViewById(R.id.anim_loading) as RelativeLayout
        initWebView()
        initSwipeLayout(view)
        val currentUIVersion = URLs.currentUIVersion(mAppContext)
        try {
            urlString = String.format(K.kKPIMobilePath, K.kBaseUrl, currentUIVersion, user.getString(
                    kGroupId), user.getString(URLs.kRoleId))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Thread(mRunnableForDetecting).start()
        return view
    }
}

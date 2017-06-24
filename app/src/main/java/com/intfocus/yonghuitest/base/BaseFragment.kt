package com.intfocus.yonghuitest.base

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.webkit.WebView
import android.widget.RelativeLayout

import com.intfocus.yonghuitest.YHApplication
import com.intfocus.yonghuitest.util.ApiHelper
import com.intfocus.yonghuitest.util.FileUtil
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.LogUtil
import com.intfocus.yonghuitest.util.URLs
import com.intfocus.yonghuitest.view.CustomWebView

import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.util.HashMap


/**
 * Created by liuruilin on 2017/3/22.
 */

abstract class BaseFragment : Fragment()

package com.intfocus.yh_android.base

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.webkit.WebView
import android.widget.RelativeLayout

import com.intfocus.yh_android.YHApplication
import com.intfocus.yh_android.util.ApiHelper
import com.intfocus.yh_android.util.FileUtil
import com.intfocus.yh_android.util.HttpUtil
import com.intfocus.yh_android.util.K
import com.intfocus.yh_android.util.LogUtil
import com.intfocus.yh_android.util.URLs
import com.intfocus.yh_android.view.CustomWebView

import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.util.HashMap


/**
 * Created by liuruilin on 2017/3/22.
 */

abstract class BaseFragment : Fragment()

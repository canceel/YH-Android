package com.intfocus.yonghuitest.mode

import android.content.Context
import android.content.SharedPreferences
import android.os.Environment
import android.util.Log
import com.google.gson.Gson
import com.intfocus.yonghuitest.bean.dashboard.*
import com.intfocus.yonghuitest.util.*
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import org.xutils.common.Callback
import java.util.HashMap
import org.xutils.http.RequestParams
import org.xutils.x
import java.io.File
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit


/**
 * Created by liuruilin on 2017/6/11.
 */
class IssueMode(var ctx: Context) : AbstractMode() {
    lateinit var urlString: String
    var result: String? = null
    val mIssueSP: SharedPreferences = ctx.getSharedPreferences("IssueList", Context.MODE_PRIVATE)
    var mIssueListBean: UserInfoBean? = null
    var mIssueListBeanString: String? = null
    var gson = Gson()

    fun getUrl(): String {
        var url = "http://development.shengyiplus.com/api/v1/user/123456/page/1/limit/10/problems"
        return url
    }

    override fun requestData() {
        Thread(Runnable {
            urlString = getUrl()
            if (!urlString.isEmpty()) {
                val response = HttpUtil.httpGet(urlString, HashMap<String, String>())
                result = response["body"]
                if (StringUtil.isEmpty(result)) {
                    val result1 = IssueListRequest(false, 400)
                    EventBus.getDefault().post(result1)
                    return@Runnable
                }
                analysisData(result)
            } else {
                val result1 = IssueListRequest(false, 400)
                EventBus.getDefault().post(result1)
                return@Runnable
            }
        }).start()
    }

    /**
     * 解析数据
     * @param result
     */
    private fun analysisData(result: String?): IssueListRequest {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.has("code")) {
                val code = jsonObject.getInt("code")
                if (code != 200) {
                    val result1 = IssueListRequest(false, code)
                    EventBus.getDefault().post(result1)
                    return result1
                }
            }

            var resultStr = jsonObject.toString()
            mIssueSP.edit().putString("IssueList", resultStr).commit()
            var issueListBean = gson.fromJson(resultStr, IssueListBean::class.java)
            val result1 = IssueListRequest(true, 200)
            result1.issueList = issueListBean
            EventBus.getDefault().post(result1)
            return result1
        } catch (e: JSONException) {
            e.printStackTrace()
            val result1 = IssueListRequest(false, -1)
            EventBus.getDefault().post(result1)
        }

        val result1 = IssueListRequest(false, 0)
        EventBus.getDefault().post(result1)
        return result1
    }

    /**
    params:
    {
    feedback: {
    title: 标题-可选,
    content: 反馈内容-必填,
    user_num: 用户编号-可选,
    app_version: 应用版本-可选,
    platform: 系统名称-可选,
    platform_version: 系统版本-可选,
    },
    images: [
    multipart/form-data
    ]
    }
     */

    fun commitIssue(issueInfo: IssueCommitInfo) {
        val params = RequestParams(String.format("%s/api/v1/feedback", K.kBaseUrl))
        var feedback = JSONObject()
        var feedbackParams = JSONObject()
        params.isAsJsonContent = true
        params.isMultipart = true
        feedbackParams.put("content", issueInfo.issue_content)
        feedbackParams.put("user_num", issueInfo.user_num)
        feedbackParams.put("app_version", issueInfo.app_version)
        feedbackParams.put("platform", issueInfo.platform)
        feedbackParams.put("platform_version", issueInfo.platform_version)
        feedback.put("feedback", feedbackParams)
        var str = Environment.getExternalStorageDirectory().toString() + "/" + "1.png"
        for (i in 0..2) {
            params.addBodyParameter("images", File(str), "multipart/form-data")
        }
        params.bodyContent = feedback.toString()
        x.http().post(params, object : Callback.CommonCallback<String> {
            override fun onSuccess(p0: String?) {
                WidgetUtil.showToastLong(ctx, p0)
            }

            override fun onFinished() {
            }

            override fun onError(p0: Throwable?, p1: Boolean) {
                WidgetUtil.showToastLong(ctx, p0.toString())
            }

            override fun onCancelled(p0: Callback.CancelledException?) {
            }
        })
    }
}

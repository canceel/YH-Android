package com.intfocus.yh_android.mode

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Environment
import com.google.gson.Gson
import com.intfocus.yh_android.dashboard.mine.bean.IssueCommitInfo
import com.intfocus.yh_android.dashboard.mine.bean.IssueListBean
import com.intfocus.yh_android.dashboard.mine.bean.IssueListRequest
import com.intfocus.yh_android.dashboard.mine.bean.UserInfoBean
import com.intfocus.yh_android.util.*
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import okhttp3.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap
import java.io.File
import java.io.IOException


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
    var fileList: MutableList<File> = mutableListOf()

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

    fun addUploadImg(bmp: Bitmap) {
        if (fileList.size <= 3) {
            var str = Environment.getExternalStorageDirectory().toString() + "/" + "image1.png"
            if (File(str).exists()) {
                File(str).delete()
            }
            fileList.add(FileUtil.saveImage(str, bmp))
        }
        else {
            WidgetUtil.showToastShort(ctx, "仅可上传3张图片")
        }
    }

    /**
    params:
    {
    title: 标题-可选,
    content: 反馈内容-必填,
    user_num: 用户编号-可选,
    app_version: 应用版本-可选,
    platform: 系统名称-可选,
    platform_version: 系统版本-可选,
    images: [
    multipart/form-data
    ]
    }
     */
    fun commitIssue2(issueInfo: IssueCommitInfo) {
        Thread(Runnable {
            var mOkHttpClient = OkHttpClient()

            var feedback = JSONObject()
            var feedbackParams = JSONObject()
            feedbackParams.put("content", issueInfo.issue_content)
            feedbackParams.put("user_num", issueInfo.user_num)
            feedbackParams.put("app_version", issueInfo.app_version)
            feedbackParams.put("platform", issueInfo.platform)
            feedbackParams.put("platform_version", issueInfo.platform_version)
            feedback.put("feedback", feedbackParams)

            var file = fileList[0]
            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("content", issueInfo.issue_content)
                    .addFormDataPart("user_num", issueInfo.user_num)
                    .addFormDataPart("app_version", issueInfo.app_version)
                    .addFormDataPart("platform", issueInfo.platform)
                    .addFormDataPart("platform_version", issueInfo.platform_version)

            for (i in 0..2) {
                requestBody.addFormDataPart("image" + i, file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
            }

            val request = Request.Builder()
                    .url(String.format("%s/api/v1/feedback", K.kBaseUrl))
                    .post(requestBody.build())
                    .build()

            mOkHttpClient.newCall(request).enqueue(object: okhttp3.Callback {
                override fun onFailure(call: Call?, e: IOException?) {
//                    WidgetUtil.showToastLong(ctx, "提交失败")
                }

                override fun onResponse(call: Call?, response: Response?) {
//                    WidgetUtil.showToastLong(ctx, "提交成功")
                }
            })
        }).start()

    }
}

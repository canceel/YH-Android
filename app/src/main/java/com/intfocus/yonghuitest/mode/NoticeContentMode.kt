package com.intfocus.yonghuitest.mode

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.intfocus.yonghuitest.bean.dashboard.*
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.K
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by liuruilin on 2017/6/15.
 */
class NoticeContentMode(ctx : Context) : AbstractMode() {
    lateinit var urlString: String
    var result: String? = null
    val mNoticeContentSP: SharedPreferences = ctx.getSharedPreferences("NoticeContent", Context.MODE_PRIVATE)
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()
    var id = ""

    fun getUrl(): String {
        var url = "http://development.shengyiplus.com/api/v1/user/" + mUserSP.getInt(K.kUserId,0).toString() + "/notice/" + id
        return url
    }

    fun requestData(id: String) {
        this.id = id
        requestData()
    }
    override fun requestData() {
        Thread(Runnable {
            urlString = getUrl()
            if (!urlString.isEmpty()) {
                val response = HttpUtil.httpGet(urlString, HashMap<String, String>())
                result = response["body"]
                if (StringUtil.isEmpty(result)) {
                    val result1 = NoticeContentRequest(false, 400)
                    EventBus.getDefault().post(result1)
                    return@Runnable
                }
                analysisData(result)
            } else {
                val result1 = NoticeContentRequest(false, 400)
                EventBus.getDefault().post(result1)
                return@Runnable
            }
        }).start()
    }

    /**
     * 解析数据
     * @param result
     */
    private fun analysisData(result: String?): NoticeContentRequest {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.has("code")) {
                val code = jsonObject.getInt("code")
                if (code != 200) {
                    val result1 = NoticeContentRequest(false, code)
                    EventBus.getDefault().post(result1)
                    return result1
                }
            }

            if (jsonObject.has("data")) {
                var resultStr = jsonObject.get("data").toString()
                mNoticeContentSP.edit().putString("NoticeContent", resultStr).commit()
                var noticeContent = gson.fromJson(resultStr, NoticeContentBean::class.java)
                val result1 = NoticeContentRequest(true, 200)
                result1.noticeContent = noticeContent
                EventBus.getDefault().post(result1)
                return result1
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            val result1 = NoticeContentRequest(false, -1)
            EventBus.getDefault().post(result1)
        }

        val result1 = NoticeContentRequest(false, 0)
        EventBus.getDefault().post(result1)
        return result1
    }
}

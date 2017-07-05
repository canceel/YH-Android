package com.intfocus.yonghuitest.dashboard.kpi.mode

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.intfocus.yonghuitest.bean.UserBean
import com.intfocus.yonghuitest.bean.dashboard.NoticeListBean
import com.intfocus.yonghuitest.bean.dashboard.NoticeListRquest
import com.intfocus.yonghuitest.bean.dashboard.kpi.Message
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiRequest
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiResultData
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * Created by liuruilin on 2017/6/21.
 */
class KpiMode(var ctx: Context): AbstractMode() {
    lateinit var urlString: String
    var result: String? = null
    var messageUrlString = ""
    var mNoticeListSP = ctx.getSharedPreferences("KpiData", Context.MODE_PRIVATE)
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()

    fun getUrl(): String {
        var url = String.format(K.kKPIApiDataPath, K.kBaseUrl,
                mUserSP.getInt(URLs.kGroupId, 0).toString(), mUserSP.getInt(URLs.kRoleId, 0).toString())
        return url
    }

    fun getMessageUrl(): String {
        val url = String.format(K.kMessageDataMobilePath, K.kBaseUrl, UserBean.user_role_id,
                UserBean.user_group_id,
                UserBean.user_id)
        return url
    }


    override fun requestData() {
        Thread(Runnable {
            urlString = getUrl()
            if (!urlString.isEmpty()) {
                val response = HttpUtil.httpGet(urlString, HashMap<String, String>())
                result = response["body"]
                if (StringUtil.isEmpty(result)) {
                    val result1 = KpiRequest(true, 400)
                    EventBus.getDefault().post(result1)
                    return@Runnable
                }
                analysisData(result)
            } else {
                val result1 = KpiRequest(true, 400)
                EventBus.getDefault().post(result1)
                return@Runnable
            }
        }).start()
    }

    /**
     * 解析数据
     * @param result
     */
    private fun analysisData(result: String?): KpiRequest {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.has("code")) {
                val code = jsonObject.getInt("code")
                if (code != 200) {
                    val result1 = KpiRequest(true, code)
                    EventBus.getDefault().post(result1)
                    return result1
                }
            } else {
                val result1 = KpiRequest(true, 404)
                EventBus.getDefault().post(result1)
                return result1
            }

            mNoticeListSP.edit().putString("KpiData", jsonObject.toString()).commit()
            var mKpiData = gson.fromJson(jsonObject.toString().replace("null", "\"\""), KpiResultData::class.java)
            val result1 = KpiRequest(true, 200)
            result1.kpi_data = mKpiData
            EventBus.getDefault().post(result1)
            return result1
        } catch (e: JSONException) {
            e.printStackTrace()
            val result1 = KpiRequest(true, -1)
            EventBus.getDefault().post(result1)
        }

        val result1 = KpiRequest(true, 0)
        EventBus.getDefault().post(result1)
        return result1
    }

    /**

     * 获取公告栏消息
     * @return message
     */
    fun getMessage(): Array<String>? {
        var message: Array<String>
        messageUrlString = getMessageUrl()
        if (messageUrlString.isEmpty()) {
            return null
        }

        val gson = Gson()
        val response = HttpUtil.httpGet(messageUrlString, HashMap<String, String>())
        val returnData = JsonParser().parse(response["body"]).asJsonObject
        val mMessage = gson.fromJson(returnData, Message::class.java)

        val firstMessage = mMessage.data[0].title
        val secondMessage = mMessage.data[1].title

        message = arrayOf(firstMessage, secondMessage)
        return message
    }

}

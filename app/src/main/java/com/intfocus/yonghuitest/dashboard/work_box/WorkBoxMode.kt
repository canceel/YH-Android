package com.intfocus.yonghuitest.dashboard.work_box

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.intfocus.yonghuitest.bean.dashboard.AppListPageRequest
import com.intfocus.yonghuitest.bean.dashboard.ListPageBean
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
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxMode(ctx: Context, var type: String?) : AbstractMode() {
    lateinit var urlString: String
    lateinit var result: String
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()

    /*
     * 获取工具箱列表 Url
     * http://development.shengyiplus.com/api/v1/group/165/role/7/app_covers
     */
    fun getUrl(): String {
        var url = String.format(K.KWorkBoxListPath, K.kBaseUrl,
                mUserSP.getInt(URLs.kGroupId,0).toString(), mUserSP.getInt(URLs.kRoleId,0).toString())
        return url
    }

    override fun requestData() {
        Thread(Runnable {
            urlString = getUrl()
            if (!urlString.isEmpty()) {
                val response = HttpUtil.httpGet(urlString, HashMap<String, String>())
                result = response["body"]!!
                if (StringUtil.isEmpty(result)) {
                    val result1 = WorkBoxRequest(false, 400)
                    EventBus.getDefault().post(result1)
                    return@Runnable
                }
                analysisData(result)
            } else {
                val result1 = WorkBoxRequest(false, 400)
                EventBus.getDefault().post(result1)
                return@Runnable
            }
        }).start()
    }

    /**
     * 解析数据
     * @param result
     */
    private fun analysisData(result: String?): WorkBoxRequest {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.has("code")) {
                val code = jsonObject.getInt("code")
                if (code != 200) {
                    val result1 = WorkBoxRequest(false, code)
                    EventBus.getDefault().post(result1)
                    return result1
                }
            }

            if (jsonObject.has("data")) {
                var resultStr = jsonObject.toString()
                var workBoxData = gson.fromJson(resultStr, WorkBoxBean::class.java)
                val result1 = WorkBoxRequest(true, 200)
                result1.workBoxDatas = workBoxData
                EventBus.getDefault().post(result1)
                return result1
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            val result1 = WorkBoxRequest(false, -1)
            EventBus.getDefault().post(result1)
        }

        val result1 = WorkBoxRequest(false, 0)
        EventBus.getDefault().post(result1)
        return result1
    }
}

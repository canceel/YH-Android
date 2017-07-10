package com.intfocus.yh_android.dashboard.App.mode

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.intfocus.yh_android.bean.dashboard.AppListPageRequest
import com.intfocus.yh_android.bean.dashboard.ListPageBean
import com.intfocus.yh_android.util.HttpUtil
import com.intfocus.yh_android.util.K
import com.intfocus.yh_android.util.URLs.kGroupId
import com.intfocus.yh_android.util.URLs.kRoleId
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * 主页 - 专题 Model
 * Created by liuruilin on 2017/6/15.
 */
class AppListMode(ctx: Context, var type: String?) : AbstractMode() {
    lateinit var urlString: String
    var result: String? = null
    val mListPageSP: SharedPreferences = ctx.getSharedPreferences("ListPage", Context.MODE_PRIVATE)
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()

    fun getUrl(): String {
        var url = String.format(K.KAppListPath, K.kBaseUrl,
                    mUserSP.getInt(kGroupId,0).toString(), mUserSP.getInt(kRoleId,0).toString())
        return url
    }

    override fun requestData() {
        Thread(Runnable {
            urlString = getUrl()
            if (!urlString.isEmpty()) {
                val response = HttpUtil.httpGet(urlString, HashMap<String, String>())
                result = response["body"]
                if (StringUtil.isEmpty(result)) {
                    val result1 = AppListPageRequest(false, 400)
                    EventBus.getDefault().post(result1)
                    return@Runnable
                }
                analysisData(result)
            } else {
                val result1 = AppListPageRequest(false, 400)
                EventBus.getDefault().post(result1)
                return@Runnable
            }
        }).start()
    }

    /**
     * 解析数据
     * @param result
     */
    private fun analysisData(result: String?): AppListPageRequest {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.has("code")) {
                val code = jsonObject.getInt("code")
                if (code != 200) {
                    val result1 = AppListPageRequest(false, code)
                    EventBus.getDefault().post(result1)
                    return result1
                }
            }

            if (jsonObject.has("data")) {
                var resultStr = jsonObject.toString()
                mListPageSP.edit().putString(type, resultStr).commit()
                var listPageData = gson.fromJson(resultStr, ListPageBean::class.java)
                val result1 = AppListPageRequest(true, 200)
                result1.categroy_list = listPageData.data
                EventBus.getDefault().post(result1)
                return result1
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            val result1 = AppListPageRequest(false, -1)
            EventBus.getDefault().post(result1)
        }

        val result1 = AppListPageRequest(false, 0)
        EventBus.getDefault().post(result1)
        return result1
    }
}

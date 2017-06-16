package com.intfocus.yonghuitest.mode

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import com.google.gson.Gson
import com.intfocus.yonghuitest.bean.dashboard.UserInfoBean
import com.intfocus.yonghuitest.bean.dashboard.UserInfoRequest
import com.intfocus.yonghuitest.util.HttpUtil
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by liuruilin on 2017/6/7.
 */
class UserInfoMode(ctx : Context) : AbstractMode() {
    lateinit var urlString: String
    var result : String? = null
    val mSharedPreferences : SharedPreferences = ctx.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var mUserInfoString : String? = null
    var gson = Gson()

    fun getUrl(): String {
        var url = "http://192.168.0.137:3000/api/v1/user/1/mine/user_info"
        return url
    }

    override fun requestData() {
        Thread(Runnable {
            urlString = getUrl()
            if (!urlString.isEmpty()) {
                val response = HttpUtil.httpGet(urlString, HashMap<String, String>())
                result = response["body"]
                if (StringUtil.isEmpty(result)) {
                    val result1 = UserInfoRequest(true, 400)
                    EventBus.getDefault().post(result1)
                    return@Runnable
                }
                analysisData(result)
            } else {
                val result1 = UserInfoRequest(true, 400)
                EventBus.getDefault().post(result1)
                return@Runnable
            }
        }).start()
    }

    /**
     * 解析数据
     * @param result
     */
    private fun analysisData(result: String?): UserInfoRequest {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.has("code")) {
                val code = jsonObject.getInt("code")
                if (code != 200) {
                    val result1 = UserInfoRequest(true, code)
                    EventBus.getDefault().post(result1)
                    return result1
                }
            }

            if (jsonObject.has("data")) {
                var data = jsonObject.getString("data")
                mSharedPreferences.edit().putString("UserInfo", data).commit()
                var userInfo = gson.fromJson(data, UserInfoBean::class.java)
                val result1 = UserInfoRequest(true, 200)
                result1.userInfoBean = userInfo
                EventBus.getDefault().post(result1)
                return result1
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            val result1 = UserInfoRequest(true, -1)
            EventBus.getDefault().post(result1)
        }

        val result1 = UserInfoRequest(true, 0)
        EventBus.getDefault().post(result1)
        return result1
    }

    fun uplodeUserIcon(bitmap: Bitmap) {

    }
}
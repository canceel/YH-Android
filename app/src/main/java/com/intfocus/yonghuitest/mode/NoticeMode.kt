package com.intfocus.yonghuitest.mode

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.intfocus.yonghuitest.dashboard.mine.bean.NoticeListBean
import com.intfocus.yonghuitest.dashboard.mine.bean.NoticeListRquest
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by liuruilin on 2017/6/12.
 */
class NoticeMode(ctx: Context) : AbstractMode() {
    lateinit var urlString: String
    var result: String? = null
    var mNoticeListSP = ctx.getSharedPreferences("NoticeList", Context.MODE_PRIVATE)
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var mNoticeListBean: NoticeListBean? = null
    var page = 1
    var gson = Gson()

    fun getUrl(): String {
        var url = String.format(K.kNoticeListPath, K.kBaseUrl,
                                mUserSP.getString(URLs.kUserNum,""), getNoticeType(), page, 10.toString())
        return url
    }

    fun requestData(page : Int) {
        this.page = page
        requestData()
    }

    override fun requestData() {
        Thread(Runnable {
            urlString = getUrl()
            if (!urlString.isEmpty()) {
                val response = HttpUtil.httpGet(urlString, HashMap<String, String>())
                result = response["body"]
                if (StringUtil.isEmpty(result)) {
                    val result1 = NoticeListRquest(true, 400)
                    result1.noticeListBean = mNoticeListBean
                    EventBus.getDefault().post(result1)
                    return@Runnable
                }
                analysisData(result)
            } else {
                val result1 = NoticeListRquest(true, 400)
                result1.noticeListBean = mNoticeListBean
                EventBus.getDefault().post(result1)
                return@Runnable
            }
        }).start()
    }

    /**
     * 解析数据
     * @param result
     */
    private fun analysisData(result: String?): NoticeListRquest {
        try {
            val jsonObject = JSONObject(result)
            if (jsonObject.has("code")) {
                val code = jsonObject.getInt("code")
                if (code != 200) {
                    val result1 = NoticeListRquest(true, code)
                    result1.noticeListBean = mNoticeListBean
                    EventBus.getDefault().post(result1)
                    return result1
                }
            } else {
                val result1 = NoticeListRquest(true, 404)
                result1.noticeListBean = mNoticeListBean
                EventBus.getDefault().post(result1)
                return result1
            }

            mNoticeListSP.edit().putString("NoticeList", jsonObject.toString()).commit()
            var mNoticeList = gson.fromJson(jsonObject.toString(), NoticeListBean::class.java)
            val result1 = NoticeListRquest(true, 200)
            result1.noticeListBean = mNoticeList
            EventBus.getDefault().post(result1)
            return result1
        } catch (e: JSONException) {
            e.printStackTrace()
            val result1 = NoticeListRquest(true, -1)
            result1.noticeListBean = mNoticeListBean
            EventBus.getDefault().post(result1)
        }

        val result1 = NoticeListRquest(true, 0)
        result1.noticeListBean = mNoticeListBean
        EventBus.getDefault().post(result1)
        return result1
    }

    fun getNoticeType() : String {
        var typeArray = arrayOf("SystemNotice","WorkNotice","WarningSystem","ReportComment")
        var typeStr = ""
        var i = 0
        for (str in typeArray) {
            if (mNoticeListSP.getBoolean(str, true)) {
                if (typeStr.equals("")) {
                    typeStr += i.toString()
                }
                else {
                    typeStr += "," + i.toString()
                }
            }
            i++
        }
        return typeStr
    }
}

package com.intfocus.yonghuitest.scanner

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.Gson
import com.intfocus.yonghuitest.bean.dashboard.ListPageBean
import com.intfocus.yonghuitest.bean.dashboard.ReportListPageRequest
import com.intfocus.yonghuitest.constant.Urls
import com.intfocus.yonghuitest.util.*
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import org.xutils.common.Callback
import java.io.File
import java.io.IOException
import java.util.HashMap
import org.xutils.http.RequestParams
import org.xutils.common.Callback.CancelledException
import org.xutils.common.Callback.ProgressCallback
import org.xutils.x
import org.xutils.common.task.PriorityExecutor
import org.xutils.common.Callback.CommonCallback


/**
 * 扫码结果页 model
 * @author Liurl21
 * create at 2017/7/6 下午3:18
 */
class ScannerMode(var ctx: Context) : AbstractMode() {
    var result: String? = null
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()
    var jsUrl = ""
    var htmlUrl = ""
    var store_id = ""
    var barcode = ""

    fun requestData(barcode: String) {
        store_id = getStoreID()
        this.barcode = barcode
        jsUrl = String.format(K.kBarCodeScanAPIDataPath, K.kBaseUrl, store_id, barcode)
        htmlUrl = String.format(K.kBarCodeScanAPIViewPath, K.kBaseUrl, store_id, barcode)
        requestData()
    }

    override fun requestData() {
        if (!jsUrl.isEmpty()) {
            val params = RequestParams(jsUrl)
            var jsFileName = String.format("store_%s_barcode_%s.js", store_id, barcode)
            val jsPath = String.format("%s/assets/javascripts/%s", FileUtil.sharedPath(ctx), jsFileName)
            params.isAutoRename = false //设置是否根据头信息自动命名文件
            params.saveFilePath = jsPath
            params.executor = PriorityExecutor(2, true)
            x.http().get(params, object : Callback.CommonCallback<File> {
                override fun onCancelled(p0: CancelledException?) {}

                override fun onError(p0: Throwable?, p1: Boolean) {
                    val result1 = ScannerRequest(false, 400)
                    result1.errorInfo = p0.toString()
                    EventBus.getDefault().post(result1)
                }

                override fun onFinished() {
                }

                override fun onSuccess(p0: File?) {
                    getHtml()
                }
            })
        }
    }

    private fun getHtml() {
        Thread(Runnable {
            var htmlName = String.format("mobile_v2_store_%s_barcode_%s.html", store_id, barcode)
            var htmlPath = String.format("%s/%s", FileUtil.dirPath(ctx, K.kHTMLDirName), htmlName)

            var response = HttpUtil.httpGet(htmlUrl, HashMap<String, String>())

            if (response["code"].equals("200")) {
                var htmlContent = response["body"]
                htmlContent = htmlContent!!.replace("/javascripts/", String.format("%s/javascripts/", "../../Shared/assets"))
                htmlContent = htmlContent.replace("/stylesheets/", String.format("%s/stylesheets/", "../../Shared/assets"))
                htmlContent = htmlContent.replace("/images/", String.format("%s/images/", "../../Shared/assets"))
                FileUtil.writeFile(htmlPath, htmlContent)
                val result1 = ScannerRequest(true, 200)
                result1.htmlPath = htmlPath
                EventBus.getDefault().post(result1)
            } else {
                val result1 = ScannerRequest(false, 400)
                result1.errorInfo = response["code"] + response["body"]
                EventBus.getDefault().post(result1)
            }
        }).start()
    }

    @Throws(JSONException::class, IOException::class)
    private fun getStoreID(): String {
        val userConfigPath = String.format("%s/%s", FileUtil.basePath(ctx), K.kUserConfigFileName)
        var user = FileUtil.readConfigFile(userConfigPath)
        if (!user.has(URLs.kStoreIds)) {
            return "0"
        }

        var cachedPath = FileUtil.dirPath(ctx, K.kCachedDirName, K.kBarCodeResultFileName)
        var cachedJSON: JSONObject
        cachedJSON = FileUtil.readConfigFile(cachedPath)
        var flag = false
        var storeName: String
        if (cachedJSON.has(URLs.kStore) && cachedJSON.getJSONObject(URLs.kStore).has("id") &&
                user.has(URLs.kStoreIds)) {
            storeName = cachedJSON.getJSONObject(URLs.kStore).getString("name")
            for (i in 0..user.getJSONArray(URLs.kStoreIds).length() - 1) {
                if (user.getJSONArray(URLs.kStoreIds).getJSONObject(i).getString("name") == storeName) {
                    flag = true
                }
            }
        }

        if (!flag) {
            cachedJSON.put(URLs.kStore, user.getJSONArray(URLs.kStoreIds).get(0))
            FileUtil.writeFile(cachedPath, cachedJSON.toString())
        }

        return cachedJSON.getJSONObject(URLs.kStore).getString("id")
    }
}

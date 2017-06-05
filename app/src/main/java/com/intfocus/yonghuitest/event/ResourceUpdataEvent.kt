package com.intfocus.yonghuitest.event

import android.content.Context
import android.os.AsyncTask
import com.intfocus.yonghuitest.bean.dashboard.ResourceUpdataResult
import com.intfocus.yonghuitest.util.*
import com.intfocus.yonghuitest.util.HttpUtil.checkAssetUpdated
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import java.io.File
import org.xutils.common.Callback.CancelledException
import com.intfocus.yonghuitest.dashboard.kpi.ui.MainActivity
import android.app.ProgressDialog.STYLE_HORIZONTAL
import org.xutils.common.Callback.ProgressCallback
import org.xutils.x
import android.R.attr.path
import android.util.Log
import org.xutils.common.Callback
import org.xutils.http.RequestParams



/**
 * Created by liuruilin on 2017/6/5.
 */
class ResourceUpdataEvent {
    companion object {
        val resourceFileNames = arrayOf(URLs.kAssets, URLs.kLoading, URLs.kBarCodeScan)
        val assetsFileNames = arrayOf(URLs.kFonts, URLs.kImages, URLs.kIcons, URLs.kStylesheets,
                URLs.kJavaScripts)

        fun checkResourceFileUpdated(context: Context){
            var isUpdata = false
            for (resourceFileName in resourceFileNames) {
                isUpdata = checkAssetUpdated(context, resourceFileName, false)
                if (!isUpdata)
                    continue
            }

            for (assetsFileName in assetsFileNames) {
                isUpdata = checkAssetUpdated(context, assetsFileName, true)
                if (!isUpdata)
                    continue
            }

            EventBus.getDefault().post(ResourceUpdataResult(isUpdata))
        }

        fun checkAssetUpdated(context: Context, assetName: String, isInAssets: Boolean): Boolean {
            try {
                var isShouldUpdateAssets = false
                val sharedPath = FileUtil.sharedPath(context)
                val assetZipPath = String.format("%s/%s.zip", sharedPath, assetName)
                isShouldUpdateAssets = !File(assetZipPath).exists()

                val userConfigPath = String.format("%s/%s", FileUtil.basePath(context), K.kUserConfigFileName)
                val userJSON = FileUtil.readConfigFile(userConfigPath)
                val localKeyName = String.format("local_%s_md5", assetName)
                val keyName = String.format("%s_md5", assetName)
                isShouldUpdateAssets = !isShouldUpdateAssets && userJSON.getString(localKeyName) != userJSON.getString(keyName)
                if (!isShouldUpdateAssets) {
                    return false
                }

                var downloadResourceUrl = String.format(K.kDownloadAssetsAPIPath, K.kBaseUrl, assetName)

                if (downloadFile(downloadResourceUrl, assetZipPath)) {

                }

                return true
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return false
        }

        fun downloadFile(url: String, path: String): Boolean {
            var isSuccess = false
            val requestParams = RequestParams(url)
            requestParams.saveFilePath = path
            x.http().get(requestParams, object : Callback.ProgressCallback<File> {
                override fun onWaiting() {}

                override fun onStarted() {}

                override fun onCancelled(cex: CancelledException?) {
                }

                override fun onLoading(total: Long, current: Long, isDownloading: Boolean) {
                }

                override fun onSuccess(result: File) {
                    Log.i("testlog", "is Success")
                    isSuccess = true
                }

                override fun onError(ex: Throwable, isOnCallback: Boolean) {
                    ex.printStackTrace()
                }
                override fun onFinished() {}
            })
            return isSuccess
        }

        fun unZip(path: String) {

        }
    }
}
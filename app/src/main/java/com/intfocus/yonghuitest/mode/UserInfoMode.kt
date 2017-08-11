package com.intfocus.yonghuitest.mode

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import com.google.gson.Gson
import com.intfocus.yonghuitest.dashboard.mine.bean.UserInfoBean
import com.intfocus.yonghuitest.dashboard.mine.bean.UserInfoRequest
import com.intfocus.yonghuitest.data.response.BaseResult
import com.intfocus.yonghuitest.data.response.mine_page.UserInfoResult
import com.intfocus.yonghuitest.net.ApiException
import com.intfocus.yonghuitest.net.CodeHandledSubscriber
import com.intfocus.yonghuitest.net.RetrofitUtil
import com.intfocus.yonghuitest.util.*
import com.intfocus.yonghuitest.util.K.*
import com.zbl.lib.baseframe.core.AbstractMode
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liuruilin on 2017/6/7.
 */
class UserInfoMode(var ctx: Context) : AbstractMode() {
    lateinit var urlString: String
    var result: String? = null
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()

    override fun requestData() {
        RetrofitUtil.getHttpService().getUserInfo(mUserSP.getString(URLs.kUserNum, ""))
                .compose(RetrofitUtil.CommonOptions<UserInfoResult>())
                .subscribe(object : CodeHandledSubscriber<UserInfoResult>(){
                    override fun onBusinessNext(data: UserInfoResult?) {
                        val result1 = UserInfoRequest(true, 200)
                        result1.userInfoBean = data
                        EventBus.getDefault().post(result1)
                    }

                    override fun onError(apiException: ApiException?) {
                    }

                    override fun onCompleted() {
                    }
                })
    }

    fun uplodeUserIcon(bitmap: Bitmap, imgPath: String) {
        Thread(Runnable {
            var format = SimpleDateFormat("yyyyMMddHHmmss")
            var date = Date(System.currentTimeMillis())
            File(imgPath).delete()
            var gravatarImgPath = FileUtil.dirPath(ctx, K.kConfigDirName, K.kAppCode + "_" + mUserSP.getString(URLs.kUserNum, "") + "_" + format.format(date) + ".jpg")
            FileUtil.saveImage(gravatarImgPath, bitmap)
            var requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), File(gravatarImgPath))
            var multiPartBody = MultipartBody.Part.createFormData("gravatar", mUserSP.getInt(kUserId, 0).toString() + "icon", requestBody)
            RetrofitUtil.getHttpService().userIconUpload(mUserSP.getInt(kUserDeviceId, 0), mUserSP.getInt(kUserId, 0), multiPartBody)
                    .compose(RetrofitUtil.CommonOptions<BaseResult>())
                    .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                        override fun onBusinessNext(data: BaseResult?) {
                            ToastUtils.show(ctx, "头像已上传")
                        }

                        override fun onError(apiException: ApiException?) {
                        }

                        override fun onCompleted() {
                        }
                    })
        }).start()
    }

    fun modifiedUserConfig(isLogin: Boolean) {
        try {
            val configJSON = JSONObject()
            configJSON.put("is_login", isLogin)
            val userConfigPath = String.format("%s/%s", FileUtil.basePath(ctx), K.kUserConfigFileName)
            var userJSON = FileUtil.readConfigFile(userConfigPath)

            userJSON = ApiHelper.mergeJson(userJSON, configJSON)
            FileUtil.writeFile(userConfigPath, userJSON.toString())

            val settingsConfigPath = FileUtil.dirPath(ctx, K.kConfigDirName, K.kSettingConfigFileName)
            FileUtil.writeFile(settingsConfigPath, userJSON.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}

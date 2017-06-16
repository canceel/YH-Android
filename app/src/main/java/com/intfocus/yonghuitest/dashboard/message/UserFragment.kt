package com.intfocus.yonghuitest.dashboard.message

import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.intfocus.yonghuitest.LoginActivity
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.bean.dashboard.UserInfoBean
import com.intfocus.yonghuitest.bean.dashboard.UserInfoRequest
import com.intfocus.yonghuitest.mode.UserInfoMode
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.ImageUtil.*
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.K.kUserDeviceId
import com.intfocus.yonghuitest.util.WidgetUtil
import com.taobao.accs.utl.UtilityImpl.isNetworkConnected
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.items_single_value.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import java.io.File
import java.util.*

/**
 * Created by liuruilin on 2017/6/7.
 */
class UserFragment : BaseModeFragment<UserInfoMode>() {
    lateinit var ctx : Context
    lateinit var mUserInfoSP : SharedPreferences
    var mUserInfo : UserInfoBean? = null
    var mUserInfoString : String? = null
    var rootView : View? = null
    var gson = Gson()

    /* 请求识别码 */
    private val CODE_GALLERY_REQUEST = 0xa0
    private val CODE_CAMERA_REQUEST = 0xa1
    private val CODE_RESULT_REQUEST = 0xa2

    override fun setSubject(): Subject {
        ctx = act.applicationContext
        mUserInfoSP = ctx.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        return UserInfoMode(ctx)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_user, container, false)
            model.requestData()
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        super.onActivityCreated(savedInstanceState)
    }

    fun initView() {
        mUserInfoString = mUserInfoSP.getString("UserInfoBean", "")
        if (!mUserInfoString.equals("")) {
            mUserInfo = gson.fromJson(mUserInfoString, UserInfoBean::class.java)
            tv_user_name.text = mUserInfo!!.user_name
            tv_location.text = "最近一次: " + mUserInfo!!.location + mUserInfo!!.time
            tv_login_number.text = mUserInfo!!.days
            tv_report_number.text = mUserInfo!!.readed_num
            tv_beyond_number.text = mUserInfo!!.percent
            tv_user_role_value.text = mUserInfo!!.role
            tv_user_group_value.text = mUserInfo!!.group
        }
        iv_user_icon.setOnClickListener { showPhotoSelectDialog(this.context) }
        rl_password_alter.setOnClickListener { startPassWordAlterActivity() }
        rl_issue.setOnClickListener { startIssueActivity() }
        rl_favorite.setOnClickListener { startFavoriteActivity() }
        rl_logout.setOnClickListener { logout() }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result : UserInfoRequest) {
        if (result.isSuccess && result.userInfoBean != null) {
            var user = result.userInfoBean
            tv_user_name.text = user!!.user_name
            tv_location.text = "最近一次: " + user.location + user!!.time
            tv_login_number.text = user.days
            tv_report_number.text = user.readed_num
            tv_beyond_number.text = user.percent
            tv_user_role_value.text = user.role
            tv_user_group_value.text = user.group
        }
    }

    fun startPassWordAlterActivity() {
        var intent =  Intent(activity, PassWordAlterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    fun startFavoriteActivity() {
        var intent =  Intent(activity, FavoriteActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    fun startIssueActivity() {
        var intent =  Intent(activity, IssueActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    fun logout() {
        // 判断有无网络
        if (!isNetworkConnected(ctx)) {
            WidgetUtil.showToastShort(ctx, "未连接网络, 无法退出")
            return
        }
        val mEditor = act.getSharedPreferences("SettingPreference", MODE_PRIVATE).edit()
        mEditor.putBoolean("ScreenLock", false)
        mEditor.commit()
        Thread(Runnable {
            try {
                val postUrl = String.format(K.kDeleteDeviceIdAPIPath,
                        K.kBaseUrl,
                        activity.getSharedPreferences("UserBean", MODE_PRIVATE).getInt(kUserDeviceId, 0).toString())
                val response = HttpUtil.httpPost(postUrl, HashMap<String, String>())
                activity.runOnUiThread(Runnable {
                    if (response["code"] == "200") {
//                        modifiedUserConfig(false)
                        val intent = Intent()
                        intent.setClass(activity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        WidgetUtil.showToastShort(ctx, response.toString())
                    }
                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 用户没有选择图片，返回
        if (resultCode == RESULT_CANCELED) {
            WidgetUtil.showToastShort(ctx, "取消")
            return
        }

        when (requestCode) {
            CODE_GALLERY_REQUEST -> {
                var cropIntent = launchSystemImageCrop(ctx, data!!.data)
                startActivityForResult(cropIntent, CODE_RESULT_REQUEST)
            }
            CODE_CAMERA_REQUEST -> {
                var cropIntent : Intent
                val tempFile = File(Environment.getExternalStorageDirectory(), "icon.jpg")
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val photoURI = FileProvider.getUriForFile(ctx,
                            "com.intfocus.yonghuitest.fileprovider",
                            tempFile)
                    cropIntent = launchSystemImageCrop(ctx, photoURI)
                } else {
                    cropIntent = launchSystemImageCrop(ctx, Uri.fromFile(tempFile))
                }
                startActivityForResult(cropIntent, CODE_RESULT_REQUEST)
            }
            else -> if (data != null) {
                setImageToHeadView(data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showPhotoSelectDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        val options = arrayOf("拍照", "从相册选择")
        alertDialog.setItems(options) { _, which ->
            if (which == 0) {
                startActivityForResult(launchCamera(context), CODE_CAMERA_REQUEST)
            } else {
                startActivityForResult(getGallery(), CODE_GALLERY_REQUEST)
            }
        }
        alertDialog.show()
    }

    /*
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private fun setImageToHeadView(intent: Intent) {
        val bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().toString() + "/icon.jpg")
        if (bitmap != null) {
            iv_user_icon.setImageBitmap(bitmap)
        }
    }
}
package com.intfocus.yonghuitest.dashboard.mine

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.google.gson.Gson
import com.intfocus.yonghuitest.login.LoginActivity
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.dashboard.mine.bean.UserInfoBean
import com.intfocus.yonghuitest.dashboard.mine.bean.UserInfoRequest
import com.intfocus.yonghuitest.mode.UserInfoMode
import com.intfocus.yonghuitest.setting.SettingActivity
import com.intfocus.yonghuitest.util.*
import com.intfocus.yonghuitest.util.ImageUtil.*
import com.intfocus.yonghuitest.util.K.kUserDeviceId
import com.taobao.accs.utl.UtilityImpl.isNetworkConnected
import com.zbl.lib.baseframe.core.Subject
import com.zbl.lib.baseframe.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.item_mine_user_top.*
import kotlinx.android.synthetic.main.items_single_value.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import org.xutils.image.ImageOptions
import org.xutils.x
import java.io.File
import java.util.*

/**
 * Created by liuruilin on 2017/6/7.
 */
class UserFragment : BaseModeFragment<UserInfoMode>() {
    lateinit var mUserInfoSP: SharedPreferences
    lateinit var mUserSP: SharedPreferences
    var mUserInfo: UserInfoBean? = null
    var mUserInfoString: String? = null
    var rootView: View? = null
    var gson = Gson()
    var imageOptions: ImageOptions? = null
    var localImageOptions: ImageOptions? = null

    /* 请求识别码 */
    private val CODE_GALLERY_REQUEST = 0xa0
    private val CODE_CAMERA_REQUEST = 0xa1
    private val CODE_RESULT_REQUEST = 0xa2

    override fun setSubject(): Subject {
        mUserInfoSP = ctx.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
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
        imageOptions = ImageOptions.Builder()
                .setSize(DisplayUtil.dip2px(ctx, 60f), DisplayUtil.dip2px(ctx, 60f))
                .setCircular(true)
                .setLoadingDrawableId(R.drawable.face_default)
                .setFailureDrawableId(R.drawable.face_default)
                .build()

        localImageOptions = ImageOptions.Builder()
                .setSize(DisplayUtil.dip2px(ctx, 60f), DisplayUtil.dip2px(ctx, 60f))
                .setCircular(true)
                .build()

        var mTypeFace = Typeface.createFromAsset(act.assets, "ALTGOT2N.TTF")
        tv_login_number.typeface = mTypeFace
        tv_report_number.typeface = mTypeFace
        tv_beyond_number.typeface = mTypeFace
        initView()
        super.onActivityCreated(savedInstanceState)
    }

    fun initView() {
        mUserInfoString = mUserInfoSP.getString("UserInfoBean", "")
        if (!mUserInfoString.equals("")) {
            mUserInfo = gson.fromJson(mUserInfoString, UserInfoBean::class.java)
            tv_user_name.text = mUserInfo!!.user_name
            tv_login_number.text = mUserInfo!!.login_duration
            tv_report_number.text = mUserInfo!!.browse_report_count
            tv_beyond_number.text = mUserInfo!!.surpass_percentage.toString()
            tv_user_role.text = mUserInfo!!.role_name
            tv_mine_user_num_value.text = mUserInfo!!.user_num
            tv_mine_user_group_value.text = mUserInfo!!.group_name
            x.image().bind(iv_user_icon, mUserInfo!!.gravatar, imageOptions)
        }
        iv_user_icon.setOnClickListener { showIconSelectPopWindow(this.context) }
        rl_password_alter.setOnClickListener { startPassWordAlterActivity() }
        rl_issue.setOnClickListener { startIssueActivity() }
        rl_setting.setOnClickListener { startSettingActivity() }
        rl_favorite.setOnClickListener { startFavoriteActivity() }
        rl_logout.setOnClickListener { showLogoutPopupWindow(this.context) }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: UserInfoRequest) {
        if (result.isSuccess && result.userInfoBean != null) {
            var user = result.userInfoBean
            tv_user_name.text = user!!.user_name
            tv_login_number.text = user.login_duration
            tv_report_number.text = user.browse_report_count
            tv_mine_user_num_value.text = user.user_num
            tv_beyond_number.text = user.surpass_percentage.toString()
            tv_user_role.text = user.role_name
            tv_mine_user_group_value.text = user.group_name
            x.image().bind(iv_user_icon, user.gravatar, imageOptions)
        }
    }

    fun startPassWordAlterActivity() {
        var intent = Intent(activity, PassWordAlterActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)

        var logParams = JSONObject()
        logParams.put(URLs.kAction, "我的/修改密码")
        ApiHelper.actionNewThreadLog(activity, logParams)
    }

    fun startFavoriteActivity() {
        ToastUtil.showToast(ctx, "文章收藏页待实现")
    }

    fun startIssueActivity() {
        var intent = Intent(activity, IssueActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)

        var logParams = JSONObject()
        logParams.put(URLs.kAction, "我的/问题反馈")
        ApiHelper.actionNewThreadLog(activity, logParams)
    }

    fun startSettingActivity() {
        var intent = Intent(activity, SettingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    /**
     * 退出登录选择窗
     */
    internal fun showLogoutPopupWindow(ctx: Context) {
        val contentView = LayoutInflater.from(ctx).inflate(R.layout.popup_logout, null)
        //设置弹出框的宽度和高度
        var popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        popupWindow.showAtLocation(activity.toolBar, Gravity.BOTTOM, 0, 0)

        contentView.findViewById(R.id.rl_logout_confirm).setOnClickListener {
            // 确认退出
            logout()
        }
        contentView.findViewById(R.id.rl_cancel).setOnClickListener {
            // 取消
            popupWindow.dismiss()
        }
        contentView.findViewById(R.id.rl_popup_logout_background).setOnClickListener {
            // 点击背景半透明区域
            popupWindow.dismiss()
        }
    }
    
    /**
     * 退出登录
     */
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
                activity.runOnUiThread({
                    if (response["code"] == "200") {
                        model.modifiedUserConfig(false)
                        val intent = Intent()
                        intent.setClass(activity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    } else {
                        WidgetUtil.showToastShort(ctx, response.toString())
                    }
                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }).start()

        var logParams = JSONObject()
        logParams.put(URLs.kAction, "退出登录")
        ApiHelper.actionNewThreadLog(activity, logParams)
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
                var cropIntent: Intent
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
                setImageToHeadView()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 显示头像选择菜单
     */
    internal fun showIconSelectPopWindow(ctx: Context) {
        val contentView = LayoutInflater.from(ctx).inflate(R.layout.popup_mine_icon_select, null)
        //设置弹出框的宽度和高度
        var popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        popupWindow.showAtLocation(activity.toolBar, Gravity.BOTTOM, 0, 0)

        contentView.findViewById(R.id.rl_camera).setOnClickListener {
            // 打开相机
            startActivityForResult(launchCamera(context), CODE_CAMERA_REQUEST)
        }
        contentView.findViewById(R.id.rl_gallery).setOnClickListener {
            // 打开相册
            startActivityForResult(getGallery(), CODE_GALLERY_REQUEST)
        }
        contentView.findViewById(R.id.rl_cancel).setOnClickListener {
            // 取消
            popupWindow.dismiss()
        }
        contentView.findViewById(R.id.rl_popup_icon_background).setOnClickListener {
            // 点击背景半透明区域
            popupWindow.dismiss()
        }

        var logParams = JSONObject()
        logParams.put(URLs.kAction, "我的/设置头像")
        ApiHelper.actionNewThreadLog(activity, logParams)
    }

    /*
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private fun setImageToHeadView() {
        var imgPath = Environment.getExternalStorageDirectory().toString() + "/icon.jpg"
        val bitmap = BitmapFactory.decodeFile(imgPath)
        if (bitmap != null) {
            iv_user_icon.setImageBitmap(DisplayUtil.makeRoundCorner(bitmap))
            model.uplodeUserIcon(bitmap, imgPath)
        }
    }
}

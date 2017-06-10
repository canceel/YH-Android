package com.intfocus.yonghuitest.dashboard.message

import android.app.Activity
import android.app.Activity.RESULT_CANCELED
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.bean.dashboard.UserInfoRequest
import com.intfocus.yonghuitest.mode.UserInfoMode
import com.intfocus.yonghuitest.util.ImageUtil.*
import com.intfocus.yonghuitest.util.WidgetUtil
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.items_single_value.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.xutils.x
import java.io.File

/**
 * Created by liuruilin on 2017/6/7.
 */
class UserFragment : BaseModeFragment<UserInfoMode>() {
    lateinit var ctx : Context
    var rootView : View? = null

    /* 请求识别码 */
    private val CODE_GALLERY_REQUEST = 0xa0
    private val CODE_CAMERA_REQUEST = 0xa1
    private val CODE_RESULT_REQUEST = 0xa2

    override fun setSubject(): Subject {
        ctx = act.applicationContext
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
        x.view().inject(rootView)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun initView(result : UserInfoRequest) {
        if (result.userInfoBean != null) {
            var user = result.userInfoBean
            tv_user_name.text = user!!.user_name
            tv_location.text = "最近一次: " + user!!.location + user!!.time
            tv_login_number.text = user!!.days
            tv_report_number.text = user!!.readed_num
            tv_beyond_number.text = user!!.percent
            tv_user_role_value.text = user!!.role
            tv_user_group_value.text = user!!.group
            iv_user_icon.setOnClickListener { showPhotoSelectDialog(this.context) }
            rl_password_alter.setOnClickListener { startPassWordAlterActivity() }
            rl_issue.setOnClickListener { startIssueActivity() }
            rl_favorite.setOnClickListener { startFavoriteActivity() }
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
//                setImageToHeadView(data)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun showPhotoSelectDialog(context: Context) {
        val alertDialog = AlertDialog.Builder(context)
        val options = arrayOf("拍照", "从相册选择")
        alertDialog.setItems(options) { dialog, which ->
            if (which == 0) {
                startActivityForResult(launchCamera(context), CODE_GALLERY_REQUEST)
            } else {
                startActivityForResult(getGallery(), CODE_GALLERY_REQUEST)
            }
        }
        alertDialog.show()
    }

}
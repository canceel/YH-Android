package com.intfocus.yh_android.dashboard.mine

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.gson.Gson
import com.intfocus.yh_android.R
import com.intfocus.yh_android.dashboard.mine.adapter.IssueListAdapter
import com.intfocus.yh_android.dashboard.mine.bean.IssueCommitInfo
import com.intfocus.yh_android.dashboard.mine.bean.IssueListBean
import com.intfocus.yh_android.mode.IssueMode
import com.intfocus.yh_android.util.*
import com.zbl.lib.baseframe.core.AbstractActivity
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.activity_issue.*


class IssueActivity : AbstractActivity<IssueMode>(), IssueListAdapter.IssueItemListener {
    private val CODE_GALLERY_REQUEST = 0xa0
    val ctx = this
    lateinit var issueListDialog: Dialog
    lateinit var mIssueListSP: SharedPreferences
    lateinit var mUserSP : SharedPreferences
    var gson = Gson()
    var issueInfo = IssueCommitInfo()
    var imgCount = 0
    var imgSum = 0

    override fun setSubject(): Subject {
        return IssueMode(ctx)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_issue)
        mIssueListSP = getSharedPreferences("IssueList", MODE_PRIVATE)
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        onCreateFinish(savedInstanceState)
    }

    override fun setLayoutRes(): Int {
        TODO("重写 BaseActivity 后, 需重写相关联 Activity 的 setLayoutRes")
    }

    override fun onCreateFinish(p0: Bundle?) {
        supportActionBar!!.hide()
        rl_issue_commit.setOnClickListener { commitIssue() }
        iv_add_img.setOnClickListener {
            if (imgSum == 3) {
                WidgetUtil.showToastLong(ctx, "只能上传 3 张图片喔")
                return@setOnClickListener
            }
            startActivityForResult(ImageUtil.getGallery(), CODE_GALLERY_REQUEST)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_GALLERY_REQUEST && resultCode == Activity.RESULT_OK && null != data) {
            val selectedImage = data.data
            val filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)

            val cursor = contentResolver.query(selectedImage,
                    filePathColumn, null, null, null)
            cursor!!.moveToFirst()

            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            cursor.close()
            changerIssueImgLayout()
            val imageBmp = BitmapFactory.decodeFile(picturePath)
            when(imgCount) {
                0 -> {
                    iv_img_1.setImageBitmap(imageBmp)
                    imgCount++
                    imgSum++
                }
                1 -> {
                    iv_img_2.setImageBitmap(imageBmp)
                    imgCount++
                    imgSum++
                }
                2 -> {
                    iv_img_3.setImageBitmap(imageBmp)
                    imgSum++
                }
            }
            iv_img_1.setImageBitmap(imageBmp)
            model.addUploadImg(imageBmp)
        }
    }

    fun changerIssueImgLayout() {
        rl_issue_img.layoutParams.height = DisplayUtil.dip2px(ctx, 120f)
        rl_issue_img.layoutParams.width = DisplayUtil.dip2px(ctx, 303f)
        tv_issue_img.visibility = View.GONE
    }

    fun commitIssue() {
        issueInfo.issue_content = et_issue_content.text.toString()
        issueInfo.app_version = mUserSP.getString(K.kAppVersion, "2.0+")
        issueInfo.platform = mUserSP.getString("device_info", "android")
        issueInfo.platform_version = mUserSP.getString("os_version", "0")
        issueInfo.user_num = mUserSP.getString(URLs.kUserNum, "null")
        model.commitIssue2(issueInfo)
    }

    /**
     * 问题反馈列表页
     */
    internal fun showIssueDialog(issueList: IssueListBean?) {
        issueListDialog = AlertDialog.Builder(ctx, R.style.CommonDialog).setTitle("生意人反馈收集").create()
        issueListDialog.show()
        val issueDialogView = LayoutInflater.from(ctx).inflate(R.layout.dialog_issue_list, null)
        val llIssueAdd = issueDialogView.findViewById(R.id.ll_issue_add) as LinearLayout
        val rcIssueList = issueDialogView.findViewById(R.id.rc_issue_list) as RecyclerView
        val mLayoutManager = LinearLayoutManager(ctx)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rcIssueList.layoutManager = mLayoutManager
        rcIssueList.adapter = IssueListAdapter(ctx, issueList!!.data, this)
        llIssueAdd.setOnClickListener { issueListDialog.dismiss() }
        issueListDialog.setContentView(issueDialogView)
    }

    fun dismissActivity(v: View) {
        this.onBackPressed()
    }
    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun itemClick(position: Int) {
        WidgetUtil.showToastLong(ctx, "进入问题反馈详情")
    }
}

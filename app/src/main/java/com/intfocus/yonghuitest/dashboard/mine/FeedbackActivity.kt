package com.intfocus.yonghuitest.dashboard.mine

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.dashboard.mine.adapter.FeedbackPageScreenshotAdapter
import com.intfocus.yonghuitest.util.LogUtil
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import kotlinx.android.synthetic.main.activity_feedback.*
import com.intfocus.yonghuitest.dashboard.mine.bean.IssueCommitInfo
import com.intfocus.yonghuitest.mode.IssueMode
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs
import com.zbl.lib.baseframe.core.AbstractActivity
import kotlinx.android.synthetic.main.activity_issue.*


class FeedbackActivity : AppCompatActivity(), View.OnClickListener, FeedbackPageScreenshotAdapter.ScreenshotItemClickListener {
    companion object {
        val REQUEST_CODE_CHOOSE = 1
    }

    var mSelected: List<Uri>? = null
    val screenshotAdapter: FeedbackPageScreenshotAdapter = FeedbackPageScreenshotAdapter(this, this)

    lateinit var issueListDialog: Dialog
    lateinit var mIssueListSP: SharedPreferences
    lateinit var mUserSP : SharedPreferences
    lateinit var mProgressDialog: ProgressDialog
    var issueInfo = IssueCommitInfo()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        initData()
        initView()
        initAdapter()
        initListener()
    }


    private fun initData() {
        mIssueListSP = getSharedPreferences("IssueList", AbstractActivity.MODE_PRIVATE)
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)

    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_feedback_page_screenshot.layoutManager = linearLayoutManager
    }

    private fun initAdapter() {
        rv_feedback_page_screenshot.adapter = screenshotAdapter
    }

    private fun initListener() {
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_feedback_banner_back -> {
                finish()
            }
            R.id.btn_feedback_submit -> {

            }
        }
    }
            R.id.btn_feedback_submit ->{
                commitIssue()
            }
        }
    }

    fun commitIssue() {
        mProgressDialog = ProgressDialog.show(this, "稍等", "正在提交...")
        issueInfo.issue_content = et_issue_content.text.toString()
        issueInfo.app_version = mUserSP.getString(K.kAppVersion, "2.0+")
        issueInfo.platform = mUserSP.getString("device_info", "android")
        issueInfo.platform_version = mUserSP.getString("os_version", "0")
        issueInfo.user_num = mUserSP.getString(URLs.kUserNum, "null")
        IssueMode(this).commitIssue2(issueInfo)
    }
}

    override fun addScreenshot(maxNum: Int) {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(maxNum)
                .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE)

    }

    override fun delScreenshot(pos: Int) {
        screenshotAdapter.delScreenWithPos(pos)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data)
            screenshotAdapter.setData(mSelected)
            LogUtil.d("Matisse", "mSelected: " + mSelected)
        }
    }
}


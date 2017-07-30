package com.intfocus.yonghuitest.dashboard.mine

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.dashboard.mine.bean.IssueCommitInfo
import com.intfocus.yonghuitest.mode.IssueMode
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs
import com.zbl.lib.baseframe.core.AbstractActivity
import kotlinx.android.synthetic.main.activity_issue.*

class FeedbackActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var issueListDialog: Dialog
    lateinit var mIssueListSP: SharedPreferences
    lateinit var mUserSP : SharedPreferences
    lateinit var mProgressDialog: ProgressDialog
    var issueInfo = IssueCommitInfo()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        initData()
    }

    private fun initData() {
        mIssueListSP = getSharedPreferences("IssueList", AbstractActivity.MODE_PRIVATE)
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_feedback_banner_back -> {
                finish()
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

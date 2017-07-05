package com.intfocus.yonghuitest.dashboard.mine

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.dashboard.mine.adapter.IssueListAdapter
import com.intfocus.yonghuitest.bean.dashboard.IssueCommitInfo
import com.intfocus.yonghuitest.bean.dashboard.IssueListBean
import com.intfocus.yonghuitest.mode.IssueMode
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs
import com.intfocus.yonghuitest.util.WidgetUtil
import com.zbl.lib.baseframe.core.AbstractActivity
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.activity_issue.*

class IssueActivity : AbstractActivity<IssueMode>(), IssueListAdapter.IssueItemListener {
    val ctx = this
    lateinit var issueListDialog: Dialog
    lateinit var mIssueListSP: SharedPreferences
    lateinit var mUserSP : SharedPreferences
    var gson = Gson()
    var issueInfo = IssueCommitInfo()

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
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun commitIssue() {
        issueInfo.issue_content = et_issue_content.text.toString()
        issueInfo.app_version = mUserSP.getString(K.kAppVersion, "2.0+")
        issueInfo.platform = mUserSP.getString("device_info", "android")
        issueInfo.platform_version = mUserSP.getString("os_version", "0")
        issueInfo.user_num = mUserSP.getString(URLs.kUserNum, "null")
        model.commitIssue(issueInfo)
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

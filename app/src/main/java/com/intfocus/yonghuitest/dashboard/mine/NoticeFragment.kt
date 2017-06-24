package com.intfocus.yonghuitest.dashboard.mine

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.adapter.dashboard.NoticeListAdapter
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.bean.dashboard.NoticeListBean
import com.intfocus.yonghuitest.bean.dashboard.NoticeListDataBean
import com.intfocus.yonghuitest.bean.dashboard.NoticeListRquest
import com.intfocus.yonghuitest.listen.EndLessOnScrollListener
import com.intfocus.yonghuitest.mode.NoticeMode
import com.intfocus.yonghuitest.util.WidgetUtil
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_notice.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by liuruilin on 2017/6/7.
 */

class NoticeFragment : BaseModeFragment<NoticeMode>(), NoticeListAdapter.NoticeItemListener {
    lateinit var ctx: Context
    var rootView: View? = null
    var datas: MutableList<NoticeListDataBean>? = null
    var mNoticeTypeStr: String? = null
    var mNoticeListSP: SharedPreferences? = null
    var gson = Gson()
    var id = ""
    var page = 1
    var totalPage = 100
    lateinit var adapter: NoticeListAdapter

    override fun setSubject(): Subject {
        ctx = act.applicationContext
        mNoticeListSP = ctx.getSharedPreferences("NoticeList", Context.MODE_PRIVATE)
        return NoticeMode(ctx)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_notice, container, false)
            mNoticeTypeStr = model.getNoticeType()
        }
        return rootView
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        rl_notice_select.setOnClickListener { isShowNoticeList() }
        initView()
        super.onActivityCreated(savedInstanceState)
    }

    fun initView() {
        setSwitchCheckListener()
        val mLayoutManager = LinearLayoutManager(ctx)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_notice_list.layoutManager = mLayoutManager
        adapter = NoticeListAdapter(ctx, null, this)
        rv_notice_list.adapter = adapter
        rv_notice_list.addOnScrollListener(object : EndLessOnScrollListener(mLayoutManager) {
            override fun onLoadMore(currentPage: Int) {
                if (page < totalPage) {
                    page += 1
                    model.requestData(page)
                }
                else {
//                    WidgetUtil.showToastShort(ctx, "没有更多公告")
                }
            }
        })
        var noticeListString = mNoticeListSP!!.getString("NoticeList", "")
        if (!noticeListString.equals("")) {
            var noticeListBean = gson.fromJson(noticeListString, NoticeListBean::class.java)
            datas = noticeListBean!!.data.toMutableList()
            adapter.setData(datas)
        }
        model.requestData(1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: NoticeListRquest) {
        totalPage = result.noticeListBean!!.total_page

        if (page == 1 && datas != null) {
            datas!!.clear()
        }

        if (datas == null) {
            datas = result.noticeListBean!!.data.toMutableList()
        }
        else {
            datas!!.addAll(result.noticeListBean!!.data)
        }
        adapter.setData(datas)
    }

    override fun itemClick(position: Int) {
        if (ll_notice_select_list.visibility == View.VISIBLE) {
            ll_notice_select_list.visibility = View.GONE
            if (mNoticeTypeStr != model.getNoticeType()) {
                page = 1
                model.requestData(1)
                mNoticeTypeStr = model.getNoticeType()
            }
            return
        }
        var intent = Intent(act, NoticeContentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("notice_id", position.toString())
        startActivity(intent)
    }

    fun isShowNoticeList() {
        if (ll_notice_select_list.visibility == View.GONE) {
            switch_system_notice.isChecked = mNoticeListSP!!.getBoolean("SystemNotice", true)
            switch_work_notice.isChecked = mNoticeListSP!!.getBoolean("WorkNotice", true)
            switch_warning_system.isChecked = mNoticeListSP!!.getBoolean("WarningSystem", true)
            switch_report_comment.isChecked = mNoticeListSP!!.getBoolean("ReportComment", true)
            ll_notice_select_list.visibility = View.VISIBLE
        } else {
            ll_notice_select_list.visibility = View.GONE
            if (mNoticeTypeStr != model.getNoticeType()) {
                page = 1
                model.requestData(1)
                mNoticeTypeStr = model.getNoticeType()
            }
        }
    }

    fun setSwitchCheckListener() {
        switch_system_notice.setOnCheckedChangeListener { _, isChecked -> mNoticeListSP!!.edit().putBoolean("SystemNotice", isChecked).commit() }
        switch_work_notice.setOnCheckedChangeListener { _, isChecked -> mNoticeListSP!!.edit().putBoolean("WorkNotice", isChecked).commit() }
        switch_warning_system.setOnCheckedChangeListener { _, isChecked -> mNoticeListSP!!.edit().putBoolean("WarningSystem", isChecked).commit() }
        switch_report_comment.setOnCheckedChangeListener { _, isChecked -> mNoticeListSP!!.edit().putBoolean("ReportComment", isChecked).commit() }
    }
}

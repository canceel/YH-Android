package com.intfocus.yonghuitest.dashboard.mine

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.dashboard.mine.adapter.NoticeListAdapter
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.dashboard.mine.bean.NoticeListBean
import com.intfocus.yonghuitest.dashboard.mine.bean.NoticeListDataBean
import com.intfocus.yonghuitest.dashboard.mine.bean.NoticeListRquest
import com.intfocus.yonghuitest.listen.EndLessOnScrollListener
import com.intfocus.yonghuitest.mode.NoticeMode
import com.intfocus.yonghuitest.util.*
import com.kyleduo.switchbutton.SwitchButton
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_notice.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import org.xutils.view.annotation.Event
import org.xutils.x

/**
 * Created by liuruilin on 2017/6/7.
 */

class NoticeFragment : BaseModeFragment<NoticeMode>(), NoticeListAdapter.NoticeItemListener{
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
        mNoticeListSP = ctx.getSharedPreferences("NoticeList", Context.MODE_PRIVATE)
        return NoticeMode(ctx)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_notice, container, false)
            x.view().inject(this, rootView)
            mNoticeTypeStr = model.getNoticeType()
        }
        return rootView
    }

    override fun onDestroyView() {
        EventBus.getDefault().unregister(this)
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        super.onActivityCreated(savedInstanceState)
    }

    fun initView() {
        val mLayoutManager = LinearLayoutManager(ctx)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_notice_list.layoutManager = mLayoutManager
        adapter = NoticeListAdapter(ctx, null, this)
        rv_notice_list.adapter = adapter
        var headerView = SinaRefreshView(ctx)
        headerView.setArrowResource(R.drawable.arrow)
        var bottomView = LoadingView(ctx)
        trl_refresh_layout.setHeaderView(headerView)
        trl_refresh_layout.setBottomView(bottomView)
        trl_refresh_layout.setOnRefreshListener(object: RefreshListenerAdapter(){
            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                if (HttpUtil.isConnected(context)) {
                    page = 1
                    model.requestData(1)
                    mNoticeTypeStr = model.getNoticeType()
                } else {
                    WidgetUtil.showToastShort(context, "请检查网络")
                }
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                super.onLoadMore(refreshLayout)
                if (page < totalPage) {
                    page += 1
                    model.requestData(page)
                } else {
                    WidgetUtil.showToastShort(ctx, "没有更多公告")
                }
            }

        })
        model.requestData(1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: NoticeListRquest) {
        trl_refresh_layout.finishRefreshing()
        trl_refresh_layout.finishLoadmore()
        if (result.isSuccess) {
            totalPage = result.noticeListBean!!.total_page

            if (page == 1 && datas != null) {
                datas!!.clear()
            }

            if (datas == null) {
                datas = result.noticeListBean!!.data.toMutableList()
            } else {
                datas!!.addAll(result.noticeListBean!!.data)
            }
            adapter.setData(datas)
        }
    }

    override fun itemClick(position: Int) {
        var intent = Intent(act, NoticeContentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("notice_id", position.toString())
        startActivity(intent)

        var logParams = JSONObject()
        logParams.put(URLs.kAction, "点击/公告列表")
        ApiHelper.actionNewThreadLog(activity, logParams)
    }

    fun initNoticeSelectSwitch(parent: View) {
        var switch_system_notice = parent.findViewById(R.id.switch_system_notice) as SwitchButton
        var switch_work_notice = parent.findViewById(R.id.switch_work_notice) as SwitchButton
        var switch_warning_system = parent.findViewById(R.id.switch_warning_system) as SwitchButton
        var switch_report_comment = parent.findViewById(R.id.switch_report_comment) as SwitchButton
        switch_system_notice.isChecked = mNoticeListSP!!.getBoolean("SystemNotice", true)
        switch_work_notice.isChecked = mNoticeListSP!!.getBoolean("WorkNotice", true)
        switch_warning_system.isChecked = mNoticeListSP!!.getBoolean("WarningSystem", true)
        switch_report_comment.isChecked = mNoticeListSP!!.getBoolean("ReportComment", true)
        switch_system_notice.setOnCheckedChangeListener { _, isChecked -> mNoticeListSP!!.edit().putBoolean("SystemNotice", isChecked).commit() }
        switch_work_notice.setOnCheckedChangeListener { _, isChecked -> mNoticeListSP!!.edit().putBoolean("WorkNotice", isChecked).commit() }
        switch_warning_system.setOnCheckedChangeListener { _, isChecked -> mNoticeListSP!!.edit().putBoolean("WarningSystem", isChecked).commit() }
        switch_report_comment.setOnCheckedChangeListener { _, isChecked -> mNoticeListSP!!.edit().putBoolean("ReportComment", isChecked).commit() }
    }

    /**
     * 显示菜单
     * @param
     */
    @Event(value = R.id.rl_notice_select)
    private fun showComplaintsPopWindow(clickView: View) {
        var contentView = activity.layoutInflater.inflate(R.layout.popup_notice_select, null)
        //设置弹出框的宽度和高度
        var popupWindow = PopupWindow(contentView,
                DisplayUtil.dip2px(ctx, 180f),
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.isFocusable = true // 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        popupWindow.showAsDropDown(clickView)
        initNoticeSelectSwitch(contentView)
        popupWindow.setOnDismissListener {
            if (mNoticeTypeStr != model.getNoticeType()) {
                page = 1
                model.requestData(1)
                mNoticeTypeStr = model.getNoticeType()
            }
        }
    }
}

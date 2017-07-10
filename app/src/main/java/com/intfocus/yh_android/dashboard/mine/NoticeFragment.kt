package com.intfocus.yh_android.dashboard.mine

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.google.gson.Gson
import com.intfocus.yh_android.R
import com.intfocus.yh_android.dashboard.mine.adapter.NoticeListAdapter
import com.intfocus.yh_android.base.BaseModeFragment
import com.intfocus.yh_android.dashboard.mine.bean.NoticeListBean
import com.intfocus.yh_android.dashboard.mine.bean.NoticeListDataBean
import com.intfocus.yh_android.dashboard.mine.bean.NoticeListRquest
import com.intfocus.yh_android.listen.EndLessOnScrollListener
import com.intfocus.yh_android.mode.NoticeMode
import com.intfocus.yh_android.util.DisplayUtil
import com.intfocus.yh_android.util.HttpUtil
import com.intfocus.yh_android.util.WidgetUtil
import com.kyleduo.switchbutton.SwitchButton
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_notice.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.xutils.view.annotation.Event
import org.xutils.x

/**
 * Created by liuruilin on 2017/6/7.
 */

class NoticeFragment : BaseModeFragment<NoticeMode>(), NoticeListAdapter.NoticeItemListener, SwipeRefreshLayout.OnRefreshListener {
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
        initSwipeLayout()
        super.onActivityCreated(savedInstanceState)
    }

    fun initSwipeLayout() {
        swipe_container.setOnRefreshListener(this)
        swipe_container.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
                android.R.color.holo_orange_light, android.R.color.holo_red_light)
        swipe_container.setDistanceToTriggerSync(300)// 设置手指在屏幕下拉多少距离会触发下拉刷新
        swipe_container.setSize(SwipeRefreshLayout.DEFAULT)
    }

    override fun onRefresh() {
        if (HttpUtil.isConnected(context)) {
            page = 1
            model.requestData(1)
            mNoticeTypeStr = model.getNoticeType()
        } else {
            swipe_container.isRefreshing = false
            WidgetUtil.showToastShort(context, "请检查网络")
        }
    }

    fun initView() {
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
                } else {
//                    WidgetUtil.showToastShort(ctx, "没有更多公告")
                }
            }
        })
        model.requestData(1)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: NoticeListRquest) {
        swipe_container.isRefreshing = false
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

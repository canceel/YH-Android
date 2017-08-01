package com.intfocus.yonghuitest.dashboard.mine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.dashboard.mine.adapter.NoticeListAdapter
import com.intfocus.yonghuitest.dashboard.mine.adapter.NoticeMenuAdapter
import com.intfocus.yonghuitest.dashboard.mine.bean.NoticeListDataBean
import com.intfocus.yonghuitest.dashboard.mine.bean.NoticeListRquest
import com.intfocus.yonghuitest.dashboard.mine.bean.NoticeMenuBean
import com.intfocus.yonghuitest.mode.NoticeMode
import com.intfocus.yonghuitest.util.*
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.common_error_view.*
import kotlinx.android.synthetic.main.fragment_notice.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import org.xutils.x

/**
 * Created by liuruilin on 2017/6/7.
 */

class NoticeFragment : BaseModeFragment<NoticeMode>(), NoticeListAdapter.NoticeItemListener, NoticeMenuAdapter.NoticeItemListener, ErrorUtils.ErrorLisenter {

    var rootView: View? = null
    var datas: MutableList<NoticeListDataBean>? = null
    var gson = Gson()
    var id = ""
    var page = 1
    var totalPage = 100
    lateinit var adapter: NoticeListAdapter
    /**
     *菜单
     */
    lateinit var noticeMenuAdapter: NoticeMenuAdapter //筛选适配器
    var noticeMenuDatas: MutableList<NoticeMenuBean>? = null//筛选数据
    var typeStr: String? = null //筛选条件
    var isRefresh: Boolean = false//是否是刷新
    var isEmpty: Boolean = true//数据是否为空

    override fun setSubject(): Subject {
        return NoticeMode(ctx)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_notice, container, false)
            x.view().inject(this, rootView)
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
        if (TextUtils.isEmpty(typeStr)) {
            typeStr = "0,1,2,3"
        }
        //数据为空(即第一次打开此界面)才初始化Menu数据）,默认全部选中
        if (noticeMenuDatas == null || noticeMenuDatas!!.size == 0) {
            val noticeMenuBean = NoticeMenuBean(0, true)
            val noticeMenuBean1 = NoticeMenuBean(1, true)
            val noticeMenuBean2 = NoticeMenuBean(2, true)
            val noticeMenuBean3 = NoticeMenuBean(3, true)
            noticeMenuDatas = ArrayList()
            noticeMenuDatas!!.add(noticeMenuBean)
            noticeMenuDatas!!.add(noticeMenuBean1)
            noticeMenuDatas!!.add(noticeMenuBean2)
            noticeMenuDatas!!.add(noticeMenuBean3)
        }
        val mMenuLayoutManager = LinearLayoutManager(ctx)
        mMenuLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        menu_recycler_view.layoutManager = mMenuLayoutManager
        noticeMenuAdapter = NoticeMenuAdapter(ctx, noticeMenuDatas, this)
        menu_recycler_view.adapter = noticeMenuAdapter

        val mLayoutManager = LinearLayoutManager(ctx)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_notice_list.layoutManager = mLayoutManager
        adapter = NoticeListAdapter(ctx, null, this)
        rv_notice_list.adapter = adapter
        var headerView = SinaRefreshView(ctx)
        headerView.setArrowResource(R.drawable.loading_up)
        var bottomView = LoadingView(ctx)
        trl_refresh_layout.setHeaderView(headerView)
        trl_refresh_layout.setBottomView(bottomView)
        trl_refresh_layout.setOnRefreshListener(object : RefreshListenerAdapter(), ErrorUtils.ErrorLisenter {
            override fun retry() {
                retryGetData()
            }

            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                isRefresh = true
                if (HttpUtil.isConnected(context)) {
                    page = 1
                    model.requestData(page, typeStr!!)
                } else {
                    trl_refresh_layout.finishRefreshing()
                    trl_refresh_layout.finishLoadmore()
                    isEmpty = datas == null || datas!!.size == 0
                    ErrorUtils.viewProcessing(trl_refresh_layout, ll_empty, ll_retry, "公告预警数据为空", tv_errorMsg, iv_error, isEmpty, false, R.drawable.pic_2, this)
                    WidgetUtil.showToastShort(context, "请检查网络")
                }
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                super.onLoadMore(refreshLayout)
                if (page < totalPage) {
                    page += 1
                    model.requestData(page, typeStr!!)
                } else {
                    trl_refresh_layout.finishLoadmore()
                    WidgetUtil.showToastShort(ctx, "没有更多公告")
                }
            }
        })
        model.requestData(1, typeStr!!)
//        //数据为空即第一次加载时候才请求
//        if (datas == null || datas!!.size == 0) {
//            val noticeListDataBean = NoticeListDataBean(11, 0, "提示信息", "Nginx是俄国人最早开发的Webserver，现在已经风靡全球，相信大家并不陌生。PHP也通过二十多年的发展来到了7系列版本，更加关注性能。", false, "2017-07-24 18:00:00")
//            val noticeListDataBean1 = NoticeListDataBean(11, 1, "提示信息", "Nginx是俄国人最早开发的Webserver，现在已经风靡全球，相信大家并不陌生。PHP也通过二十多年的发展来到了7系列版本，更加关注性能。", true, "2017-07-24 18:00:00")
//            val noticeListDataBean2 = NoticeListDataBean(11, 2, "提示信息", "Nginx是俄国人最早开发的Webserver，现在已经风靡全球，相信大家并不陌生。PHP也通过二十多年的发展来到了7系列版本，更加关注性能。", false, "2017-07-24 18:00:00")
//            val noticeListDataBean3 = NoticeListDataBean(11, 3, "提示信息", "Nginx是俄国人最早开发的Webserver，现在已经风靡全球，相信大家并不陌生。PHP也通过二十多年的发展来到了7系列版本，更加关注性能。", true, "2017-07-24 18:00:00")
//            val noticeListDataBean4 = NoticeListDataBean(11, 2, "提示信息", "Nginx是俄国人最早开发的Webserver，现在已经风靡全球，相信大家并不陌生。PHP也通过二十多年的发展来到了7系列版本，更加关注性能。", false, "2017-07-24 18:00:00")
//            val noticeListDataBean5 = NoticeListDataBean(11, 1, "提示信息", "Nginx是俄国人最早开发的Webserver，现在已经风靡全球，相信大家并不陌生。PHP也通过二十多年的发展来到了7系列版本，更加关注性能。", true, "2017-07-24 18:00:00")
//            val noticeListDataBean6 = NoticeListDataBean(11, 0, "提示信息", "Nginx是俄国人最早开发的Webserver，现在已经风靡全球，相信大家并不陌生。PHP也通过二十多年的发展来到了7系列版本，更加关注性能。", true, "2017-07-24 18:00:00")
//            datas = ArrayList()
//            datas!!.add(noticeListDataBean)
//            datas!!.add(noticeListDataBean1)
//            datas!!.add(noticeListDataBean2)
//            datas!!.add(noticeListDataBean3)
//            datas!!.add(noticeListDataBean4)
//            datas!!.add(noticeListDataBean5)
//            datas!!.add(noticeListDataBean6)
//        }
//        adapter.setData(datas)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: NoticeListRquest) {
        trl_refresh_layout.finishRefreshing()
        trl_refresh_layout.finishLoadmore()
        hideLoading()
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
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(trl_refresh_layout, ll_empty, ll_retry, "公告预警数据为空", tv_errorMsg, iv_error, isEmpty, true, R.drawable.pic_2, null)
        } else {
            WidgetUtil.showToastShort(context, result.errorMsg)
        }
    }

    /**
     * 进入详情
     */
    override fun itemClick(position: Int) {
        var intent = Intent(act, NoticeContentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.putExtra("notice_id", position.toString())
        startActivity(intent)

        var logParams = JSONObject()
        logParams.put(URLs.kAction, "点击/公告预警")
        ActionLogUtil.actionLog(activity, logParams)
    }

    /**
     *点击筛选
     */
    override fun menuClick(noticeMenuBean: NoticeMenuBean) {
        typeStr = ""
        if (noticeMenuDatas != null) {
            for (data in noticeMenuDatas!!.iterator()) {
                if (data == noticeMenuBean) {
                    data.isSelected = !data.isSelected
                }
                if (data.isSelected) {
                    if (!TextUtils.isEmpty(typeStr)) {
                        typeStr = typeStr + "," + data.code
                    } else {
                        typeStr = "" + data.code
                    }
                }
            }
            noticeMenuAdapter.setData(noticeMenuDatas)
        }
        if (HttpUtil.isConnected(context)) {
            showDialog(activity)
            model.requestData(page, typeStr!!)
        } else {
            hideLoading()
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(trl_refresh_layout, ll_empty, ll_retry, "公告预警数据为空", tv_errorMsg, iv_error, isEmpty, false, R.drawable.pic_2, this)
            WidgetUtil.showToastShort(context, "请检查网络")
        }
    }

    override fun retry() {
        retryGetData()
    }

    /**
     * 重新获取数据
     */
    fun retryGetData() {
        if (HttpUtil.isConnected(context)) {
            showDialog(activity)
            model.requestData(page, typeStr!!)
        } else {
            hideLoading()
            trl_refresh_layout.finishRefreshing()
            trl_refresh_layout.finishLoadmore()
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(trl_refresh_layout, ll_empty, ll_retry, "公告预警数据为空", tv_errorMsg, iv_error, isEmpty, false, R.drawable.pic_2, this)
            WidgetUtil.showToastShort(context, "请检查网络")
        }
    }
}

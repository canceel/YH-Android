package com.intfocus.yonghuitest.dashboard.kpi

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.dashboard.kpi.bean.HomeBean
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiRequest
import com.intfocus.yonghuitest.dashboard.kpi.bean.MsgRequest
import com.intfocus.yonghuitest.dashboard.kpi.mode.KpiMode
import com.intfocus.yonghuitest.dashboard.mine.adapter.HomePageAdapter
import com.intfocus.yonghuitest.dashboard.mine.bean.InstituteDataBean
import com.intfocus.yonghuitest.util.ErrorUtils
import com.intfocus.yonghuitest.util.ListUtils
import com.intfocus.yonghuitest.view.DefaultRefreshView
import com.intfocus.yonghuitest.view.MyLinearLayoutManager
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_home.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by CANC on 2017/7/27.
 */
class HomeFragment : BaseModeFragment<KpiMode>(), HomePageAdapter.HomePageListener {
    override fun itemClick(instituteDataBean: InstituteDataBean) {
    }

    lateinit var adapter: HomePageAdapter
    var homeDatas: MutableList<HomeBean>? = null
    var msgDatas: List<KpiGroupItem>? = null
    var rootView: View? = null
    lateinit var mUserSP: SharedPreferences
    override fun setSubject(): Subject {
        mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        return KpiMode(ctx)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
            model.requestData()
        }
        return rootView
    }

    fun initView() {
        recycler_view.layoutManager = MyLinearLayoutManager(context)
        adapter = HomePageAdapter(context, homeDatas, this)
        recycler_view.adapter = adapter
        var headerView = DefaultRefreshView(ctx)
        headerView.setArrowResource(R.drawable.loading_up)
        refresh_layout.setHeaderView(headerView)
        refresh_layout.setOnRefreshListener(object : RefreshListenerAdapter(), ErrorUtils.ErrorLisenter {
            override fun retry() {
                model.requestData()
            }

            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                model.requestData()
                super.onRefresh(refreshLayout)
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                super.onLoadMore(refreshLayout)
            }
        })
        //监听
        recycler_view.addOnScrollListener(HomePageScrollerListener(activity, recycler_view, title_top))
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: KpiRequest) {
        initView()
        if (homeDatas == null) {
            homeDatas = ArrayList()
        }
        homeDatas!!.clear()
        var datas = result.kpi_data
        for (KpiResultData in datas!!.data!!) {
            if ("top_data".equals(KpiResultData.group_name)) {
                val homeBean = HomeBean()
                homeBean.group_name = "轮播图"
                homeBean.index = 0
                homeBean.data = KpiResultData.data
                homeDatas!!.add(homeBean)
            }
            if ("经营预警".equals(KpiResultData.group_name)) {
                val homeBean = HomeBean()
                homeBean.group_name = "经营预警"
                homeBean.index = 2
                homeBean.data = KpiResultData.data
                homeDatas!!.add(homeBean)
            }
            if ("生意概况".equals(KpiResultData.group_name)) {
                val homeBean = HomeBean()
                homeBean.group_name = "生意概况"
                homeBean.index = 3
                homeBean.data = KpiResultData.data
                homeDatas!!.add(homeBean)
            }
        }
        /**
         * 添加底部信息
         */
        val homeBean = HomeBean()
        homeBean.group_name = "底部信息"
        homeBean.index = 4
        homeDatas!!.add(homeBean)
        /**
         * 预先添加一个文字跳动模块，解决刷新跳动问题
         */
        val homeBean1 = HomeBean()
        homeBean1.group_name = "通知"
        homeBean1.index = 1
        homeBean1.data = msgDatas
        homeDatas!!.add(homeBean1)
        /**
         * 排序，根据index 升序
         */
        ListUtils.sort(homeDatas, true, "index")
        model.requestMessage()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: MsgRequest) {
        refresh_layout.finishLoadmore()
        refresh_layout.finishRefreshing()
        if (homeDatas == null) homeDatas = ArrayList()
        if (result.isSuccess) {
            val homeBean = HomeBean()
            homeBean.group_name = "通知"
            homeBean.index = 1
            msgDatas = result.msgData!!.data
            if (homeDatas != null && homeDatas!!.size > 0) {
                for (homeData in homeDatas!!) {
                    if (1 == homeData.index) {
                        homeData.data = msgDatas
                    }
                }
            }
        }
        ListUtils.sort(homeDatas, true, "index")
        adapter.setData(homeDatas)
    }
}

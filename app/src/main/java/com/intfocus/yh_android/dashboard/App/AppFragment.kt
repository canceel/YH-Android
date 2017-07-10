package com.intfocus.yh_android.dashboard.App

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.yh_android.R
import com.intfocus.yh_android.dashboard.App.adapter.AppListAdapter
import com.intfocus.yh_android.dashboard.App.adapter.AppListItemAdapter
import com.intfocus.yh_android.base.BaseModeFragment
import com.intfocus.yh_android.bean.dashboard.AppListPageRequest
import com.intfocus.yh_android.bean.dashboard.CategoryBean
import com.intfocus.yh_android.dashboard.App.mode.AppListMode
import com.intfocus.yh_android.subject.SubjectActivity
import com.intfocus.yh_android.util.HttpUtil
import com.intfocus.yh_android.util.URLs
import com.intfocus.yh_android.util.WidgetUtil
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_app.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主页 - 专题
 * Created by liuruilin on 2017/6/15.
 */
class AppFragment: BaseModeFragment<AppListMode>(), AppListItemAdapter.ItemListener, SwipeRefreshLayout.OnRefreshListener {
    lateinit var ctx: Context
    var rootView : View? = null
    var datas: List<CategoryBean>? = null

    override fun setSubject(): Subject {
        ctx = act.applicationContext
        return AppListMode(ctx,"app")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_app, container, false)
            model.requestData()
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initSwipeLayout()
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
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
            model.requestData()
        } else {
            swipe_container.isRefreshing = false
            WidgetUtil.showToastShort(context, "请检查网络")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun initView(requestReport: AppListPageRequest) {
        if (requestReport.isSuccess) {
            datas = requestReport.categroy_list
            val mLayoutManager = LinearLayoutManager(ctx)
            mLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rv_app_list.layoutManager = mLayoutManager
            rv_app_list.adapter = AppListAdapter(ctx, datas!![0].data, this)
        }
        swipe_container.isRefreshing = false
    }

    override fun itemClick(bannerName: String?, link: String?) {
        if (!link!!.isEmpty()) {
            val intent = Intent(activity, SubjectActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra(URLs.kBannerName, bannerName)
            intent.putExtra(URLs.kLink, link)
            startActivity(intent)
        }
    }
}

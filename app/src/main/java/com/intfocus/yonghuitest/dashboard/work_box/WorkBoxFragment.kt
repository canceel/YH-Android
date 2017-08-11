package com.intfocus.yonghuitest.dashboard.work_box

import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.ToastUtils
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_work_box.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxFragment: BaseModeFragment<WorkBoxMode>(), SwipeRefreshLayout.OnRefreshListener {
    var rootView : View? = null
    var datas: List<WorkBoxItem>? = null

    override fun setSubject(): Subject {
        return WorkBoxMode(ctx)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_work_box, container, false)
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
            ToastUtils.show(context, "请检查网络")
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun initView(request: WorkBoxRequest) {
        if (request.isSuccess) {
            datas = request.workBoxDatas
            gv_work_box.adapter = WorkBoxAdapter(ctx, datas)
        }
        swipe_container.isRefreshing = false
    }
}

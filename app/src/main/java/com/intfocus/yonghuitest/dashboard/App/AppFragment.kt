package com.intfocus.yonghuitest.dashboard.App

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.adapter.dashboard.AppListAdapter
import com.intfocus.yonghuitest.adapter.dashboard.AppListItemAdapter
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.bean.dashboard.CategoryBean
import com.intfocus.yonghuitest.bean.dashboard.ListPageResult
import com.intfocus.yonghuitest.mode.ListPageMode
import com.intfocus.yonghuitest.subject.SubjectActivity
import com.intfocus.yonghuitest.util.URLs
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_app.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * 主页 - 专题
 * Created by liuruilin on 2017/6/15.
 */
class AppFragment: BaseModeFragment<ListPageMode>(), AppListItemAdapter.ItemListener{
    lateinit var ctx: Context
    var rootView : View? = null
    var datas: List<CategoryBean>? = null

    override fun setSubject(): Subject {
        ctx = act.applicationContext
        return ListPageMode(ctx)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_app, container, false)
            model.requestData("app")
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun initView(result: ListPageResult) {
        if (result.isSuccess) {
            datas = result.categroy_list
            val mLayoutManager = LinearLayoutManager(ctx)
            mLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rv_app_list.layoutManager = mLayoutManager
            rv_app_list.adapter = AppListAdapter(ctx, datas!![0].data, this)
        }
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
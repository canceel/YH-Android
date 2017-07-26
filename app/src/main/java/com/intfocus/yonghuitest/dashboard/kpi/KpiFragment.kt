package com.intfocus.yonghuitest.dashboard.kpi

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.dashboard.DashboardActivity
import com.intfocus.yonghuitest.dashboard.kpi.adapter.KpiItemAdapter
import com.intfocus.yonghuitest.dashboard.kpi.adapter.KpiStickAdapter
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiGroup
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiRequest
import com.intfocus.yonghuitest.dashboard.kpi.mode.KpiMode
import com.intfocus.yonghuitest.listen.CustPagerTransformer
import com.intfocus.yonghuitest.util.ErrorUtils
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.WidgetUtil
import com.intfocus.yonghuitest.view.DefaultRefreshView
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.header.GoogleDotView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.common_error_view.*
import kotlinx.android.synthetic.main.fragment_kpi.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import sumimakito.android.advtextswitcher.Switcher
import java.util.*

/**
 * Created by liuruilin on 2017/6/20.
 */
class KpiFragment : BaseModeFragment<KpiMode>(), ViewPager.OnPageChangeListener{
    lateinit var ctx: Context
    lateinit var mViewPagerAdapter: KpiStickAdapter
    var rootView: View? = null
    var gson = Gson()
    lateinit var mUserSP: SharedPreferences
    val FIRST_PAGE_INDEX: Int = 0
    var stickSzize: Int = 0
    var timer = Timer()
    lateinit var stickCycle : StickCycleTask

    override fun setSubject(): Subject {
        ctx = act.applicationContext
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
            rootView = inflater!!.inflate(R.layout.fragment_kpi, container, false)
            model.requestData()
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initAffiche()
        super.onActivityCreated(savedInstanceState)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun initView(result: KpiRequest) {
        stickCycle = StickCycleTask()

        var top_fragment: MutableList<Fragment> = mutableListOf()
        var kpi_datas: MutableList<KpiGroup> = mutableListOf()
        var datas = result.kpi_data

        for (kpiGroupDatas in datas!!.data!!.iterator()) {
            if (kpiGroupDatas.group_name.equals("top_data")) {
                for (kpiGroupItem in kpiGroupDatas!!.data!!.iterator()) {
                    top_fragment.add(NumberOneFragment.newInstance(kpiGroupItem))
                }
            } else {
                kpi_datas.add(kpiGroupDatas)
            }
        }

        if (top_fragment != null) {
            stickSzize = top_fragment.size
            mViewPagerAdapter = KpiStickAdapter(childFragmentManager, top_fragment)
            mViewPagerAdapter.switchTo(FIRST_PAGE_INDEX)

            vp_kpi_stick.setPageTransformer(false, CustPagerTransformer(act.applicationContext))
            vp_kpi_stick.adapter = mViewPagerAdapter
            vp_kpi_stick.addOnPageChangeListener(this)
            vp_kpi_stick.currentItem = 0

            indicator.setViewPager(vp_kpi_stick)
            timer.schedule(stickCycle, 0, 5000)
        }

        if (kpi_datas != null) {
            var layoutManager: StaggeredGridLayoutManager
            layoutManager = object : StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

            rc_kpi_groups.layoutManager = layoutManager
            var recycleAdapter = KpiItemAdapter(ctx, kpi_datas)
            rc_kpi_groups.adapter = recycleAdapter

            var headerView = DefaultRefreshView(ctx)
            headerView.setArrowResource(R.drawable.loading_up)
            trl_refresh_layout.setHeaderView(headerView)
            trl_refresh_layout.setOnRefreshListener(object : RefreshListenerAdapter(), ErrorUtils.ErrorLisenter {
                override fun retry() {
                }

                override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                    super.onRefresh(refreshLayout)
                }

                override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                    super.onLoadMore(refreshLayout)
                }

            })
        }
        rootView!!.invalidate()
    }

    /**
     * 初始化公告控件
     */
    private fun initAffiche() {
        ll_kpi_notice.visibility = View.GONE
        Thread(Runnable {
            val texts = model.getMessage()
            if (texts != null) {
                activity.runOnUiThread {
                    tv_kpi_notice.setTexts(texts)
                    tv_kpi_notice.setCallback(activity as DashboardActivity)
                    Switcher().attach(tv_kpi_notice).setDuration(5000).start()
                    ll_kpi_notice.visibility = View.VISIBLE
                }
            }
        }).start()
    }

    //重写ViewPager页面切换的处理方法
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        mViewPagerAdapter.switchTo(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    inner class StickCycleTask : TimerTask() {
        override fun run() {
            act.runOnUiThread {
                if (vp_kpi_stick != null) {
                    if (vp_kpi_stick.currentItem + 1 < stickSzize) {
                        vp_kpi_stick.currentItem = vp_kpi_stick.currentItem + 1
                    }
                    else {
                        vp_kpi_stick.currentItem = 0
                    }
                }
            }
        }
    }
}

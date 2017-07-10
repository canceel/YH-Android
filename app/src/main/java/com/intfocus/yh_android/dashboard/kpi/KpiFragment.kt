package com.intfocus.yh_android.dashboard.kpi

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.gson.Gson
import com.intfocus.yh_android.R
import com.intfocus.yh_android.dashboard.old_kpi.MarginDecoration
import com.intfocus.yh_android.base.BaseModeFragment
import com.intfocus.yh_android.dashboard.DashboardActivity
import com.intfocus.yh_android.dashboard.kpi.adapter.*
import com.intfocus.yh_android.dashboard.kpi.bean.KpiGroup
import com.intfocus.yh_android.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.yh_android.dashboard.kpi.bean.KpiRequest
import com.intfocus.yh_android.dashboard.kpi.mode.KpiMode
import com.intfocus.yh_android.subject.HomeTricsActivity
import com.intfocus.yh_android.subject.SubjectActivity
import com.intfocus.yh_android.subject.TableActivity
import com.intfocus.yh_android.util.*
import com.zbl.lib.baseframe.core.Subject
import com.zbl.lib.baseframe.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_kpi.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import sumimakito.android.advtextswitcher.Switcher
import java.util.*

/**
 * Created by liuruilin on 2017/6/20.
 */
class KpiFragment : BaseModeFragment<KpiMode>(), ViewPager.OnPageChangeListener, SwipeRefreshLayout.OnRefreshListener {
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
            if (stickCycle != null) {
                stickCycle.cancel()
            }
            model.requestData()
        } else {
            swipe_container.isRefreshing = false
            WidgetUtil.showToastShort(context, "请检查网络")
        }
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

            vp_kpi_stick.adapter = mViewPagerAdapter
            vp_kpi_stick.addOnPageChangeListener(this)
            vp_kpi_stick.currentItem = 0

            indicator.setViewPager(vp_kpi_stick)
            timer.schedule(stickCycle, 0, 5000)
        }

        if (kpi_datas != null) {
            val mLayoutManager = LinearLayoutManager(ctx)
            mLayoutManager.orientation = LinearLayoutManager.VERTICAL

            //设置布局管理器
            rc_kpi_groups.layoutManager = mLayoutManager
            //设置Adapter
            var recycleAdapter = KpiItemAdapter(ctx, kpi_datas)
            rc_kpi_groups.adapter = recycleAdapter
//            rc_kpi_groups.isNestedScrollingEnabled = false
        }

        swipe_container.isRefreshing = false
        rootView!!.invalidate()
    }

    private fun initRecycleView(recyclerView: RecyclerView, itemDatas: KpiGroup) {
        val offset = DisplayUtil.dip2px(ctx, -3.5f)
        recyclerView.setPadding(offset, 0 - offset, offset, 0 - offset + 3)

        var layoutManager: StaggeredGridLayoutManager
        if (itemDatas.data!![0].dashboard_type.equals("number2")) {
            layoutManager = object : StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

            //设置布局管理器
            recyclerView.layoutManager = layoutManager
            //设置Adapter
            var recycleAdapter = NumberThreeItemAdapter(ctx, itemDatas.data)
            recyclerView.adapter = recycleAdapter
            //设置分隔线
            recyclerView.addItemDecoration(MarginDecoration(ctx))
            //设置增加或删除条目的动画
            recyclerView.itemAnimator = DefaultItemAnimator()
        } else {
            val mLayoutManager = LinearLayoutManager(ctx)
            mLayoutManager.orientation = LinearLayoutManager.VERTICAL

            //设置布局管理器
            recyclerView.layoutManager = mLayoutManager
            //设置Adapter
            var recycleAdapter = NumberTwoItemAdapter(ctx, itemDatas.data)
            recyclerView.adapter = recycleAdapter
            //设置分隔线
            recyclerView.addItemDecoration(MarginDecoration(ctx))
            //设置增加或删除条目的动画
            recyclerView.itemAnimator = DefaultItemAnimator()
        }
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

    /**
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(datas: KpiGroupItem?) {
        if (datas != null) {
            val link = "/" + datas.target_url
            val bannerName = datas.title

            if (link.indexOf("template") > 0 && link.indexOf("group") > 0) {
                try {
                    val templateID = TextUtils.split(link, "/")[6]
                    val groupID = mUserSP.getInt(URLs.kGroupId, 0)
                    val reportID = TextUtils.split(link, "/")[8]
                    var urlString: String
                    val intent: Intent

                    when (templateID) {
                        "-1", "2", "4" -> {
                            intent = Intent(activity, SubjectActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra(URLs.kBannerName, bannerName)
                            intent.putExtra(URLs.kLink, link)
                            intent.putExtra(URLs.kObjectId, 1)
                            intent.putExtra(URLs.kObjectType, 1)
                            intent.putExtra("groupID", groupID)
                            intent.putExtra("reportID", reportID)
                            startActivity(intent)
                        }

                        "3" -> {
                            intent = Intent(ctx, HomeTricsActivity::class.java)
                            urlString = String.format("%s/api/v1/group/%d/template/%s/report/%s/json",
                                    K.kBaseUrl, groupID, templateID, reportID)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra(URLs.kBannerName, bannerName)
                            intent.putExtra(URLs.kObjectId, 1)
                            intent.putExtra(URLs.kObjectType, 1)
                            intent.putExtra("groupID", groupID)
                            intent.putExtra("reportID", reportID)
                            intent.putExtra("urlString", urlString)
                            startActivity(intent)
                        }

                        "5" -> {
                            intent = Intent(ctx, TableActivity::class.java)
                            urlString = String.format("%s/api/v1/group/%d/template/%s/report/%s/json",
                                    K.kBaseUrl, groupID, templateID, reportID)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra(URLs.kBannerName, bannerName)
                            intent.putExtra(URLs.kObjectId, 1)
                            intent.putExtra(URLs.kObjectType, 1)
                            intent.putExtra("groupID", groupID)
                            intent.putExtra("reportID", reportID)
                            intent.putExtra("urlString", urlString)
                            startActivity(intent)
                        }

                        else -> showTemplateErrorDialog()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            } else {
                val intent = Intent(activity, SubjectActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra(URLs.kBannerName, bannerName)
                intent.putExtra(URLs.kLink, link)
                intent.putExtra(URLs.kObjectId, 1)
                intent.putExtra(URLs.kObjectType, 1)
                startActivity(intent)
            }
        } else {
            ToastUtil.showToast(ctx, "数据实体为空")
        }
    }

    internal fun showTemplateErrorDialog() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("温馨提示")
                .setMessage("当前版本暂不支持该模板, 请升级应用后查看")
                .setPositiveButton("前去升级") { _, _ ->
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(K.kPgyerUrl))
                    startActivity(browserIntent)
                }
                .setNegativeButton("稍后升级") { _, _ ->
                    // 返回 LoginActivity
                }
        builder.show()
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

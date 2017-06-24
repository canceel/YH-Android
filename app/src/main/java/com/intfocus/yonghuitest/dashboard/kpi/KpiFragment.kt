package com.intfocus.yonghuitest.dashboard.kpi

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.adapter.MessageViewPagerAdapter
import com.intfocus.yonghuitest.adapter.dashboard.MarginDecoration
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.bean.dashboard.kpi.MeterClickEventEntity
import com.intfocus.yonghuitest.dashboard.DashboardActivity
import com.intfocus.yonghuitest.dashboard.kpi.adapter.KpiGroupItemAdapter
import com.intfocus.yonghuitest.dashboard.kpi.adapter.KpiStickAdapter
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiGroup
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiRequest
import com.intfocus.yonghuitest.dashboard.kpi.mode.KpiMode
import com.intfocus.yonghuitest.subject.HomeTricsActivity
import com.intfocus.yonghuitest.subject.SubjectActivity
import com.intfocus.yonghuitest.subject.TableActivity
import com.intfocus.yonghuitest.util.DisplayUtil
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs
import com.zbl.lib.baseframe.core.Subject
import com.zbl.lib.baseframe.utils.ToastUtil
import kotlinx.android.synthetic.main.fragment_kpi.*
import kotlinx.android.synthetic.main.fragment_message.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import sumimakito.android.advtextswitcher.Switcher

/**
 * Created by liuruilin on 2017/6/20.
 */
class KpiFragment: BaseModeFragment<KpiMode>(), ViewPager.OnPageChangeListener  {
    lateinit var ctx: Context
    lateinit var mViewPagerAdapter: KpiStickAdapter
    var rootView : View? = null
    var gson = Gson()
    lateinit var mUserSP : SharedPreferences
    var top_fragment: MutableList<Fragment> = mutableListOf()
    var kpi_datas: MutableList<KpiGroup> = mutableListOf()
    val FIRST_PAGE_INDEX: Int = 0

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

    @Subscribe (threadMode = ThreadMode.MAIN)
    fun initView(result: KpiRequest) {
        ll_kpi_groups.removeAllViews()
        ll_kpi_groups.setBackgroundResource(R.color.base_background)

        var datas = result.kpi_data
        for (kpiGroupDatas in datas!!.data!!.iterator()) {
            if (kpiGroupDatas.group_name.equals("top_data")) {
                for (kpiGroupItem in kpiGroupDatas!!.data!!.iterator()) {
                    top_fragment!!.add(NumberOneFragment.newInstance(kpiGroupItem))
                }
            }
            else {
                kpi_datas!!.add(kpiGroupDatas)
            }
        }

        if (top_fragment != null) {
            mViewPagerAdapter = KpiStickAdapter(childFragmentManager, top_fragment)
            mViewPagerAdapter.switchTo(FIRST_PAGE_INDEX)

            vp_kpi_stick.adapter = mViewPagerAdapter
            vp_kpi_stick.addOnPageChangeListener(this)
            vp_kpi_stick.currentItem = 0
        }

        if (kpi_datas != null) {
            for (kpiGroup in kpi_datas!!.iterator()) {
                val inflater = LayoutInflater.from(ctx)
                val view = inflater.inflate(R.layout.fragment_kpi_group, null)
                var tvKpiGroupName = view.findViewById(R.id.tv_kpi_group_name) as TextView
                var rvKpiGroupItems = view.findViewById(R.id.rv_kpi_group) as RecyclerView
                tvKpiGroupName.text = kpiGroup.group_name
                initRecycleView(rvKpiGroupItems, kpiGroup)
                ll_kpi_groups.addView(view)
            }
        }

        rootView!!.invalidate()
    }

    private fun initRecycleView(recyclerView: RecyclerView, itemDatas: KpiGroup) {
        val offset = DisplayUtil.dip2px(ctx, -3.5f)
        recyclerView.setPadding(offset, 0 - offset, offset, offset)
        var layoutManager : StaggeredGridLayoutManager
        if (itemDatas.data!![0].dashboard_type.equals("number2")) {
            layoutManager = object : StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        }
        else {
            layoutManager = object : StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
        }

        //设置布局管理器
        recyclerView.layoutManager = layoutManager
        //设置Adapter
        val recycleAdapter = KpiGroupItemAdapter(ctx, activity.supportFragmentManager, itemDatas.data)
        recyclerView.adapter = recycleAdapter
        //设置分隔线
        recyclerView.addItemDecoration(MarginDecoration(ctx))
        //设置增加或删除条目的动画
        recyclerView.itemAnimator = DefaultItemAnimator()
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
                    Switcher().attach(tv_kpi_notice).setDuration(3000).start()
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
                    var urlString = link
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
}

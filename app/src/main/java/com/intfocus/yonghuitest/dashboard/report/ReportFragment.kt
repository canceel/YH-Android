package com.intfocus.yonghuitest.dashboard.report

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.bean.dashboard.CategoryBean
import com.intfocus.yonghuitest.bean.dashboard.ReportListPageRequest
import com.intfocus.yonghuitest.dashboard.report.adapter.ReportsLeftListAdapter
import com.intfocus.yonghuitest.dashboard.report.adapter.ReportsRightGVAdapter
import com.intfocus.yonghuitest.dashboard.report.adapter.ReportsRightRVAdapter
import com.intfocus.yonghuitest.mode.ReportsListMode
import com.intfocus.yonghuitest.subject.*
import com.intfocus.yonghuitest.subject.template_v2.ui.ModularTwo_Activity
import com.intfocus.yonghuitest.subject.HomeTricsActivity
import com.intfocus.yonghuitest.subject.SubjectActivity
import com.intfocus.yonghuitest.subject.TableActivity
import com.intfocus.yonghuitest.subject.WebApplicationActivity
import com.intfocus.yonghuitest.util.*
import com.intfocus.yonghuitest.util.URLs.kGroupId
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_reports.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by liuruilin on 2017/6/15.
 */
class ReportFragment : BaseModeFragment<ReportsListMode>(), ReportsLeftListAdapter.ReportLeftListListener, ReportsRightGVAdapter.ItemListener, SwipeRefreshLayout.OnRefreshListener {
    lateinit var ctx: Context
    var rootView: View? = null
    var datas: List<CategoryBean>? = null
    lateinit var reportsRightAdapter: ReportsRightRVAdapter
    lateinit var reportsLeftAdapter: ReportsLeftListAdapter

    override fun setSubject(): Subject {
        ctx = act.applicationContext
        return ReportsListMode(ctx, "reports")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_reports, container, false)
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
    fun initView(requestReport: ReportListPageRequest) {
        if (requestReport.isSuccess) {
            datas = requestReport.categroy_list
            reportsLeftAdapter = ReportsLeftListAdapter(ctx, datas, this)
            ll_reports_category_list.adapter = reportsLeftAdapter
            val mLayoutManager = LinearLayoutManager(ctx)
            mLayoutManager.orientation = LinearLayoutManager.VERTICAL
            rv_reports_group_list.layoutManager = mLayoutManager
            reportsRightAdapter = ReportsRightRVAdapter(ctx, datas!![0].data, this)
            rv_reports_group_list.adapter = reportsRightAdapter
        }
        swipe_container.isRefreshing = false
    }

    override fun reportLeftItemClick(sign: ImageView, position: Int) {
        reportsRightAdapter.setData(datas!![position].data)
        reportsLeftAdapter.refreshListItemState(position)
    }

    override fun reportItemClick(bannerName: String, link: String) {
        if (link.indexOf("template") > 0 && link.indexOf("group") > 0) {
            try {
                val groupID = act.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getInt(URLs.kGroupId,0)
                val reportID = TextUtils.split(link, "/")[8]
                var urlString: String
                val intent: Intent
                when {
                    link.indexOf("template/2") or link.indexOf("template/4") > 0 -> {
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
                    link.indexOf("template/3") > 0-> {
                        intent = Intent(ctx, HomeTricsActivity::class.java)
                        urlString = String.format("%s/api/v1/group/%d/template/%s/report/%s/json",
                                K.kBaseUrl, groupID, "3", reportID)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra(URLs.kBannerName, bannerName)
                        intent.putExtra(URLs.kObjectId, 1)
                        intent.putExtra(URLs.kObjectType, 1)
                        intent.putExtra("groupID", groupID)
                        intent.putExtra("reportID", reportID)
                        intent.putExtra("urlString", urlString)
                        startActivity(intent)
                    }
                    link.indexOf("template/5") > 0  -> {
                        intent = Intent(ctx, TableActivity::class.java)
                        urlString = String.format("%s/api/v1/group/%d/template/%s/report/%s/json",
                                K.kBaseUrl, groupID, "5", reportID)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra(URLs.kBannerName, bannerName)
                        intent.putExtra(URLs.kObjectId, 1)
                        intent.putExtra(URLs.kObjectType, 1)
                        intent.putExtra("groupID", groupID)
                        intent.putExtra("reportID", reportID)
                        intent.putExtra("urlString", urlString)
                        startActivity(intent)
                    }
                    link.indexOf("template/1") > 0 -> {
                        val intent = Intent(activity, ModularTwo_Activity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra(URLs.kBannerName, bannerName)
                        intent.putExtra(URLs.kLink, link)
                        startActivity(intent)
                    }
                    else -> showTemplateErrorDialog()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        else {
            val intent = Intent(ctx, WebApplicationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra(URLs.kBannerName, bannerName)
            intent.putExtra(URLs.kLink, link)
            intent.putExtra(URLs.kObjectId, 1)
            intent.putExtra(URLs.kObjectType, 1)
            startActivity(intent)
        }


        var logParams = JSONObject()
        logParams.put(URLs.kAction, "点击/报表/报表")
        logParams.put(URLs.kObjTitle, bannerName)
        ApiHelper.actionNewThreadLog(activity, logParams)
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

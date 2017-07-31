package com.intfocus.yonghuitest.dashboard.mine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.dashboard.mine.adapter.InstituteAdapter
import com.intfocus.yonghuitest.dashboard.mine.bean.CollectionRquest
import com.intfocus.yonghuitest.dashboard.mine.bean.InstituteDataBean
import com.intfocus.yonghuitest.dashboard.mine.bean.InstituteRquest
import com.intfocus.yonghuitest.mode.InstituteMode
import com.intfocus.yonghuitest.util.ErrorUtils
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.ToastUtils
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.common_error_view.*
import kotlinx.android.synthetic.main.fragment_instiute.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.xutils.x

/**
 * Created by liuruilin on 2017/6/7.
 */
class InstituteFragment : BaseModeFragment<InstituteMode>(), InstituteAdapter.NoticeItemListener, ErrorUtils.ErrorLisenter {

    lateinit var ctx: Context
    var rootView: View? = null
    var datas: MutableList<InstituteDataBean>? = null
    var gson = Gson()
    var id = ""
    var page = 1
    var totalPage = 100

    lateinit var adapter: InstituteAdapter
    var isRefresh: Boolean = false//是否是刷新
    var isEmpty: Boolean = true//数据是否为空
    var keyWord: String? = ""//搜索关键字


    override fun setSubject(): Subject {
        ctx = act.applicationContext
        return InstituteMode(ctx)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_instiute, container, false)
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
        val mLayoutManager = LinearLayoutManager(ctx)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_notice_list.layoutManager = mLayoutManager
        adapter = InstituteAdapter(ctx, null, this)
        rv_notice_list.adapter = adapter
        var headerView = SinaRefreshView(ctx)
        headerView.setArrowResource(R.drawable.loading_up)
        var bottomView = LoadingView(ctx)
        trl_refresh_layout.setHeaderView(headerView)
        trl_refresh_layout.setBottomView(bottomView)
        trl_refresh_layout.setOnRefreshListener(object : RefreshListenerAdapter(), ErrorUtils.ErrorLisenter {
            override fun retry() {
                getData(true)
            }

            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                isRefresh = true
                if (HttpUtil.isConnected(context)) {
                    page = 1
                    getData(false)
                } else {
                    trl_refresh_layout.finishRefreshing()
                    trl_refresh_layout.finishLoadmore()
                    isEmpty = datas == null || datas!!.size == 0
                    ErrorUtils.viewProcessing(trl_refresh_layout, ll_empty, ll_retry, "无更多文章了", tv_errorMsg, iv_error, isEmpty, false, R.drawable.pic_3, this)
                    ToastUtils.show(context, "请检查网络")
                }
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                super.onLoadMore(refreshLayout)
                if (page < totalPage) {
                    page += 1
                    getData(false)
                } else {
                    trl_refresh_layout.finishLoadmore()
                    ToastUtils.show(ctx, "没有更多公告")
                }
            }

        })
        getData(true)
        edit_search!!.setOnEditorActionListener({ textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyWord = textView.text.toString().trim()
                getData(true)
            }
            false
        })
    }

    /**
     * 获取数据
     */
    fun getData(isShowDialog: Boolean) {
        if (HttpUtil.isConnected(context)) {
            if (isShowDialog) {
                if (loadingDialog == null || !loadingDialog.isShowing) {
                    showDialog(activity)
                }
            }
            model.requestData(page, keyWord!!)
        } else {
            hideLoading()
            trl_refresh_layout.finishRefreshing()
            trl_refresh_layout.finishLoadmore()
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(trl_refresh_layout, ll_empty, ll_retry, "无更多文章了", tv_errorMsg, iv_error, isEmpty, false, R.drawable.pic_3, this)
            ToastUtils.show(context, "请检查网络")
        }
    }

    /**
     *  操作收藏
     * favouritStatus 1:收藏，2:取消收藏
     */
    fun operatingCollection(articleId: String, favouritStatus: String) {
        if (HttpUtil.isConnected(context)) {
            showDialog(activity)
            model.operatingCollection(articleId, favouritStatus)
        } else {
            hideLoading()
            ToastUtils.show(context, "请检查网络")
        }
    }

    /**
     * 获取列表数据
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: InstituteRquest) {
        trl_refresh_layout.finishRefreshing()
        trl_refresh_layout.finishLoadmore()
        hideLoading()
        if (result.isSuccess) {
            totalPage = result.instittuteListBean!!.page!!.totalPage
            if (page == 1 && datas != null) {
                datas!!.clear()
            }

            if (datas == null) {
                datas = result.instittuteListBean!!.page!!.list.toMutableList()
            } else {
                datas!!.addAll(result.instittuteListBean!!.page!!.list)
            }
            adapter.setData(datas)
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(trl_refresh_layout, ll_empty, ll_retry, "无更多文章了", tv_errorMsg, iv_error, isEmpty, true, R.drawable.pic_3, this)
        } else {
            ToastUtils.show(context, result.errorMsg)
        }
    }

    /**
     * 接收收藏改变结果
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: CollectionRquest) {
        if (result.isSuccess) {
            ToastUtils.show(context, result!!.collectionBean!!.message.toString())
            getData(true)
        } else {
            ToastUtils.show(context, result!!.errorMsg)
            hideLoading()
        }
    }

    /**
     * 进去文章详情
     */
    override fun itemClick(instituteDataBean: InstituteDataBean) {
        var intent = Intent(act, InstituteContentActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("id", instituteDataBean!!.id.toString())
        intent.putExtra("title", instituteDataBean!!.title.toString())
        startActivity(intent)
    }

    /**
     * 添加收藏
     */
    override fun addCollection(instituteDataBean: InstituteDataBean) {
        operatingCollection(instituteDataBean!!.id.toString(), "1")
    }

    /**
     * 取消收藏
     */
    override fun cancelCollection(instituteDataBean: InstituteDataBean) {
        operatingCollection(instituteDataBean!!.id.toString(), "2")
    }

    /**
     * 网络请求失败，重试方法
     */
    override fun retry() {
        getData(true)
    }
}
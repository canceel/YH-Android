package com.intfocus.yonghuitest.dashboard.mine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.RefreshActivity
import com.intfocus.yonghuitest.dashboard.mine.adapter.InstituteAdapter
import com.intfocus.yonghuitest.dashboard.mine.bean.InstituteDataBean
import com.intfocus.yonghuitest.data.response.BaseResult
import com.intfocus.yonghuitest.data.response.article.ArticleResult
import com.intfocus.yonghuitest.net.ApiException
import com.intfocus.yonghuitest.net.CodeHandledSubscriber
import com.intfocus.yonghuitest.net.RetrofitUtil
import com.intfocus.yonghuitest.util.ErrorUtils
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.ToastUtils
import com.intfocus.yonghuitest.util.URLs
import com.intfocus.yonghuitest.view.CommonPopupWindow
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView

class FavoriteActivity : RefreshActivity(), InstituteAdapter.NoticeItemListener {

    lateinit var adapter: InstituteAdapter
    var datas: MutableList<InstituteDataBean>? = null
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        setRefreshLayout()
        userId = mActivity.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString(URLs.kUserNum, "")
        init()
    }

    fun init() {
        val mLayoutManager = LinearLayoutManager(mActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerView.layoutManager = mLayoutManager
        adapter = InstituteAdapter(mActivity, null, this)
        recyclerView.adapter = adapter
        var headerView = SinaRefreshView(mActivity)
        headerView.setArrowResource(R.drawable.loading_up)
        var bottomView = LoadingView(mActivity)
        refreshLayout.setHeaderView(headerView)
        refreshLayout.setBottomView(bottomView)
        getData(true)
    }

    /**
     * 获取数据
     */
    override fun getData(isShowDialog: Boolean) {
        if (!HttpUtil.isConnected(mActivity)) {
            finshRequest()
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(refreshLayout, llError, llRetry, "无更多文章了", tvErrorMsg, ivError,
                    isEmpty!!, false, R.drawable.pic_3, {
                getData(true)
            })
            ToastUtils.show(mActivity, "请检查网络链接")
            return
        }
        if (isShowDialog && (loadingDialog == null || !loadingDialog!!.isShowing)) {
            showLoading()
        }
        RetrofitUtil.getHttpService().getArticleList(userId, page.toString(), pageSize.toString())
                .compose(RetrofitUtil.CommonOptions<ArticleResult>())
                .subscribe(object : CodeHandledSubscriber<ArticleResult>() {
                    override fun onCompleted() {
                        finshRequest()
                    }

                    override fun onError(apiException: ApiException) {
                        finshRequest()
                        ToastUtils.show(mActivity, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: ArticleResult) {
                        finshRequest()
                        totalPage = data.data!!.totalPage
                        isLasePage = page == totalPage
                        if (datas == null) {
                            datas = ArrayList()
                        }
                        if (isRefresh!!) {
                            datas!!.clear()
                        }
                        datas!!.addAll(data.data!!.list)
                        adapter.setData(datas)
                        isEmpty = datas == null || datas!!.size == 0
                        ErrorUtils.viewProcessing(refreshLayout, llError, llRetry, "无更多文章了", tvErrorMsg, ivError,
                                isEmpty!!, true, R.drawable.pic_3, null)
                    }
                })
    }

    fun finshRequest() {
        refreshLayout.finishRefreshing()
        refreshLayout.finishLoadmore()
        dismissLoading()
    }


    /**
     * 操作收藏/取消收藏
     */
    fun ArticleOperating(articleId: String, status: String) {
        if (!HttpUtil.isConnected(mActivity)) {
            ToastUtils.show(mActivity, "请检查网络链接")
            return
        }
        showLoading()
        RetrofitUtil.getHttpService().articleOperating(userId, articleId, status)
                .compose(RetrofitUtil.CommonOptions<BaseResult>())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onCompleted() {
                    }

                    override fun onError(apiException: ApiException) {
                        dismissLoading()
                        ToastUtils.show(mActivity, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: BaseResult) {
                        getData(true)
                        ToastUtils.show(mActivity, data.message + "", R.color.co1_syr)
                    }
                })
    }


    override fun itemClick(instituteDataBean: InstituteDataBean) {
        var intent = Intent(mActivity, InstituteContentActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra("id", instituteDataBean!!.acticleId.toString())
        intent.putExtra("title", instituteDataBean!!.title.toString())
        startActivity(intent)
    }

    /**
     * 加入收藏
     */
    override fun addCollection(instituteDataBean: InstituteDataBean) {
        ArticleOperating(instituteDataBean.acticleId.toString(), "1")
    }

    /**
     * 取消收藏
     */
    override fun cancelCollection(instituteDataBean: InstituteDataBean) {
        CommonPopupWindow().showPopupWindow(mActivity, "取消收藏", R.color.co11_syr, "继续收藏", R.color.co3_syr,
                object : CommonPopupWindow.ButtonLisenter {
                    override fun btn1Click() {
                        ArticleOperating(instituteDataBean.acticleId.toString(), "2")
                    }

                    override fun btn2Click() {
                    }
                })
    }

}

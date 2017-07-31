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
import com.intfocus.yonghuitest.util.*
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView
import kotlinx.android.synthetic.main.activity_favorite.*
import kotlinx.android.synthetic.main.common_error_view.*

class FavoriteActivity : RefreshActivity(), InstituteAdapter.NoticeItemListener {

    lateinit var adapter: InstituteAdapter
    var datas: MutableList<InstituteDataBean>? = null
    lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorite)
        userId = mActivity!!.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString(URLs.kUserNum, "")
        init()
    }

    fun init() {
        val mLayoutManager = LinearLayoutManager(mActivity)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        my_recycler_view.layoutManager = mLayoutManager
        adapter = InstituteAdapter(mActivity!!, null, this)
        my_recycler_view.adapter = adapter
        var headerView = SinaRefreshView(mActivity)
        headerView.setArrowResource(R.drawable.loading_up)
        var bottomView = LoadingView(mActivity)
        refresh_layout.setHeaderView(headerView)
        refresh_layout.setBottomView(bottomView)
        refresh_layout.setOnRefreshListener(object : RefreshListenerAdapter(), ErrorUtils.ErrorLisenter {
            override fun retry() {
                getData(true)
            }

            override fun onRefresh(refreshLayout: TwinklingRefreshLayout?) {
                super.onRefresh(refreshLayout)
                isRefresh = true
                getData(false)
            }

            override fun onLoadMore(refreshLayout: TwinklingRefreshLayout?) {
                super.onLoadMore(refreshLayout)
                isRefresh = false
                if (page < totalPage) {
                    page += 1
                    getData(false)
                } else {
                    refresh_layout.finishLoadmore()
                    ToastUtils.show(mActivity!!, "无更多文章了")
                }
            }

        })
        getData(true)
    }

    /**
     * 获取数据
     */
    fun getData(isShowDialog: Boolean) {
        if (isRefresh!!) {
            page = 1
        }
        if (!HttpUtil.isConnected(mActivity)) {
            refresh_layout.finishRefreshing()
            refresh_layout.finishLoadmore()
            isEmpty = datas == null || datas!!.size == 0
            ErrorUtils.viewProcessing(refresh_layout, ll_empty, ll_retry, "无更多文章了", tv_errorMsg, iv_error,
                    isEmpty!!, false, R.drawable.pic_3, {
                getData(true)
            })
            ToastUtils.show(mActivity!!, "请检查网络链接")
            return
        }
        if (isShowDialog) {
            if (loadingDialog == null || !loadingDialog!!.isShowing)
                showLoading()
        }
        RetrofitUtil.getHttpService().getArticleList(userId, page.toString(), pageSize.toString())
                .compose(RetrofitUtil.CommonOptions<ArticleResult>())
                .subscribe(object : CodeHandledSubscriber<ArticleResult>() {
                    override fun onCompleted() {
                        dismissLoading()
                        refresh_layout.finishRefreshing()
                        refresh_layout.finishLoadmore()
                    }

                    override fun onError(apiException: ApiException) {
                        dismissLoading()
                        refresh_layout.finishRefreshing()
                        refresh_layout.finishLoadmore()
                        WidgetUtil.showToastShort(mActivity, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: ArticleResult) {
                        refresh_layout.finishRefreshing()
                        refresh_layout.finishLoadmore()
                        totalPage = data.data!!.totalPage
                        if (isRefresh!!) {
                            datas!!.clear()
                        }
                        if (datas == null) {
                            datas = ArrayList()
                        }
                        datas!!.addAll(data.data!!.list)
                        adapter.setData(datas)
                        isEmpty = datas == null || datas!!.size == 0
                        ErrorUtils.viewProcessing(refresh_layout, ll_empty, ll_retry, "无更多文章了", tv_errorMsg, iv_error,
                                isEmpty!!, true, R.drawable.pic_3, null)
                    }
                })
    }


    /**
     * 操作收藏/取消收藏
     */
    fun ArticleOperating(articleId: String, status: String) {
        RetrofitUtil.getHttpService().articleOperating(userId, articleId, status)
                .compose(RetrofitUtil.CommonOptions<BaseResult>())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onCompleted() {
                    }

                    override fun onError(apiException: ApiException) {
                        dismissLoading()
                        WidgetUtil.showToastShort(mActivity, apiException.displayMessage)
                    }

                    override fun onBusinessNext(data: BaseResult) {
                        getData(true)
                        WidgetUtil.showToastShort(mActivity, data.message)
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
     * 取消收藏---这里不会出现
     */
    override fun cancelCollection(instituteDataBean: InstituteDataBean) {
        ArticleOperating(instituteDataBean.acticleId.toString(), "2")
    }

}

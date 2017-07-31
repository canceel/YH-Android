package com.intfocus.yonghuitest.base

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import com.intfocus.yonghuitest.util.LoadingUtils

/**
 * Created by CANC on 2017/7/31.
 */
open class RefreshActivity : BaseActivity() {
    var page: Int = 1
    var pageSize: Int = 10
    var totalPage: Int = 0
    var loadingDialog: Dialog? = null
    var mActivity: Activity? = null
    internal var isRefresh: Boolean? = false
    //是否是刷新
    internal var isEmpty: Boolean? = false
    //数据是否为空

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = this
    }

    protected fun showLoading() {
        loadingDialog = LoadingUtils.createLoadingDialog(mActivity)
        loadingDialog!!.show()
    }

    protected fun dismissLoading() {
        if (loadingDialog != null) {
            loadingDialog!!.dismiss()
        }
    }

}
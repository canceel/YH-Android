package com.intfocus.yonghuitest.mode

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.intfocus.yonghuitest.dashboard.report.mode.ListPageBean
import com.intfocus.yonghuitest.dashboard.report.mode.ReportListPageRequest
import com.intfocus.yonghuitest.data.response.home.ReportListResult
import com.intfocus.yonghuitest.net.ApiException
import com.intfocus.yonghuitest.net.CodeHandledSubscriber
import com.intfocus.yonghuitest.net.RetrofitUtil
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.URLs.kGroupId
import com.intfocus.yonghuitest.util.URLs.kRoleId
import com.zbl.lib.baseframe.core.AbstractMode
import com.zbl.lib.baseframe.utils.StringUtil
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

/**
 * 主页 - 报表 Model
 * Created by liuruilin on 2017/6/15.
 */
class ReportsListMode(ctx: Context) : AbstractMode() {
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)

    override fun requestData() {
        RetrofitUtil.getHttpService().getReportList(mUserSP.getInt(kGroupId, 0).toString(), mUserSP.getInt(kRoleId, 0).toString())
                .compose(RetrofitUtil.CommonOptions<ReportListResult>())
                .subscribe(object : CodeHandledSubscriber<ReportListResult>() {

                    override fun onBusinessNext(data: ReportListResult?) {
                        val result1 = ReportListPageRequest(true, 200)
                        result1.categroy_list = data!!.data
                        EventBus.getDefault().post(result1)
                    }

                    override fun onError(apiException: ApiException?) {
                        val result1 = ReportListPageRequest(false, -1)
                        EventBus.getDefault().post(result1)
                    }

                    override fun onCompleted() {
                    }
                })
    }
}

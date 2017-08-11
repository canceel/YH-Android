package com.intfocus.yonghuitest.dashboard.mine.bean

import com.intfocus.yonghuitest.data.response.mine_page.UserInfoResult

/**
 * Created by liuruilin on 2017/6/7.
 */

class UserInfoRequest(var isSuccess: Boolean, var stateCode: Int) {
    var userInfoBean: UserInfoResult? = null
}

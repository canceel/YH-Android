package com.intfocus.yonghuitest.login.bean

import com.google.gson.annotations.SerializedName
import com.intfocus.yonghuitest.data.response.BaseResult

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/11 上午09:23
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

class NewUser : BaseResult() {

    @SerializedName("data")
    private var mResult: LoginResult? = null

    internal inner class LoginResult {
        /**
         * user_num : 13913778859
         * user_name : 王施君
         * user_pass : c4ca4238a0b923820dcc509a6f75849b
         * email :
         * mobile : 13913778859
         * user_id : 121801
         * status : true
         * gravatar :
         * group_id : 165
         * group_name : 大区(全部)门店(全部)商行(全部)
         * role_id : 99
         * role_name : 胜因团队
         * code : 200
         */

        private val user_num: String? = null
        private val user_name: String? = null
        private val user_pass: String? = null
        private val email: String? = null
        private val mobile: String? = null
        private val user_id: String? = null
        private val status: String? = null
        private val gravatar: String? = null
        private val group_id: String? = null
        private val group_name: String? = null
        private val role_id: String? = null
        private val role_name: String? = null
    }
}

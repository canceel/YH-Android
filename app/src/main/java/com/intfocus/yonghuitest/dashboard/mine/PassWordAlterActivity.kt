package com.intfocus.yonghuitest.dashboard.mine

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseActivity
import com.intfocus.yonghuitest.login.LoginActivity
import com.intfocus.yonghuitest.util.ActionLogUtil
import com.intfocus.yonghuitest.util.ApiHelper
import com.intfocus.yonghuitest.util.ToastUtils
import com.intfocus.yonghuitest.util.URLs
import kotlinx.android.synthetic.main.activity_password_alter.*
import org.json.JSONObject
import java.util.regex.Pattern

/**
 * Created by liuruilin on 2017/6/9.
 */
class PassWordAlterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_alter)
        initListener()
    }

    private fun initListener() {
        btn_pwd_alter_submit.setOnClickListener { submitPassword() }
        rl_pwd_alter_look_new_pwd.setOnClickListener {
            setEditTextInputTypeByCheckBox(et_pwd_alter_new_pwd, cb_pwd_alter_look_new_pwd)
        }
        rl_pwd_alter_look_confirm_new_pwd.setOnClickListener {
            setEditTextInputTypeByCheckBox(et_pwd_alter_confirm_new_pwd, cb_pwd_alter_look_confirm_new_pwd)
        }
    }

    /**
     * 根据CheckBox选中情况更改EditText输入类型
     */
    private fun setEditTextInputTypeByCheckBox(mEditText: EditText, mCheckBox: CheckBox) {
        if (mCheckBox.isChecked) {
            mEditText.transformationMethod = PasswordTransformationMethod.getInstance()
            mCheckBox.isChecked = false
        } else {
            mEditText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            mCheckBox.isChecked = true
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun dismissActivity(v: View) {
        this.onBackPressed()
    }

    fun submitPassword() {
        var oldPassword = et_pwd_alter_old_pwd.text.toString()
        var newPassword = et_pwd_alter_new_pwd.text.toString()
        var confirmNewPassword = et_pwd_alter_confirm_new_pwd.text.toString()

        if (TextUtils.isEmpty(oldPassword)) {
            ToastUtils.show(this, "请输入旧密码")
            return
        }

        if (TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword) || !newPassword.equals(confirmNewPassword)) {
            ToastUtils.show(this, "两次输入密码不一致")
            return
        } else if (newPassword.length < 6) {
            ToastUtils.show(this, "密码不得小于6位")
            return
        } else if (!checkPassword(newPassword)) {
            ToastUtils.show(this, "密码必须为数字与字母的组合")
            return
        }

        if (URLs.MD5(oldPassword) == user.get(URLs.kPassword)) {
            Thread(Runnable {
                val response = ApiHelper.resetPassword(user.get("user_id").toString(), URLs.MD5(newPassword))
                runOnUiThread {
                    if (response[URLs.kCode] == "200" || response[URLs.kCode] == "201") {
                        val alertDialog = AlertDialog.Builder(this@PassWordAlterActivity)
                        alertDialog.setTitle("温馨提示")
                        alertDialog.setMessage("密码修改成功")
                        alertDialog.setPositiveButton("重新登录") { _, _ ->
                            modifiedUserConfig(false)
                            val mEditor = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE).edit()
                            mEditor.putBoolean("ScreenLock", false)
                            mEditor.commit()

                            val intent = Intent()
                            intent.setClass(this@PassWordAlterActivity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        alertDialog.show()
                    } else {
                        ToastUtils.show(this, "密码修改失败")
                    }

                    var logParams = JSONObject()
                    logParams.put(URLs.kAction, "点击/密码修改")
                    ActionLogUtil.actionLog(this, logParams)
                }
            }).start()
        } else {
            ToastUtils.show(this, "旧密码输入有误")
        }
    }

    /**
     * 正则判断密码  至少6位，必须包含数字和字母
     */
    fun checkPassword(str: String): Boolean {
        val regexp = "^(?!\\d+\$)(?![a-zA-Z]+\$)\\w{6,16}"
        val pattern = Pattern.compile(regexp)
        val matcher = pattern.matcher(str)
        return matcher.matches()
    }
}

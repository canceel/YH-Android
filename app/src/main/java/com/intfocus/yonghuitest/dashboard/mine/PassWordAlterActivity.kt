package com.intfocus.yonghuitest.dashboard.mine

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.intfocus.yonghuitest.login.LoginActivity
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseActivity
import com.intfocus.yonghuitest.util.ApiHelper
import com.intfocus.yonghuitest.util.URLs
import com.intfocus.yonghuitest.util.WidgetUtil
import kotlinx.android.synthetic.main.activity_password_alter.*
import org.json.JSONObject

/**
 * Created by liuruilin on 2017/6/9.
 */
class PassWordAlterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_alter)
        rl_commit_password.setOnClickListener { submitPassword() }
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
        var oldPassword = et_old_password.text.toString()
        var newPassword = et_new_password.text.toString()
        var confirmNewPassword = et_confirm_new_password.text.toString()

        if (oldPassword.isEmpty() ) {
            WidgetUtil.showToastLong(this, "请输入旧密码")
            return
        }
        
        if (!newPassword.equals(confirmNewPassword) || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
            WidgetUtil.showToastLong(this, "两次输入密码不一致")
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
                        WidgetUtil.showToastShort(this, "密码修改失败")
                    }

                    var logParams = JSONObject()
                    logParams.put(URLs.kAction, "点击/密码修改")
                    ApiHelper.actionNewThreadLog(this, logParams)
                }
            }).start()
        } else {
            Toast.makeText(this@PassWordAlterActivity, "旧密码输入有误", Toast.LENGTH_SHORT).show()
            Thread(mRunnableForDetecting).start()
        }
    }
}

package com.intfocus.yonghuitest.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import com.intfocus.yonghuitest.screen_lock.ConfirmPassCodeActivity


class LauncherActivity : AppCompatActivity() {
    val ctx = this
    private var mSharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)

        if (mSharedPreferences!!.getBoolean("ScreenLock", false)) {
            intent = Intent(this, ConfirmPassCodeActivity::class.java)
            intent.putExtra("is_from_login", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        } else if (mSharedPreferences!!.getInt("Version", 0) != packageInfo.versionCode) {
            intent = Intent(this, GuideActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        } else {
            intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        }
    }
}

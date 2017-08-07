package com.intfocus.yonghuitest.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.screen_lock.ConfirmPassCodeActivity
import kotlinx.android.synthetic.main.activity_splash.*


class LauncherActivity : Activity(), Animation.AnimationListener {

    val ctx = this
    private var mSharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)
        val animation = AlphaAnimation(1f, 1.0f)
        animation.duration = 2000
        animation.setAnimationListener(this)
        logo.startAnimation(animation)
        mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
        enter()
    }

    override fun onAnimationStart(p0: Animation?) {
    }

    fun enter() {
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        if (mSharedPreferences!!.getBoolean("ScreenLock", false)) {
            intent = Intent(this, ConfirmPassCodeActivity::class.java)
            intent.putExtra("is_from_login", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        } else if (mSharedPreferences!!.getInt("Version", 0) != packageInfo.versionCode) {
            intent = Intent(this, GuidePageActivity::class.java)
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

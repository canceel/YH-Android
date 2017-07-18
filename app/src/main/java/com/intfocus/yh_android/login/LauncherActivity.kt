package com.intfocus.yh_android.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.intfocus.yh_android.R
import com.intfocus.yh_android.login.mode.LaunchMode
import com.intfocus.yh_android.screen_lock.ConfirmPassCodeActivity
import com.zbl.lib.baseframe.core.AbstractActivity
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.activity_launcher.*
import org.xutils.x
import android.location.LocationManager.NETWORK_PROVIDER
import android.location.LocationManager.GPS_PROVIDER
import android.location.LocationProvider
import android.widget.Toast


class LauncherActivity : AppCompatActivity(){
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
        }
        else if (mSharedPreferences!!.getInt("Version", 0) != packageInfo.versionCode) {
            intent = Intent(this, GuidePageActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        }
        else {
            intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        }
    }
}

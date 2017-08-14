package com.intfocus.yonghuitest.setting

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.CompoundButton
import android.widget.Switch

import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseActivity
import com.intfocus.yonghuitest.screen_lock.InitPassCodeActivity
import com.intfocus.yonghuitest.util.FileUtil
import com.intfocus.yonghuitest.util.ToastUtils

/**
 * Created by liuruilin on 2017/3/28.
 */

class SettingPreferenceActivity : BaseActivity() {
    private var mScreenLockSwitch: Switch? = null
    private var mReportCopySwitch: Switch? = null
    private var mLandscapeBannerSwitch: Switch? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_preference)

        mContext = this

        mScreenLockSwitch = findViewById(R.id.switch_screenLock) as Switch
        mReportCopySwitch = findViewById(R.id.switch_report_copy) as Switch
        mLandscapeBannerSwitch = findViewById(R.id.switch_landscape_banner) as Switch

        mScreenLockSwitch!!.setOnCheckedChangeListener(mSwitchScreenLockListener)
        mReportCopySwitch!!.setOnCheckedChangeListener(mSwitchReportCopyListener)
        mLandscapeBannerSwitch!!.setOnCheckedChangeListener(mSwitchBannerListener)
    }

    override fun onResume() {
        super.onResume()
        initSwitchPreference()
    }

    /*
     * Switch 状态初始化
     */
    private fun initSwitchPreference() {
        mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        mScreenLockSwitch!!.isChecked = mSharedPreferences!!.getBoolean("ScreenLock", false)
        mReportCopySwitch!!.isChecked = mSharedPreferences!!.getBoolean("ReportCopy", false)
        mLandscapeBannerSwitch!!.isChecked = mSharedPreferences!!.getBoolean("Landscape", false)
    }

    /*
     *  Switch ScreenLock 开关
     */
    private val mSwitchScreenLockListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (!buttonView.isPressed) {
            return@OnCheckedChangeListener
        }
        if (isChecked) {
            val intent = Intent(this@SettingPreferenceActivity, InitPassCodeActivity::class.java)
            startActivity(intent)
        } else {
            mSharedPreferences!!.edit().putBoolean("ScreenLock", isChecked).commit()
        }
    }

    /*
     *  Switch LandScape Banner 开关
     */
    private val mSwitchBannerListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> mSharedPreferences!!.edit().putBoolean("Landscape", isChecked).commit() }


    /*
     *  Switch Report Copy 开关
     */
    private val mSwitchReportCopyListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> mSharedPreferences!!.edit().putBoolean("ReportCopy", isChecked).commit() }

    /*
     * 清理缓存
     */
    fun clearUserCache(v: View) {
        FileUtil.CacheCleanAsync(mAppContext, "cache-clean").execute()
        var mProgressDialog = ProgressDialog.show(this@SettingPreferenceActivity, "稍等", "正在清理缓存...")
        Handler().postDelayed({ mProgressDialog.dismiss() }, 15000)
    }
}

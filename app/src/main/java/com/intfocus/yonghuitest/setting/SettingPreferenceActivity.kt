package com.intfocus.yonghuitest.setting

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.CompoundButton
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseActivity
import com.intfocus.yonghuitest.screen_lock.InitPassCodeActivity
import com.intfocus.yonghuitest.util.FileUtil
import kotlinx.android.synthetic.main.activity_setting_preference.*

/**
 * Created by liuruilin on 2017/3/28.
 */

class SettingPreferenceActivity : BaseActivity() {
    private var mSharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_preference)

        initSwitchPreference()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        switch_screenLock!!.isChecked = mSharedPreferences!!.getBoolean("ScreenLock", false)
    }

    /*
     * Switch 状态初始化
     */
    private fun initSwitchPreference() {
        mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        switch_screenLock.isChecked = mSharedPreferences!!.getBoolean("ScreenLock", false)
        switch_report_copy.isChecked = mSharedPreferences!!.getBoolean("ReportCopy", false)
        switch_landscape_banner.isChecked = mSharedPreferences!!.getBoolean("Landscape", false)
    }

    private fun initListener() {
        switch_screenLock.setOnCheckedChangeListener(mSwitchScreenLockListener)
        switch_report_copy.setOnCheckedChangeListener(mSwitchReportCopyListener)
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
     *  Switch Report Copy 开关
     */
    private val mSwitchReportCopyListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked -> mSharedPreferences!!.edit().putBoolean("ReportCopy", isChecked).commit() }

    /*
     * 清理缓存
     */
    fun clearUserCache(v: View) {
        var mProgressDialog = ProgressDialog.show(this@SettingPreferenceActivity, "稍等", "正在清理缓存...")
        FileUtil.CacheCleanAsync(mAppContext, "cache-clean").execute()
        Handler().postDelayed({ mProgressDialog.dismiss() }, 15000)
    }
}

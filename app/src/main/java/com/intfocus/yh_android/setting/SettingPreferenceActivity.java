package com.intfocus.yh_android.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.intfocus.yh_android.BaseActivity;
import com.intfocus.yh_android.R;
import com.intfocus.yh_android.screen_lock.InitPassCodeActivity;
import com.intfocus.yh_android.util.FileUtil;
import com.intfocus.yh_android.util.K;
import com.intfocus.yh_android.util.URLs;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by liuruilin on 2017/3/28.
 */

public class SettingPreferenceActivity extends BaseActivity {
    private Switch mScreenLockSwitch, mScreenShotSwitch, mReportCopySwitch, mLandscapeBannerSwitch;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_preference);

        mContext = this;

        mScreenLockSwitch = (Switch) findViewById(R.id.switch_screenLock);
        mScreenShotSwitch = (Switch) findViewById(R.id.switch_screenshot);
        mReportCopySwitch = (Switch) findViewById(R.id.switch_report_copy);
        mLandscapeBannerSwitch = (Switch) findViewById(R.id.switch_landscape_banner);

        mScreenLockSwitch.setOnCheckedChangeListener(mSwitchScreenLockListener);
        mScreenShotSwitch.setOnCheckedChangeListener(mSwitchScreenShotListener);
        mReportCopySwitch.setOnCheckedChangeListener(mSwitchReportCopyListener);
        mLandscapeBannerSwitch.setOnCheckedChangeListener(mSwitchBannerListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSwitchPreference();
    }

    /*
         * Switch 状态初始化
         */
    private void initSwitchPreference() {
        mSharedPreferences = getSharedPreferences("SettingPreference", MODE_PRIVATE);
        mScreenLockSwitch.setChecked(mSharedPreferences.getBoolean("ScreenLock", false));
        mScreenShotSwitch.setChecked(mSharedPreferences.getBoolean("ScreenShort", true));
        mReportCopySwitch.setChecked(mSharedPreferences.getBoolean("ReportCopy", false));
        mLandscapeBannerSwitch.setChecked(mSharedPreferences.getBoolean("Landscape", false));
    }

    /*
     *  Switch ScreenLock 开关
     */
    private final CompoundButton.OnCheckedChangeListener mSwitchScreenLockListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed()) {
                return;
            }
            if (isChecked) {
                Intent intent = new Intent(SettingPreferenceActivity.this, InitPassCodeActivity.class);
                startActivity(intent);
                // 开启锁屏设置
            }
            else {
                SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                mEditor.putBoolean("ScreenLock", isChecked);
                mEditor.commit();
            }
        }
    };

    /*
     *  Switch LandScape Banner 开关
     */
    private final CompoundButton.OnCheckedChangeListener mSwitchBannerListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putBoolean("Landscape", isChecked);
            mEditor.commit();
        }
    };

    /*
     *  Switch ScreenShot 开关
     */
    private final CompoundButton.OnCheckedChangeListener mSwitchScreenShotListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putBoolean("ScreenShot", isChecked);
            mEditor.commit();
        }
    };

    /*
     *  Switch Report Copy 开关
     */
    private final CompoundButton.OnCheckedChangeListener mSwitchReportCopyListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putBoolean("ReportCopy", isChecked);
            mEditor.commit();
        }
    };

    /*
     * 清理缓存
     */
    public void clearUserCache(View v) {
        String userspace = FileUtil.userspace(mContext);
        new File(userspace).delete();
        Toast.makeText(mContext, "缓存已清理", Toast.LENGTH_SHORT).show();
    }
}

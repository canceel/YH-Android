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
import com.intfocus.yonghuitest.data.response.assets.AssetsMD5
import com.intfocus.yonghuitest.data.response.assets.AssetsResult
import com.intfocus.yonghuitest.net.ApiException
import com.intfocus.yonghuitest.net.CodeHandledSubscriber
import com.intfocus.yonghuitest.net.RetrofitUtil
import com.intfocus.yonghuitest.screen_lock.ConfirmPassCodeActivity
import com.intfocus.yonghuitest.util.HttpUtil
import com.intfocus.yonghuitest.util.URLs
import kotlinx.android.synthetic.main.activity_splash.*


class LauncherActivity : Activity(), Animation.AnimationListener {

    val ctx = this
    lateinit var mSettingSP: SharedPreferences
    lateinit var mAssetsSP: SharedPreferences
    lateinit var mAssetsSPEdit: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)
        val animation = AlphaAnimation(1f, 1.0f)
        animation.duration = 2000
        animation.setAnimationListener(this)
        mSettingSP = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        mAssetsSP = getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        mAssetsSPEdit = mAssetsSP.edit()
        logo.startAnimation(animation)
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
        if (mSettingSP!!.getBoolean("ScreenLock", false)) {
            checkAssetsIsUpdata(ctx)
            intent = Intent(this, ConfirmPassCodeActivity::class.java)
            intent.putExtra("is_from_login", true)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        } else if (mSettingSP!!.getInt("Version", 0) != packageInfo.versionCode) {
            intent = Intent(this, GuideActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        } else {
            checkAssetsIsUpdata(ctx)
            intent = Intent(this, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
            this.startActivity(intent)

            finish()
        }
    }

    fun checkAssetsIsUpdata(ctx: Context) {
        if (!HttpUtil.isConnected(ctx)) {
            return
        }
        RetrofitUtil.getHttpService().assetsMD5
                .compose(RetrofitUtil.CommonOptions<AssetsResult>())
                .subscribe(object : CodeHandledSubscriber<AssetsResult>() {
                    override fun onError(apiException: ApiException?) {

                    }

                    override fun onCompleted() {
                        enter()
                    }

                    override fun onBusinessNext(data: AssetsResult?) {
                        var assetsMD5s = data!!.data!!
                        /*
                         * assets_md5 : ca94578b33a0d620de4caad6bba41fbd
                         * loading_md5 : 8bd5c6a91d38848d3160e6c8a462b852
                         * fonts_md5 : 5901960c857600316c3d141401c3af08
                         * icons_md5 : 7afa625cca643d01a6b12d80a19d4756
                         * images_md5 : 65266455bea40469dcb9f022f63ce769
                         * javascripts_md5 : 9a072008dfd547026c5828cd65d3e973
                         * stylesheets_md5 : 4b5b98d9ad460a67e0943805e2be17c9
                         * advertisement_md5 : 0239802a086466ec31d566ca910da0c9
                         */
                        mAssetsSPEdit.putString("assets_md5", assetsMD5s.assets_md5).commit()
                        mAssetsSPEdit.putString("loading_md5", assetsMD5s.loading_md5).commit()
                        mAssetsSPEdit.putString("fonts_md5", assetsMD5s.fonts_md5).commit()
                        mAssetsSPEdit.putString("images_md5", assetsMD5s.images_md5).commit()
                        mAssetsSPEdit.putString("icons_md5", assetsMD5s.icons_md5).commit()
                        mAssetsSPEdit.putString("javascripts_md5", assetsMD5s.javascripts_md5).commit()
                        mAssetsSPEdit.putString("stylesheets_md5", assetsMD5s.stylesheets_md5).commit()
                        HttpUtil.checkAssetsUpdated(ctx, "launch")
                    }
                })
    }
}

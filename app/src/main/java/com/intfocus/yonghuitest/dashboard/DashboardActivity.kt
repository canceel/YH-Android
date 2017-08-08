package com.intfocus.yonghuitest.dashboard

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.text.TextUtils
import android.util.Log
import android.view.View
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.YHApplication
import com.intfocus.yonghuitest.bean.DashboardItemBean
import com.intfocus.yonghuitest.bean.User
import com.intfocus.yonghuitest.dashboard.mine.PassWordAlterActivity
import com.intfocus.yonghuitest.dashboard.mine.bean.PushMessageBean
import com.intfocus.yonghuitest.db.OrmDBHelper
import com.intfocus.yonghuitest.scanner.BarCodeScannerActivity
import com.intfocus.yonghuitest.subject.HomeTricsActivity
import com.intfocus.yonghuitest.subject.SubjectActivity
import com.intfocus.yonghuitest.subject.TableActivity
import com.intfocus.yonghuitest.subject.WebApplicationActivity
import com.intfocus.yonghuitest.subject.template_v2.ModularTwo_Mode_Activity
import com.intfocus.yonghuitest.util.*
import com.intfocus.yonghuitest.view.NoScrollViewPager
import com.intfocus.yonghuitest.view.TabView
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import sumimakito.android.advtextswitcher.AdvTextSwitcher
import java.io.File
import java.io.IOException
import java.sql.SQLException

class DashboardActivity : FragmentActivity(), ViewPager.OnPageChangeListener, AdvTextSwitcher.Callback {
    private var mDashboardFragmentAdapter: DashboardFragmentAdapter? = null
    private var mSharedPreferences: SharedPreferences? = null
    private var mTabView: Array<TabView>? = null
    private var user: User? = null
    private var userID: Int = 0
    private var mApp: YHApplication? = null
    private var mViewPager: NoScrollViewPager? = null
    private var mTabKPI: TabView? = null
    private var mTabAnalysis: TabView? = null
    private var mTabAPP: TabView? = null
    private var mTabMessage: TabView? = null
    private var mContext: Context? = null
    private var mAppContext: Context? = null
    private var mGson: Gson? = null
    lateinit var mUserSP: SharedPreferences

    private var objectTypeName = arrayOf("生意概况", "报表", "工具箱")

    companion object {
        val PAGE_KPI = 0
        val PAGE_REPORTS = 1
        val PAGE_APP = 2
        val PAGE_MINE = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        EventBus.getDefault().register(this)
        mApp = this.application as YHApplication
        mAppContext = mApp!!.appContext
        mContext = this
        mGson = Gson()
        initUser()
        mSharedPreferences = getSharedPreferences("DashboardPreferences", Context.MODE_PRIVATE)
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        mDashboardFragmentAdapter = DashboardFragmentAdapter(supportFragmentManager)
        mViewPager = findViewById(R.id.content_view) as NoScrollViewPager
        initTabView()
        initViewPaper(mDashboardFragmentAdapter!!)
        checkUserModifiedInitPassword() // 检测用户密码
        checkPgyerVersionUpgrade(this@DashboardActivity, false)

        var intent = intent
        if (intent.hasExtra("msgData")) {
            handlePushMessage(intent.getBundleExtra("msgData").getString("message"))

        } else {
            HttpUtil.checkAssetsUpdated(mContext)
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    /*
     * 推送消息处理
     */
    fun handlePushMessage(message: String) {
        Log.i("testlog", message)
        var pushMessage = mGson!!.fromJson(message, PushMessageBean::class.java)
        pushMessage.body_title = intent.getBundleExtra("msgData").getString("message_body_title")
        pushMessage.body_text = intent.getBundleExtra("msgData").getString("message_body_text")
        pushMessage.new_msg = true
        pushMessage.user_id = userID
        var personDao = OrmDBHelper.getInstance(this).pushMessageDao
        //  RxJava异步存储推送过来的数据
        Observable.create(Observable.OnSubscribe <PushMessageBean> {
            try {
                personDao.createIfNotExists(pushMessage)
            } catch(e: SQLException) {
                e.printStackTrace()
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { }
        // RxBus通知消息界面 ShowPushMessageActivity 更新数据
        RxBusUtil.getInstance().post("UpDatePushMessage")

        when (pushMessage.type) {
            "report" -> pageLink(pushMessage.title + "", pushMessage.url + "", 1, 1)
            "analyse" -> {
                mViewPager!!.currentItem = PAGE_REPORTS
                mTabView!![mViewPager!!.currentItem].setActive(true)
            }
            "app" -> {
                mViewPager!!.currentItem = PAGE_REPORTS
                mTabView!![mViewPager!!.currentItem].setActive(true)
            }
            "message" -> {
                mViewPager!!.currentItem = PAGE_MINE
                mTabView!![mViewPager!!.currentItem].setActive(true)
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("温馨提示")
                .setMessage(String.format("确认退出【%s】？", resources.getString(R.string.app_name)))
                .setPositiveButton("确认") { _, _ ->
                    mApp!!.setCurrentActivity(null)
                    finish()
                    System.exit(0)
                }
                .setNegativeButton("取消") { _, _ ->
                    // 返回DashboardActivity
                }
        builder.show()
    }

    /*
     * 初始化用户信息
     */
    private fun initUser() {
        val userConfigPath = String.format("%s/%s", FileUtil.basePath(mAppContext), K.kUserConfigFileName)
        if (File(userConfigPath).exists()) {
            user = mGson!!.fromJson(FileUtil.readConfigFile(userConfigPath).toString(), User::class.java)
            if (user!!.isIs_login) {
                userID = user!!.user_id
            }
        }
    }

    fun startBarCodeActivity(v: View) {
        if (ContextCompat.checkSelfPermission(this@DashboardActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            val builder = AlertDialog.Builder(this@DashboardActivity)
            builder.setTitle("温馨提示")
                    .setMessage("相机权限获取失败，是否到本应用的设置界面设置权限")
                    .setPositiveButton("确认") { _, _ -> goToAppSetting() }
                    .setNegativeButton("取消") { _, _ ->
                        // 返回DashboardActivity
                    }
            builder.show()
            return
        } else if (user!!.store_ids == null || user!!.store_ids.size == 0) {
            val builder = AlertDialog.Builder(this@DashboardActivity)
            builder.setTitle("温馨提示")
                    .setMessage("抱歉, 您没有扫码权限")
                    .setPositiveButton("确认") { dialog, which -> }
            builder.show()
            return
        } else {
            val barCodeScannerIntent = Intent(mContext, BarCodeScannerActivity::class.java)
            mContext!!.startActivity(barCodeScannerIntent)

            var logParams = JSONObject()
            logParams.put(URLs.kAction, "点击/扫一扫")
            ActionLogUtil.actionLog(mAppContext, logParams)
        }
    }

    /*
     * 跳转系统设置页面
     */
    private fun goToAppSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun initTabView() {
        mTabKPI = findViewById(R.id.tab_kpi) as TabView
        mTabAnalysis = findViewById(R.id.tab_analysis) as TabView
        mTabAPP = findViewById(R.id.tab_app) as TabView
        mTabMessage = findViewById(R.id.tab_message) as TabView
        mTabView = arrayOf<TabView>(mTabKPI!!, mTabAnalysis!!, mTabAPP!!, mTabMessage!!)

        mTabKPI!!.setOnClickListener(mTabChangeListener)
        mTabAnalysis!!.setOnClickListener(mTabChangeListener)
        mTabAPP!!.setOnClickListener(mTabChangeListener)
        mTabMessage!!.setOnClickListener(mTabChangeListener)
    }

    /**
     * @param dashboardFragmentAdapter
     */
    private fun initViewPaper(dashboardFragmentAdapter: DashboardFragmentAdapter) {
        mViewPager!!.adapter = dashboardFragmentAdapter
        mViewPager!!.offscreenPageLimit = 4
        mViewPager!!.currentItem = mSharedPreferences!!.getInt("LastTab", 0)
        mTabView!![mViewPager!!.currentItem].setActive(true)
        mViewPager!!.addOnPageChangeListener(this)
    }

    /*
     * Tab 栏按钮监听事件
     */
    private val mTabChangeListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.tab_kpi -> mViewPager!!.currentItem = PAGE_KPI
            R.id.tab_analysis -> mViewPager!!.currentItem = PAGE_REPORTS
            R.id.tab_app -> mViewPager!!.currentItem = PAGE_APP
            R.id.tab_message -> mViewPager!!.currentItem = PAGE_MINE
            else -> {
            }
        }
        refreshTabView()
    }

    //重写ViewPager页面切换的处理方法
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {
        if (state == 2) {
            when (mViewPager!!.currentItem) {
                PAGE_KPI -> mTabKPI!!.setActive(true)

                PAGE_REPORTS -> mTabAnalysis!!.setActive(true)

                PAGE_APP -> mTabAPP!!.setActive(true)

                PAGE_MINE -> mTabMessage!!.setActive(true)
            }
        }
        refreshTabView()
        mSharedPreferences!!.edit().putInt("LastTab", mViewPager!!.currentItem).commit()
    }

    /*
     * 公告点击事件
     */
    override fun onItemClick(position: Int) {
        mViewPager!!.currentItem = PAGE_MINE
        refreshTabView()
    }

    /*
     * 刷新 TabView 高亮状态
     */
    private fun refreshTabView() {
        mTabView!![mViewPager!!.currentItem].setActive(true)
        for (i in mTabView!!.indices) {
            if (i != mViewPager!!.currentItem) {
                mTabView!![i].setActive(false)
            }
        }
    }

    /*
     * 用户编号
     */
    fun checkUserModifiedInitPassword() {
        if (user!!.password != URLs.MD5(K.kInitPassword)) {
            return
        }
        val alertDialog = AlertDialog.Builder(this@DashboardActivity)
        alertDialog.setTitle("温馨提示")
        alertDialog.setMessage("安全起见，请在【个人信息】-【基本信息】-【修改登录密码】页面修改初始密码")

        alertDialog.setPositiveButton("立即前往") { _, _ ->
            val intent = Intent(this@DashboardActivity, PassWordAlterActivity::class.java)
            startActivity(intent)
        }.setNegativeButton("稍后修改") { _, _ ->
            // 返回DashboardActivity
        }
        alertDialog.show()
    }

    /*
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(items: DashboardItemBean?) {
        if (items != null) {
            val link = items.link
            val bannerName = items.bannerName + ""
            val objectId = items.objectId
            val objectType = items.objectType
            pageLink(bannerName, link, objectId, objectType)
        } else {
            ToastUtils.show(this, "没有指定链接")
        }
    }

    /*
     * 页面跳转事件
     */
    fun pageLink(mBannerName: String, link: String, objectId: Int, objectType: Int) {
        if (link.indexOf("template") > 0 && link.indexOf("group") > 0) {
            try {
                val groupID = getSharedPreferences("UserBean", Context.MODE_PRIVATE).getInt(URLs.kGroupId, 0)
                val reportID = TextUtils.split(link, "report/")[1].split("/")[0]
                var urlString: String
                val intent: Intent

                when {
                    link.indexOf("template/2") > 0 -> {
                        intent = Intent(this, SubjectActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra(URLs.kBannerName, mBannerName)
                        intent.putExtra(URLs.kLink, link)
                        intent.putExtra(URLs.kObjectId, objectId)
                        intent.putExtra(URLs.kObjectType, objectType)
                        intent.putExtra("groupID", groupID)
                        intent.putExtra("reportID", reportID)
                        startActivity(intent)
                    }
                    link.indexOf("template/4") > 0 -> {
                        intent = Intent(this, SubjectActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra(URLs.kBannerName, mBannerName)
                        intent.putExtra(URLs.kLink, link)
                        intent.putExtra(URLs.kObjectId, objectId)
                        intent.putExtra(URLs.kObjectType, objectType)
                        intent.putExtra("groupID", groupID)
                        intent.putExtra("reportID", reportID)
                        startActivity(intent)
                    }
                    link.indexOf("template/3") > 0 -> {
                        intent = Intent(this, HomeTricsActivity::class.java)
                        urlString = String.format("%s/api/v1/group/%d/template/%s/report/%s/json",
                                K.kBaseUrl, groupID, "3", reportID)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra(URLs.kBannerName, mBannerName)
                        intent.putExtra(URLs.kObjectId, objectId)
                        intent.putExtra(URLs.kObjectType, objectType)
                        intent.putExtra("groupID", groupID)
                        intent.putExtra("reportID", reportID)
                        intent.putExtra("urlString", urlString)
                        startActivity(intent)
                    }
                    link.indexOf("template/5") > 0 -> {
                        intent = Intent(this, TableActivity::class.java)
                        urlString = String.format("%s/api/v1/group/%d/template/%s/report/%s/json",
                                K.kBaseUrl, groupID, "5", reportID)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra(URLs.kBannerName, mBannerName)
                        intent.putExtra(URLs.kObjectId, objectId)
                        intent.putExtra(URLs.kObjectType, objectType)
                        intent.putExtra("groupID", groupID)
                        intent.putExtra("reportID", reportID)
                        intent.putExtra("urlString", urlString)
                        startActivity(intent)
                    }
                    link.indexOf("template/1") > 0 -> {
                        val intent = Intent(this, ModularTwo_Mode_Activity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                        intent.putExtra(URLs.kBannerName, mBannerName)
                        intent.putExtra(URLs.kObjectId, objectId)
                        intent.putExtra(URLs.kObjectType, objectType)
                        intent.putExtra("groupID", groupID)
                        intent.putExtra("reportID", reportID)
                        intent.putExtra(URLs.kLink, link)
                        startActivity(intent)
                    }
                    else -> showTemplateErrorDialog()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            var logParams = JSONObject()
            logParams.put(URLs.kAction, "点击/" + objectTypeName[objectType - 1] + "/报表")
            logParams.put(URLs.kObjTitle, mBannerName)
            ActionLogUtil.actionLog(mAppContext, logParams)
        } else {
            val intent = Intent(this, WebApplicationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            intent.putExtra(URLs.kBannerName, mBannerName)
            intent.putExtra(URLs.kLink, link)
            intent.putExtra(URLs.kObjectId, objectId)
            intent.putExtra(URLs.kObjectType, objectType)
            startActivity(intent)

            var logParams = JSONObject()
            logParams.put(URLs.kAction, "点击/生意概况/链接")
            logParams.put(URLs.kObjTitle, mBannerName)
            ActionLogUtil.actionLog(mAppContext, logParams)
        }
    }

    internal fun showTemplateErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("温馨提示")
                .setMessage("当前版本暂不支持该模板, 请升级应用后查看")
                .setPositiveButton("前去升级") { _, _ ->
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(K.kPgyerUrl))
                    startActivity(browserIntent)
                }
                .setNegativeButton("稍后升级") { _, _ ->
                    // 返回 LoginActivity
                }
        builder.show()
    }

    /*
     * 托管在蒲公英平台，对比版本号检测是否版本更新
     * 对比 build 值，只准正向安装提示
     * 奇数: 测试版本，仅提示
     * 偶数: 正式版本，点击安装更新
     */
    fun checkPgyerVersionUpgrade(activity: Activity, isShowToast: Boolean) {
        PgyUpdateManager.register(activity, "com.intfocus.yonghuitest.fileprovider", object : UpdateManagerListener() {
            override fun onUpdateAvailable(result: String?) {
                try {
                    val appBean = UpdateManagerListener.getAppBeanFromString(result)

                    if (result == null || result.isEmpty()) {
                        return
                    }

                    val packageInfo = packageManager.getPackageInfo(packageName, 0)
                    val currentVersionCode = packageInfo.versionCode

                    val response = JSONObject(result)
                    val message = response.getString("message")

                    val responseVersionJSON = response.getJSONObject(URLs.kData)
                    val newVersionCode = responseVersionJSON.getInt("versionCode")

                    if (currentVersionCode >= newVersionCode) {
                        return
                    }

                    val pgyerVersionPath = String.format("%s/%s", FileUtil.basePath(mAppContext), K.kPgyerVersionConfigFileName)
                    FileUtil.writeFile(pgyerVersionPath, result)

                    if (newVersionCode % 2 == 1) {
                        return
                    } else if (HttpUtil.isWifi(activity) && newVersionCode % 10 == 8) {
                        UpdateManagerListener.startDownloadTask(activity, appBean.downloadURL)
                        return
                    }

                    AlertDialog.Builder(activity)
                            .setTitle("版本更新")
                            .setMessage(if (message.isEmpty()) "无升级简介" else message)
                            .setPositiveButton(
                                    "确定"
                            ) { _, _ -> UpdateManagerListener.startDownloadTask(activity, appBean.downloadURL) }
                            .setNegativeButton("下一次"
                            ) { dialog, _ -> dialog.dismiss() }
                            .setCancelable(false)
                            .show()

                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun onNoUpdateAvailable() {
            }
        })
    }
}

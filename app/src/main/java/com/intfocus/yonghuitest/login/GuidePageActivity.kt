package com.intfocus.yonghuitest.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseActivity
import com.intfocus.yonghuitest.login.adapter.GuidePageAdapter


class GuidePageActivity : BaseActivity() {
    private var mSharedPreferences: SharedPreferences? = null
    var imageViews: MutableList<ImageView> = mutableListOf()
    var imageIDList: MutableList<String> = mutableListOf()
    var viewPager: ViewPager? = null
    var currentItem: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 使背景填满整个屏幕,包括状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        setContentView(R.layout.activity_guide_page)

        mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)

        imageIDList.add("assets://guide_page_1.png")
        imageIDList.add("assets://guide_page_2.png")
        imageIDList.add("assets://guide_page_3.png")
        imageIDList.add("assets://guide_page_4.png")

        for (i in 0..imageIDList.size - 1) {
            imageViews.add(ImageView(this))
        }

        viewPager = findViewById(R.id.vp_guide_pager) as ViewPager
        viewPager!!.adapter = GuidePageAdapter(this, imageViews, imageIDList)
        viewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                currentItem = position
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        viewPager!!.setOnTouchListener(object : View.OnTouchListener {
            internal var startX: Float = 0.toFloat()
            internal var startY: Float = 0.toFloat()
            internal var endX: Float = 0.toFloat()
            internal var endY: Float = 0.toFloat()
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        startX = event.x
                        startY = event.y
                    }
                    MotionEvent.ACTION_UP -> {
                        endX = event.x
                        endY = event.y
                        val windowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                        //获取屏幕的宽度
                        val size = Point()
                        windowManager.defaultDisplay.getSize(size)
                        val width = size.x
                        //首先要确定的是，是否到了最后一页，然后判断是否向左滑动，并且滑动距离是否符合，我这里的判断距离是屏幕宽度的4分之一（这里可以适当控制）
                        if (currentItem === imageViews.size - 1 && startX - endX > 0 && startX - endX >= width / 4) {
                            startLoginActivity()
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_in_left)
                        }
                    }
                }
                return false
            }
        })
    }

    fun startLoginActivity() {
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        mSharedPreferences!!.edit().putInt("Version", packageInfo.versionCode).commit()
        var intent =  Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

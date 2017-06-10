package com.intfocus.yonghuitest.dashboard.message

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.adapter.MessageViewPagerAdapter
import com.intfocus.yonghuitest.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_message.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class MessageFragment : BaseFragment(), ViewPager.OnPageChangeListener {
    lateinit var mViewPagerAdapter: MessageViewPagerAdapter
    var fragmentList = ArrayList<Fragment>()
    val titleList = ArrayList<String>()
    val FIRST_PAGE_INDEX: Int = 0

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_message, container, false)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViewPager()
    }

    fun initViewPager() {
        fragmentList.add(NoticeFragment())
//        fragmentList.add(UserFragment())
        fragmentList.add(UserFragment())

        titleList.add("公告预警")
//        titleList.add("数据学院")
        titleList.add("个人信息")

        mViewPagerAdapter = MessageViewPagerAdapter(childFragmentManager, fragmentList, titleList)
        mViewPagerAdapter.switchTo(FIRST_PAGE_INDEX)

        vp_message.adapter = mViewPagerAdapter
        vp_message.addOnPageChangeListener(this)
        vp_message.currentItem = 0
//        vp_message.offscreenPageLimit = 3

        vp_tablayout.setViewPager(vp_message)
    }

    //重写ViewPager页面切换的处理方法
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {
        mViewPagerAdapter.switchTo(position)
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setTabUpdatedState() {

    }
}


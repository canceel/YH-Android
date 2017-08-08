package com.intfocus.yonghuitest.filter

import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.amap.api.location.AMapLocation
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.RefreshActivity
import com.intfocus.yonghuitest.dashboard.mine.adapter.FilterMenuAdapter
import com.intfocus.yonghuitest.data.response.filter.MenuItem
import com.intfocus.yonghuitest.data.response.filter.MenuResult
import com.intfocus.yonghuitest.net.ApiException
import com.intfocus.yonghuitest.net.CodeHandledSubscriber
import com.intfocus.yonghuitest.net.RetrofitUtil
import com.intfocus.yonghuitest.service.LocationService
import com.intfocus.yonghuitest.util.ToastUtils
import com.intfocus.yonghuitest.view.addressselector.AddressPopupWindow
import com.intfocus.yonghuitest.view.addressselector.FilterPopupWindow
import com.lcodecore.tkrefreshlayout.footer.LoadingView
import com.lcodecore.tkrefreshlayout.header.SinaRefreshView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by CANC on 2017/8/3.
 */
class FilterActivity : RefreshActivity(), AddressPopupWindow.AddressLisenter, FilterMenuAdapter.FilterMenuListener, FilterPopupWindow.MenuLisenter {
    /**
     * 地址选择
     */
    lateinit var addressPopupWindow: AddressPopupWindow
    lateinit var locationDatas: ArrayList<MenuItem>
    /**
     * 菜单
     */
    var currentPosition: Int? = 0//当前展开的menu
    lateinit var menuDatas: ArrayList<MenuItem>
    lateinit var menuAdpter: FilterMenuAdapter
    var filterPopupWindow: FilterPopupWindow? = null
    lateinit var viewLine: View
    lateinit var viewBg: View

    lateinit var filterRecyclerView: RecyclerView
    lateinit var tvAddressFilter: TextView
    lateinit var tvLocationAddress: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        setRefreshLayout()
        EventBus.getDefault().register(this)
        initView()
        startService(Intent(this, LocationService::class.java))
        getMenuData()
    }

    fun initView() {
        locationDatas = ArrayList()
        menuDatas = ArrayList()
        viewLine = findViewById(R.id.view_line)
        viewBg = findViewById(R.id.view_bg)
        tvAddressFilter = findViewById(R.id.tv_address_filter) as TextView
        tvLocationAddress = findViewById(R.id.tv_location_address) as TextView
        filterRecyclerView = findViewById(R.id.filter_recycler_view) as RecyclerView


        val mLayoutManager = LinearLayoutManager(mActivity)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        filterRecyclerView.layoutManager = mLayoutManager
        menuAdpter = FilterMenuAdapter(mActivity, menuDatas, this)
        filterRecyclerView.adapter = menuAdpter

        var headerView = SinaRefreshView(mActivity)
        headerView.setArrowResource(R.drawable.loading_up)
        var bottomView = LoadingView(mActivity)
        refreshLayout.setHeaderView(headerView)
        refreshLayout.setBottomView(bottomView)

        tvAddressFilter.setOnClickListener { showAddressPop() }
    }

    fun initAddressPopup() {
        addressPopupWindow = AddressPopupWindow(this, locationDatas, this)
        addressPopupWindow.init()
    }

    private fun showAddressPop() {
        if (addressPopupWindow == null) {
            initAddressPopup()
        }
        val params = mActivity.window.attributes
        params.alpha = 0.7f
        mActivity.window.attributes = params
        addressPopupWindow.animationStyle = R.style.anim_popup_bottombar
        addressPopupWindow.showAtLocation(tvAddressFilter, Gravity.BOTTOM, 0, 0)
        tvAddressFilter.setTextColor(ContextCompat.getColor(mActivity, R.color.co1_syr))
        addressPopupWindow.setOnDismissListener {
            tvAddressFilter.setTextColor(ContextCompat.getColor(mActivity, R.color.co6_syr))
            val paramsa = mActivity.window.attributes
            paramsa.alpha = 1f
            mActivity.window.attributes = paramsa
        }
    }

    fun initMenuPopup(position: Int) {
        filterPopupWindow = FilterPopupWindow(this, menuDatas!![position].data!!, this)
        filterPopupWindow!!.init()
    }

    private fun showMenuPop(position: Int) {
        if (filterPopupWindow == null) {
            initMenuPopup(position)
        } else {
            filterPopupWindow!!.upDateDatas(menuDatas!![position].data!!)
        }
        viewBg.visibility = View.VISIBLE
        filterPopupWindow!!.showAsDropDown(viewLine)
        filterPopupWindow!!.setOnDismissListener {
            for (menu in menuDatas) {
                menu.arrorDirection = false
            }
            menuAdpter.setData(menuDatas)
            viewBg.visibility = View.GONE
        }
    }


    /**
     * 获取筛选菜单数据
     */
    fun getMenuData() {
        RetrofitUtil.getHttpService().filterMenu
                .compose(RetrofitUtil.CommonOptions<MenuResult>())
                .subscribe(object : CodeHandledSubscriber<MenuResult>() {
                    override fun onError(apiException: ApiException?) {
                        ToastUtils.show(mActivity, apiException!!.displayMessage, R.color.co11_syr)
                    }

                    override fun onCompleted() {
                    }

                    override fun onBusinessNext(data: MenuResult?) {
                        for (menu in data!!.data) {
                            if ("location".equals(menu.type)) {
                                locationDatas = menu.data!!
                                tvLocationAddress.text = menu.current_location!!.display
                            }
                            if ("faster_select".equals(menu.type)) {
                                menuDatas = menu.data!!
                            }
                            menuAdpter.setData(menuDatas)
                            initAddressPopup()
                        }
                    }
                })
    }

    override fun getData(isShowDialog: Boolean) {

    }

    /**
     * 地址选择完成
     */
    override fun complete(firstData: MenuItem, seconedData: MenuItem, thirdData: MenuItem) {
        tvLocationAddress.text = firstData.name + "|" + seconedData.name + "|" + thirdData.name
        addressPopupWindow.dismiss()
    }

    /**
     * 点击普通筛选栏
     */
    override fun itemClick(position: Int) {
        //标记点击位置
        menuDatas!![position].arrorDirection = true
        menuAdpter.setData(menuDatas)
        currentPosition = position
        showMenuPop(position)
    }


    override fun menuItemClick(position: Int) {
        for (menuItem in menuDatas!![currentPosition!!].data!!) {
            menuItem.arrorDirection = false
        }
        //标记点击位置
        menuDatas!![currentPosition!!].data!![position].arrorDirection = true
        filterPopupWindow!!.dismiss()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun Location(location: AMapLocation) {
        if (location != null && location.errorCode == 0) {
            tvLocationAddress.text = location.address
        } else {
            tvLocationAddress.text = "定位失败"
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        startService(Intent(this, LocationService::class.java))
        super.onDestroy()
    }
}
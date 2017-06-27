package com.intfocus.yonghuitest.dashboard.kpi.adapter

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.ViewUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.intfocus.yonghuitest.dashboard.kpi.NumberThreeFragment
import com.intfocus.yonghuitest.dashboard.kpi.NumberTwoFragment
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.yonghuitest.util.Utils
import com.zbl.lib.baseframe.utils.PhoneUtil
import java.util.*

/**
 * Created by liuruilin on 2017/6/22.
 */
class KpiGroupItemAdapter(ctx: Context, internal var fm: FragmentManager, internal var itemDatas: List<KpiGroupItem>?) : RecyclerView.Adapter<KpiGroupItemAdapter.KpiGroupItemHolder>() {

    internal var ctx: Context? = null

    internal var viewWidth: Int = 0
    internal var viewHeigt: Int = 0
    internal var view2width: Int = 0

    init {
        val sw = PhoneUtil.getScreenWidth(ctx)
        view2width = PhoneUtil.dip2px(ctx, 350.toFloat())
        viewWidth = sw/2
        viewHeigt = (viewWidth * 0.8).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KpiGroupItemHolder {
        val layout = FrameLayout(parent.context)
        val random = Random()
        val id = random.nextInt(201505)
        layout.id = id
        val holder = KpiGroupItemHolder(layout, viewType)
        return holder
    }

    override fun onBindViewHolder(holder: KpiGroupItemHolder, position: Int) {

        var lparams: ViewGroup.LayoutParams
        when (holder.datas.dashboard_type) {
            "number2" -> lparams = ViewGroup.LayoutParams(viewWidth, viewHeigt)
            "number3" -> lparams = ViewGroup.LayoutParams(view2width, (viewHeigt * 0.4).toInt())
            else -> lparams = ViewGroup.LayoutParams(viewWidth, viewHeigt)
        }
        holder.layout.layoutParams = lparams
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        if (itemDatas == null)
            return 0
        return itemDatas!!.size
    }

    inner class KpiGroupItemHolder(view: View, viewType: Int) : RecyclerView.ViewHolder(view) {
        var layout: FrameLayout
        var datas: KpiGroupItem

        init {
            this.layout = view as FrameLayout
            datas = itemDatas!![viewType]
            var fragment: Fragment
            when (datas.dashboard_type) {
                "number2" -> fragment = NumberTwoFragment.newInstance(datas)
                "number3" -> fragment = NumberThreeFragment.newInstance(datas)
                else -> fragment = NumberThreeFragment.newInstance(datas)
            }
            val ft = fm.beginTransaction()
            ft.replace(view.getId(), fragment)
            ft.commit()
        }
    }
}
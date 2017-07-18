package com.intfocus.yh_android.dashboard.kpi.adapter

import android.app.Fragment
import android.app.FragmentManager
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.yh_android.R
import com.intfocus.yh_android.constant.Constant
import com.intfocus.yh_android.dashboard.kpi.NumberThreeFragment
import com.intfocus.yh_android.dashboard.kpi.NumberTwoFragment
import com.intfocus.yh_android.dashboard.kpi.bean.KpiGroupItem
import com.zbl.lib.baseframe.utils.PhoneUtil
import org.greenrobot.eventbus.EventBus

/**
 * Created by liuruilin on 2017/7/10.
 */
class NumberTwoItemAdapter(var ctx: Context, internal var itemDatas: List<KpiGroupItem>?) : RecyclerView.Adapter<NumberTwoItemAdapter.NumberTwoItemHolder>() {
    internal var viewWidth: Int = 0
    internal var viewHeight: Int = 0
    internal var view2width: Int = 0
    var inflater = LayoutInflater.from(ctx)
    private val colors = Constant.colorsRGY

    init {
        val sw = PhoneUtil.getScreenWidth(ctx)
        view2width = PhoneUtil.dip2px(ctx, 350.toFloat())
        viewWidth = sw / 2
        viewHeight = (viewWidth * 0.8).toInt()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NumberTwoItemHolder {
        val contentView = inflater.inflate(R.layout.fragment_number_two, parent, false)
        val holder = NumberTwoItemHolder(contentView, viewType)
        return holder
    }

    override fun onBindViewHolder(holder: NumberTwoItemHolder, position: Int) {
        holder.tv_number_two_title.text = itemDatas!![position].title
        var number = itemDatas!![position].data!!.high_light!!.number
        holder.tv_number_two_number.text = formatNumber(number)
        holder.tv_number_two_number.setTextColor(colors[itemDatas!![position].data!!.high_light!!.arrow])
        holder.tv_number_two_unit.text = itemDatas!![position].unit
        holder.tv_number_two_compare.text = itemDatas!![position].data!!.high_light!!.compare
        holder.tv_number_two_compare.setTextColor(colors[itemDatas!![position].data!!.high_light!!.arrow])
        holder.tv_number_two_sub.text = itemDatas!![position].memo1
        holder.ll_number_two_item.setOnClickListener {
            EventBus.getDefault().post(itemDatas!![position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        if (itemDatas == null)
            return 0
        return itemDatas!!.size
    }

    inner class NumberTwoItemHolder(view: View, viewType: Int) : RecyclerView.ViewHolder(view) {
        var tv_number_two_title = view.findViewById(R.id.tv_number_two_title) as TextView
        var tv_number_two_number = view.findViewById(R.id.tv_number_two_main) as TextView
        var tv_number_two_unit = view.findViewById(R.id.tv_number_two_unit) as TextView
        var tv_number_two_compare = view.findViewById(R.id.tv_number_two_compare) as TextView
        var tv_number_two_sub = view.findViewById(R.id.tv_number_two_sub) as TextView
        var ll_number_two_item = view.findViewById(R.id.ll_number_two_item) as LinearLayout
    }

    fun formatNumber(number: String): String {
        var number = number
        if (number.contains("")) {
            number = number.replace("0+?$".toRegex(), "")//去掉多余的0
            number = number.replace("[.]$".toRegex(), "")//如最后一位是.则去掉
        }
        return number
    }
}

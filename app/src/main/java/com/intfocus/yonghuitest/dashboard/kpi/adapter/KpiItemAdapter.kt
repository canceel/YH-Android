package com.intfocus.yonghuitest.dashboard.kpi.adapter

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiGroup
import com.intfocus.yonghuitest.dashboard.old_kpi.MarginDecoration
import com.intfocus.yonghuitest.util.DisplayUtil

/**
 * Created by liuruilin on 2017/7/10.
 */
class KpiItemAdapter(var ctx: Context, internal var itemDatas: MutableList<KpiGroup>) : RecyclerView.Adapter<KpiItemAdapter.KpiItemHolder>() {
    var inflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KpiItemHolder {
        val contentView = inflater.inflate(R.layout.fragment_kpi_group, parent, false)
        val holder = KpiItemHolder(contentView)
        return holder
    }

    override fun onBindViewHolder(holder: KpiItemHolder, position: Int) {
        holder.tv_kpi_group_name.text = itemDatas[position].group_name
        var recyclerView = holder.rc_kpi_item
        val offset = DisplayUtil.dip2px(ctx, -3.5f)
        recyclerView.setPadding(offset, 0 - offset, offset, 0 - offset + 3)

        var layoutManager: StaggeredGridLayoutManager
        if (itemDatas!![position].data!![0].dashboard_type.equals("number2")) {
            layoutManager = object : StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }

            //设置布局管理器
            recyclerView.layoutManager = layoutManager
            //设置Adapter
            var recycleAdapter = NumberTwoItemAdapter(ctx, itemDatas!![position].data)
            recyclerView.adapter = recycleAdapter
            //设置分隔线
            recyclerView.addItemDecoration(MarginDecoration(ctx))
            //设置增加或删除条目的动画
            recyclerView.itemAnimator = DefaultItemAnimator()
        } else {
            layoutManager = object : StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL) {
                override fun canScrollVertically(): Boolean {
                    return false
                }
            }
            //设置布局管理器
            recyclerView.layoutManager = layoutManager
            //设置Adapter
            var recycleAdapter = NumberThreeItemAdapter(ctx, itemDatas!![position].data)
            recyclerView.adapter = recycleAdapter
            //设置分隔线
            recyclerView.addItemDecoration(MarginDecoration(ctx))
            //设置增加或删除条目的动画
            recyclerView.itemAnimator = DefaultItemAnimator()
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

    inner class KpiItemHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv_kpi_group_name = view.findViewById(R.id.tv_kpi_group_name) as TextView
        var rc_kpi_item = view.findViewById(R.id.rv_kpi_group) as RecyclerView
    }
}

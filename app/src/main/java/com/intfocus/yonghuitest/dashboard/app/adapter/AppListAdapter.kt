package com.intfocus.yonghuitest.dashboard.app.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.bean.dashboard.ListGroupBean
import com.intfocus.yonghuitest.view.MyGridView

/**
 * Created by liuruilin on 2017/6/15.
 */
class AppListAdapter(val ctx: Context, var appListDatas: List<ListGroupBean>?, var listener: AppListItemAdapter.ItemListener)
                                    : RecyclerView.Adapter<AppListAdapter.AppListViewHolder>() {

    var inflater = LayoutInflater.from(ctx)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppListViewHolder {
        val contentView = inflater.inflate(R.layout.item_app_list, parent, false)
        return AppListViewHolder(contentView)
    }

    override fun onBindViewHolder(holder: AppListViewHolder, position: Int) {
        holder.tvAppListTitle.text = appListDatas!![position].group_name
        holder.gvAppListItem.adapter = AppListItemAdapter(ctx, appListDatas!![position].data, listener)
    }

    override fun getItemCount(): Int {
        return if (appListDatas == null) 0 else appListDatas!!.size
    }


    class AppListViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var tvAppListTitle = itemView.findViewById(R.id.tv_app_list_title) as TextView
        var gvAppListItem = itemView.findViewById(R.id.gv_app_list_item) as MyGridView
    }
}

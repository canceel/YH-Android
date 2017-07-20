package com.intfocus.yh_android.subject.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.intfocus.yh_android.R

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/07/20 下午5:54
 * e-mail: PassionateWsj@outlook.com
 * name: 查询选项页面 单选列表ListView的Adapter
 * desc:
 * ****************************************************
 */

class QueryOptionRadioListAdapter(mContext: Context, mData: List<String>) : BaseAdapter() {
    val mContext = mContext
    val mData = mData

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        var holder: QueryOptionRadioListHolder?
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_query_option_radio_list, parent, false)
            holder = QueryOptionRadioListHolder(convertView)
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as QueryOptionRadioListHolder
        }
        holder.mTvRadioListItem.text = mData[position]
//        if (position == holder.mPos) {
//            holder.mTvRadioListItem.setTextColor(ContextCompat.getColor(mContext, R.color.white))
//            holder.mTvRadioListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_title_base))
//        } else {
//            holder.mTvRadioListItem.setTextColor(ContextCompat.getColor(mContext, R.color.query_options_grouping_title_color))
//            holder.mTvRadioListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))
//        }
        holder.mTvRadioListItem.setOnClickListener {
            holder!!.mPos = position
            notifyDataSetChanged()
        }

        return convertView
    }

    override fun getItem(position: Int): Any {
        return if (mData == null) null!! else mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return if (mData == null) 0 else mData.size
    }

    class QueryOptionRadioListHolder(view: View, mPos: Int = 0) {
        var mTvRadioListItem: TextView = view.findViewById(R.id.tv_radio_list_item) as TextView
        // 记录当前选中item的position
        var mPos: Int = mPos
    }
}

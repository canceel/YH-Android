package com.intfocus.yonghuitest.dashboard.report.adapter

import android.view.LayoutInflater
import android.view.LayoutInflater.from
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.bean.dashboard.CategoryBean

/**
 * Created by Liurl on 2017/6/17.
 */
class ReportsLeftListAdapter(var ctx: android.content.Context,
                             var datas: List<CategoryBean>?,
                             var listener: ReportsLeftListAdapter.ReportLeftListListener): BaseAdapter() {

    var mInflater: LayoutInflater = from(ctx)
    var currentPosition = 0

    override fun getCount(): Int {
        return datas!!.size
    }

    override fun getItem(position: Int): Any {
        return datas!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, v: View?, parent: ViewGroup): View {
        var convertView = v
        val viewTag: ReportsLeftListAdapter.ItemViewTag

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_reports_left_lv, null)

            viewTag = ItemViewTag(convertView!!.findViewById(R.id.rl_reports_category_item) as RelativeLayout,
                    convertView.findViewById(R.id.tv_reports_category_name) as TextView,
                    convertView.findViewById(R.id.iv_reports_left_sign) as ImageView,
                    convertView.findViewById(R.id.iv_reports_line_top) as View,
                    convertView.findViewById(R.id.iv_reports_line_end) as View
                    )
            convertView.tag = viewTag
        } else {
            viewTag = convertView.tag as ReportsLeftListAdapter.ItemViewTag
        }

        viewTag.tvCategoryName.text = datas!![position].category
        if (position == currentPosition) {
            viewTag.ivCategorySign.visibility = View.VISIBLE
            viewTag.ivTopLine.visibility = View.VISIBLE
            viewTag.ivEndLine.visibility = View.VISIBLE
        }
        else {
            viewTag.ivCategorySign.visibility = View.INVISIBLE
            viewTag.ivTopLine.visibility = View.INVISIBLE
            viewTag.ivEndLine.visibility = View.INVISIBLE
        }
        viewTag.rlCategory.setOnClickListener { listener.reportLeftItemClick(viewTag.ivCategorySign, position) }

        return convertView
    }

    inner class ItemViewTag(var rlCategory: RelativeLayout,
                            var tvCategoryName: TextView,
                            var ivCategorySign: ImageView,
                            var ivTopLine: View,
                            var ivEndLine: View)

    interface ReportLeftListListener {
        fun reportLeftItemClick(sign: ImageView, position: Int)
    }

    fun refreshListItemState(position: Int) {
        currentPosition = position
        notifyDataSetInvalidated()
    }
}
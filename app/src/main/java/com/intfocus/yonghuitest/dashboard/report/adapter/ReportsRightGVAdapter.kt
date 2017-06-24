package com.intfocus.yonghuitest.dashboard.report.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.StringSignature
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.bean.dashboard.GroupDataBean

/**
 * Created by liuruilin on 2017/6/17.
 */
class ReportsRightGVAdapter(var ctx: Context, var datas: List<GroupDataBean>?, var listener: ItemListener) : BaseAdapter() {
    var mInflater: LayoutInflater = LayoutInflater.from(ctx)

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
        val viewTag: ItemViewTag

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_reports_right_gv, null)

            viewTag = ItemViewTag(convertView!!.findViewById(R.id.ll_reports_right_item) as LinearLayout,
                    convertView.findViewById(R.id.iv_reports_item_img) as ImageView,
                    convertView.findViewById(R.id.tv_reports_item_name) as TextView)
            convertView.tag = viewTag
        } else {
            viewTag = convertView.tag as ItemViewTag
        }

        viewTag.mName.text = datas!![position].name
        Glide.with(ctx).load(datas!![position].icon_link).signature { StringSignature("report") }.into(viewTag.mIcon)
        viewTag.llItem.setOnClickListener { listener.reportItemClick(datas!![position].name, datas!![position].link_path) }

        return convertView
    }

    internal inner class ItemViewTag(var llItem: LinearLayout, var mIcon: ImageView, var mName: TextView)

    interface ItemListener {
        fun reportItemClick(bannerName: String?, link: String?)
    }
}
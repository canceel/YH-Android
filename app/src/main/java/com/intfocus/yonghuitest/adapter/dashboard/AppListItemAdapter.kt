package com.intfocus.yonghuitest.adapter.dashboard

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.bean.dashboard.GroupDataBean

/**
 * Created by liuruilin on 2017/6/16.
 */
class AppListItemAdapter(var ctx: Context, var datas: List<GroupDataBean>?) : BaseAdapter() {
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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewTag: ItemViewTag

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_app_list_gv, null)

            // construct an item tag
            viewTag = ItemViewTag(convertView!!.findViewById(R.id.iv_app_item_img) as ImageView, convertView!!.findViewById(R.id.tv_app_item_name) as TextView)
            convertView!!.tag = viewTag
        } else {
            viewTag = convertView.tag as ItemViewTag
        }
        viewTag.mName.text = datas!![position].name
        viewTag.mIcon.setImageResource(R.drawable.default_icon)

        return convertView
    }

    internal inner class ItemViewTag(var mIcon: ImageView, var mName: TextView)
}

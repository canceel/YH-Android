package com.intfocus.yonghuitest.dashboard.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.bean.dashboard.GroupDataBean
import org.xutils.x

/**
 * Created by liuruilin on 2017/6/16.
 */
class AppListItemAdapter(var ctx: Context, var datas: List<GroupDataBean>?, var listener: AppListItemAdapter.ItemListener) : BaseAdapter() {
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
        val viewTag: AppListItemAdapter.ItemViewTag

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_app_list_gv, null)

            // construct an item tag
            viewTag = ItemViewTag(convertView!!.findViewById(R.id.ll_app_item) as LinearLayout,
                    convertView.findViewById(R.id.iv_app_item_img) as ImageView,
                    convertView.findViewById(R.id.tv_app_item_name) as TextView)
            convertView.tag = viewTag
        } else {
            viewTag = convertView.tag as AppListItemAdapter.ItemViewTag
        }

        viewTag.mName.text = datas!![position].name
        x.image().bind(viewTag.mIcon, datas!![position].icon_link)
        viewTag.llItem.setOnClickListener { listener.itemClick(datas!![position].name, datas!![position].link_path) }

        return convertView
    }

    internal inner class ItemViewTag(var llItem: LinearLayout, var mIcon: ImageView, var mName: TextView)

    interface ItemListener {
        fun itemClick(bannerName: String?, link: String?)
    }
}

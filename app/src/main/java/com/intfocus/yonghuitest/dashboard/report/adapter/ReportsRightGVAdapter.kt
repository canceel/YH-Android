package com.intfocus.yonghuitest.dashboard.report.adapter

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
import com.intfocus.yonghuitest.util.DisplayUtil
import org.xutils.x

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

        var lparams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DisplayUtil.dip2px(ctx, 95f))

        viewTag.llItem.setBackgroundResource(getItemBackground(position, datas!!.size))
        viewTag.mName.text = datas!![position].name
        x.image().bind(viewTag.mIcon, datas!![position].icon_link)
        viewTag.llItem.setOnClickListener { listener.reportItemClick(datas!![position].name, datas!![position].link_path) }
        convertView.layoutParams = lparams
        return convertView
    }

    internal inner class ItemViewTag(var llItem: LinearLayout, var mIcon: ImageView, var mName: TextView)

    interface ItemListener {
        fun reportItemClick(bannerName: String?, link: String?)
    }

    fun getItemBackground(position: Int, size: Int): Int {

        var background: Int = 0
        if (size == 1) {
            background = R.drawable.btn_bg_ab
        }

        if (size == 2) {
            when(position) {
                0,3,6,9,12,15,18 -> background = R.drawable.btn_bg_a
                1,4,7,10,13,16,19 -> background = R.drawable.btn_bg_aaa
            }
        }

        if (size == 3) {
            when(position) {
                0,3,6,9,12,15,18 -> background = R.drawable.btn_bg_a
                1,4,7,10,13,16,19 -> background = R.drawable.btn_bg_aa
                2,5,8,11,14,17,20 -> background = R.drawable.btn_bg_aaa
            }
        }

        if (size > 3) {
            if (position < 3) {
                when(position) {
                    0,3,6,9,12,15,18 -> background = R.drawable.btn_bg_a
                    1,4,7,10,13,16,19 -> background = R.drawable.btn_bg_aa
                    2,5,8,11,14,17,20 -> background = R.drawable.btn_bg_aaa
                }
            }
            else {
                background = getItemBackground(position - 3, size - 3)
            }
        }

        return background
    }
}
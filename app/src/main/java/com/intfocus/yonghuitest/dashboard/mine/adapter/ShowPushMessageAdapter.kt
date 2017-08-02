package com.intfocus.yonghuitest.dashboard.mine.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.dashboard.mine.bean.PushMessageBean

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/01 下午3:38
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ShowPushMessageAdapter(val mContext: Context, val listener: OnPushMessageListener) : RecyclerView.Adapter<ShowPushMessageAdapter.MessageHolder>() {

    var mData = mutableListOf<PushMessageBean>()

    override fun onBindViewHolder(holder: MessageHolder?, position: Int) {
        holder!!.tvNoticeType.visibility = View.GONE

        holder.tvNoticeTitle.text = mData[position].title
        holder.tvNoticeListContent.text = mData[position].body_text
        holder.tvNoticeTime.text = mData[position].debug_timestamp

        if (mData[position].new_msg) {
            holder.tvNoticePoint.visibility = View.VISIBLE

            holder.tvNoticeTitle.setTextColor(ContextCompat.getColor(mContext,R.color.co3_syr))
            holder.tvNoticeListContent.setTextColor(ContextCompat.getColor(mContext,R.color.co3_syr))
            holder.tvNoticeTime.setTextColor(ContextCompat.getColor(mContext,R.color.co3_syr))
        } else {
            holder.tvNoticePoint.visibility = View.GONE

            holder.tvNoticeTitle.setTextColor(ContextCompat.getColor(mContext,R.color.co4_syr))
            holder.tvNoticeListContent.setTextColor(ContextCompat.getColor(mContext,R.color.co4_syr))
            holder.tvNoticeTime.setTextColor(ContextCompat.getColor(mContext,R.color.co4_syr))
        }

        holder.llNoticeItem.setOnClickListener {
            listener.onItemClick(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageHolder {
        return MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_notice_list, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setData(data: List<PushMessageBean>) {
        mData = data as MutableList<PushMessageBean>
        notifyDataSetChanged()
    }

    class MessageHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val llNoticeItem = itemView!!.findViewById(R.id.ll_notice_item) as LinearLayout
        val tvNoticeType = itemView!!.findViewById(R.id.tv_notice_type) as TextView
        val tvNoticeTitle = itemView!!.findViewById(R.id.tv_notice_title) as TextView
        val tvNoticePoint = itemView!!.findViewById(R.id.iv_notice_point) as ImageView
        val tvNoticeListContent = itemView!!.findViewById(R.id.tv_notice_list_content) as TextView
        val tvNoticeTime = itemView!!.findViewById(R.id.tv_notice_time) as TextView
    }

    interface OnPushMessageListener {
        fun onItemClick(position: Int)
    }
}
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
import com.zbl.lib.baseframe.utils.ToastUtil

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/01 下午3:38
 * e-mail: PassionateWsj@outlook.com
 * name: 推送消息适配器
 * desc: 显示当前用户推送消息的适配器
 * ****************************************************
 */
class ShowPushMessageAdapter(val mContext: Context, val listener: OnPushMessageListener) : RecyclerView.Adapter<ShowPushMessageAdapter.MessageHolder>() {
    /**
     * 推送消息 bean 集合
     */
    var mData = mutableListOf<PushMessageBean>()

    override fun onBindViewHolder(holder: MessageHolder?, position: Int) {
        // 加载数据
        if (mData.size > 0) {
            holder!!.tvNoticeType.visibility = View.GONE

            holder.tvNoticeTitle.text = mData[position].title
            holder.tvNoticeListContent.text = mData[position].body_text
            holder.tvNoticeTime.text = mData[position].debug_timestamp

            // 根据 new_msg 判断是否是新消息 处理 item 样式
            if (mData[position].new_msg) {
                holder.tvNoticePoint.visibility = View.VISIBLE

                holder.tvNoticeTitle.setTextColor(ContextCompat.getColor(mContext, R.color.co3_syr))
                holder.tvNoticeListContent.setTextColor(ContextCompat.getColor(mContext, R.color.co3_syr))
                holder.tvNoticeTime.setTextColor(ContextCompat.getColor(mContext, R.color.co3_syr))
            } else {
                holder.tvNoticePoint.visibility = View.GONE

                holder.tvNoticeTitle.setTextColor(ContextCompat.getColor(mContext, R.color.co4_syr))
                holder.tvNoticeListContent.setTextColor(ContextCompat.getColor(mContext, R.color.co4_syr))
                holder.tvNoticeTime.setTextColor(ContextCompat.getColor(mContext, R.color.co4_syr))
            }

            holder.llNoticeItem.setOnClickListener {
                listener.onItemClick(position)
            }
        } else {
            ToastUtil.showToast(mContext, "暂无消息")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageHolder {
        return MessageHolder(LayoutInflater.from(mContext).inflate(R.layout.item_notice_list, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setData(data: List<PushMessageBean>) {
        mData.clear()
        mData.addAll(data)
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
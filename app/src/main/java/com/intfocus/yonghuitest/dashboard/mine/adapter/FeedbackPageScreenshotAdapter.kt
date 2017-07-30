package com.intfocus.yonghuitest.dashboard.mine.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.intfocus.yonghuitest.R
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File
import java.net.URI


/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/07/30 下午6:06
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

class FeedbackPageScreenshotAdapter(val mContext: Context, val listener: ScreenshotItemClickListener) :
        RecyclerView.Adapter<FeedbackPageScreenshotAdapter.ScreenshotHolder>() {

    companion object {
        val MAX_CHOSEN_PHOTO_NUM = 3
    }

    var mData = mutableListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ScreenshotHolder {
        val inflate = LayoutInflater.from(mContext).inflate(R.layout.item_feedback_page_screenshot, parent, false)
        return ScreenshotHolder(inflate)
    }

    override fun getItemCount(): Int {
        return mData.size + 1
    }

    override fun onBindViewHolder(holder: ScreenshotHolder?, position: Int) {
        when {
            itemCount == MAX_CHOSEN_PHOTO_NUM + 1 && position == 3 -> {
                setAddScreenshotView(holder)
                holder!!.rlFeedbackPageScreenshotContainer.visibility = View.GONE
            }
            position == itemCount - 1 -> {

                setAddScreenshotView(holder)
            }
            else -> {
                var uri = mData[position].toString()
                Luban.with(mContext)
                        .load(File(URI.create(uri)))                     //传人要压缩的图片
                        .setCompressListener(object : OnCompressListener {
                            override fun onSuccess(p0: File?) {
                                Glide.with(mContext).load(p0).into(holder!!.ivPageScreenshot)
                                holder.ivScreenshotDel.visibility = View.VISIBLE
                                holder.ivScreenshotDel.setOnClickListener { listener.delScreenshot(position) }
                            }

                            override fun onError(p0: Throwable?) {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                            override fun onStart() {
                                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                            }

                        }).launch()    //启动压缩
            }
        }
    }

    private fun setAddScreenshotView(holder: ScreenshotHolder?) {
        holder!!.rlFeedbackPageScreenshotContainer.visibility = View.VISIBLE

        holder.ivPageScreenshot.setImageDrawable(mContext.resources.getDrawable(R.drawable.btn_addpic))
        holder.ivScreenshotDel.visibility = View.GONE
        holder.ivPageScreenshot.setOnClickListener { listener.addScreenshot(4 - itemCount) }
    }

    fun setData(data: List<Uri>?) {
        mData.addAll(data!!)
        notifyDataSetChanged()
    }

    fun delScreenWithPos(mPos: Int) {
        mData.removeAt(mPos)
        notifyDataSetChanged()
    }

    class ScreenshotHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var rlFeedbackPageScreenshotContainer = itemView.findViewById(R.id.ll_feedback_page_screenshot_container) as LinearLayout
        var ivPageScreenshot = itemView.findViewById(R.id.iv_feedback_page_screenshot) as ImageView
        var ivScreenshotDel = itemView.findViewById(R.id.iv_feedback_screenshot_del) as ImageView
    }

    interface ScreenshotItemClickListener {
        fun addScreenshot(maxNum: Int)
        fun delScreenshot(mPos: Int)
    }

}
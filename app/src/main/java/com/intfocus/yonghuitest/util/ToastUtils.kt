package com.intfocus.yonghuitest.util

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.intfocus.yonghuitest.R

/**
 * Created by CANC on 2017/7/31.
 */
object ToastUtils {
    private var mToast: Toast? = null

    fun show(context: Context, message: String, colorId: Int = 0) {
        if (mToast == null) {
            val view = LinearLayout(context)
            LayoutInflater.from(context).inflate(R.layout.toast, view)

            mToast = Toast(context)
            mToast!!.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            mToast!!.duration = Toast.LENGTH_SHORT
            mToast!!.view = view
        }
        val textView = mToast!!.view.findViewById(R.id.toast_text) as TextView
        if (colorId != 0) {
            textView.setBackgroundColor(ContextCompat.getColor(context, colorId))
        }
        textView.text = message
        mToast!!.show()
    }

    fun cancel() {
        if (mToast != null) {
            mToast!!.cancel()
        }
    }
}
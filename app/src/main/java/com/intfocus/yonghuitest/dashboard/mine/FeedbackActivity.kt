package com.intfocus.yonghuitest.dashboard.mine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.intfocus.yonghuitest.R

class FeedbackActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_feedback_banner_back -> {
                finish()
            }
        }
    }
}

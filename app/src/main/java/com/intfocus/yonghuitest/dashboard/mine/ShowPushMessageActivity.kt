package com.intfocus.yonghuitest.dashboard.mine

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.dashboard.mine.adapter.ShowPushMessageAdapter
import com.intfocus.yonghuitest.dashboard.mine.bean.PushMessageBean
import com.intfocus.yonghuitest.dashboard.mine.presenter.PushMessagePresenter
import com.intfocus.yonghuitest.dashboard.mine.view.PushMessageView
import com.intfocus.yonghuitest.util.ToastUtils
import kotlinx.android.synthetic.main.activity_show_push_message.*

class ShowPushMessageActivity : AppCompatActivity(), PushMessageView, ShowPushMessageAdapter.OnPushMessageListener {

    val adapter = ShowPushMessageAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_push_message)

        initData()
        initAdapter()
    }

    private fun initData() {
        val presenter: PushMessagePresenter = PushMessagePresenter(applicationContext,this)
        presenter.loadData()
    }

    private fun initAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_show_push_message.layoutManager = mLayoutManager
        rv_show_push_message.adapter = adapter
    }

    override fun onResultFailure() {
        ToastUtils.show(this, "没有找到数据")
    }

    override fun onResultSuccess(data: MutableList<PushMessageBean>?) {
        adapter.setData(data!!)
    }
    override fun onItemClick(position: Int) {
        // TODO 跳转msg内容界面
    }
}

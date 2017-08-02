package com.intfocus.yonghuitest.dashboard.mine

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.bean.User
import com.intfocus.yonghuitest.dashboard.mine.activity.PushMessageContentActivity
import com.intfocus.yonghuitest.dashboard.mine.adapter.ShowPushMessageAdapter
import com.intfocus.yonghuitest.dashboard.mine.bean.PushMessageBean
import com.intfocus.yonghuitest.dashboard.mine.presenter.PushMessagePresenter
import com.intfocus.yonghuitest.dashboard.mine.view.PushMessageView
import com.intfocus.yonghuitest.db.OrmDBHelper
import com.intfocus.yonghuitest.util.FileUtil
import com.intfocus.yonghuitest.util.K
import com.intfocus.yonghuitest.util.RxBusUtil
import com.intfocus.yonghuitest.util.ToastUtils
import kotlinx.android.synthetic.main.activity_show_push_message.*
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.sql.SQLException

class ShowPushMessageActivity : AppCompatActivity(), PushMessageView, ShowPushMessageAdapter.OnPushMessageListener {
    var mUserID = 0
    
    val adapter = ShowPushMessageAdapter(this, this)
    val pushMessageDao = OrmDBHelper.getInstance(this).pushMessageDao!!
    lateinit var subscribe: Subscription

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_push_message)

        initData()
        initAdapter()
    }

    private fun initData() {
        val presenter: PushMessagePresenter = PushMessagePresenter(applicationContext, this)
        presenter.loadData()

        val userConfigPath = String.format("%s/%s", FileUtil.basePath(this), K.kUserConfigFileName)
        if (File(userConfigPath).exists()) {
            val user = Gson()!!.fromJson(FileUtil.readConfigFile(userConfigPath).toString(), User::class.java)
            if (user!!.isIs_login) {
                mUserID = user!!.user_id
            }
        }
        // RxBus接收到推送信息，处理数据列表更新
        subscribe = RxBusUtil.getInstance().toObservable(PushMessageBean::class.java)
                .subscribe { msg ->
                    if ("UpDatePushMessage".equals(msg)) presenter.loadData()
                }
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
        adapter.setUserId(mUserID)
        adapter.setData(data!!)
    }

    override fun onItemClick(position: Int) {
        // 跳转msg内容界面
        adapter.mData[position].new_msg = false
        pushMessageDao.update(adapter.mData[position])

        Observable.create(Observable.OnSubscribe<MutableList<PushMessageBean>> { t ->
            try {
                t!!.onNext(pushMessageDao.queryForAll())
            } catch(e: SQLException) {
                e.printStackTrace()
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<PushMessageBean>> {
                    override fun onCompleted() {

                    }

                    override fun onError(e: Throwable) {
                    }

                    override fun onNext(pushMessageBeen: List<PushMessageBean>) {
                        adapter.setData(pushMessageBeen)
                    }
                })

        adapter.notifyDataSetChanged()
        var intent = Intent(this, PushMessageContentActivity::class.java)
        intent.putExtra("push_message_bean", adapter.mData[position])
        startActivity(intent)
    }

    fun back(v: View?) {
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscribe.isUnsubscribed)
            subscribe.unsubscribe()
    }
}

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
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.sql.SQLException

class ShowPushMessageActivity : AppCompatActivity(), PushMessageView, ShowPushMessageAdapter.OnPushMessageListener {
    var mUserID = 0
    val message = "{\n" +
            "         \"type\":\"report\",\n" +
            "         \"title\":\"第二集群销售额\",\n" +
            "         \"url\":\"/mobile/v2/group/%@/template/4/report/8\",\n" +
            "         \"obj_id\":8,\n" +
            "         \"obj_type\":1,\n" +
            "         \"debug_timestamp\":\"2017-08-01 15:50:51 +0800\"\n" +
            "     }"
    val adapter = ShowPushMessageAdapter(this, this)
    val pushMessageDao = OrmDBHelper.getInstance(this).pushMessageDao!!

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
        RxBusUtil.getInstance().toObservable(PushMessageBean::class.java)
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
        // TODO 跳转msg内容界面
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
}

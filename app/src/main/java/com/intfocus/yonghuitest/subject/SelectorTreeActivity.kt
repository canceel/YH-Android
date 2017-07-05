package com.intfocus.yonghuitest.subject

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import android.widget.Toast
import com.google.gson.Gson

import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.adapter.SimpleTreeAdapter
import com.intfocus.yonghuitest.base.BaseActivity
import com.intfocus.yonghuitest.subject.selecttree.Bean
import com.intfocus.yonghuitest.subject.selecttree.TreeListViewAdapter
import com.intfocus.yonghuitest.subject.selecttree.SelectItems
import com.intfocus.yonghuitest.util.FileUtil
import java.util.ArrayList

class SelectorTreeActivity : BaseActivity() {
    val mDatas = ArrayList<Bean>()
    lateinit var mAdapter: TreeListViewAdapter<*>
    var gson = Gson()
    var selectItemsStr = ""
    var selectedItemPath = ""
    lateinit var mTree: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selector_tree)

        var intent = intent
        selectItemsStr = intent.getStringExtra("selectItems")
        selectedItemPath = intent.getStringExtra("selectedItemPath")
        mTree = findViewById(R.id.lv_selector_tree) as ListView
        initDatas(selectItemsStr)
    }

    fun initDatas(selectItems: String) {
        Thread(Runnable {
            var selectItems = gson.fromJson(selectItems, SelectItems::class.java)
            var id = 1
            var first: Int
            var second: Int
            for (selectItem in selectItems.data!!.iterator()) {
                mDatas.add(Bean(id, 0, selectItem.titles))
                first = id
                id += 1
                if (selectItem.infos != null) {
                    for (info in selectItem.infos!!.iterator()) {
                        mDatas.add(Bean(id, first, info.titles))
                        second = id
                        id += 1
                        if (info.infos != null) {
                            for (info in info.infos!!.iterator()) {
                                mDatas.add(Bean(id, second, info.titles))
                                id += 1
                            }
                        }
                    }
                }
            }
            runOnUiThread {
                mAdapter = SimpleTreeAdapter(mTree, this, mDatas, 10)
                mAdapter.setOnTreeNodeClickListener { node, _ ->
                    if (node.isLeaf) {
                        var selectedName = node.name
                        if (node.parent != null) {
                            selectedName = node.parent.name + "||" + selectedName

                            if (node.parent.parent != null) {
                                selectedName = node.parent.parent.name + "||" + selectedName
                            }
                        }
                        Toast.makeText(applicationContext, selectedName,
                                Toast.LENGTH_SHORT).show()
                        FileUtil.writeFile(selectedItemPath, selectedName)
                        finish()
                    }
                }

                mTree.adapter = mAdapter
            }

        }).start()
    }

    /*
     * 返回
     */
    override fun dismissActivity(v: View) {
        this@SelectorTreeActivity.onBackPressed()
    }

    override fun onBackPressed() {
        finish()
    }
}

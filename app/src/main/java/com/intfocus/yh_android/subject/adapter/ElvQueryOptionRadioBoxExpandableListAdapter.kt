package com.intfocus.yh_android.subject.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/07/20 下午6:01
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ElvQueryOptionRadioBoxExpandableListAdapter : BaseExpandableListAdapter() {
    /**
     * 得到当前position父项
     */
    override fun getGroup(groupPosition: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 子项是否可选中
     */
    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 按函数的名字来理解应该是是否具有稳定的id，这个方法目前一直都是返回false，没有去改动过
     */
    override fun hasStableIds(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 得到父项的view
     */
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 得到子项数量
     */
    override fun getChildrenCount(groupPosition: Int): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 得到子项
     */
    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 得到当前父项position
     */
    override fun getGroupId(groupPosition: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 得到子项View
     */
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 得到当前子项position
     */
    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * 获取父项数量
     */
    override fun getGroupCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
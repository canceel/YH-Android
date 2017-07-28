package com.intfocus.yonghuitest.dashboard.kpi

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import com.intfocus.yonghuitest.view.MyLinearLayoutManager
import com.yonghui.homemetrics.utils.Utils

/**
 * Created by CANC on 2017/7/28.
 */

class HomePageScrollerListener(private val context: Context,
                               private val recyclerView: RecyclerView,
                               private val titleTop: View) : RecyclerView.OnScrollListener() {
    private val statusBarHeight: Int = Utils.getStatusBarHeight(context)
    private val linearLayoutManager: MyLinearLayoutManager = recyclerView.layoutManager as MyLinearLayoutManager


    /**
     * firstVisibleItem：当前能看见的第一个列表项ID（从0开始）
     * visibleItemCount：当前能看见的列表项个数（小半个也算） totalItemCount：列表项共数
     */
    override fun onScrolled(view: RecyclerView?, dx: Int, dy: Int) {
        val isSignificantDelta = Math.abs(dy) > 4
        if (isSignificantDelta) {
            if (dy > 0) {
            } else {
            }
        }

        val loc = IntArray(2)
        val firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition()
        if (firstVisibleItem == 0 && recyclerView.getChildAt(firstVisibleItem) != null) {
            recyclerView.getChildAt(firstVisibleItem).getLocationOnScreen(loc)
            if (loc[1] <= statusBarHeight) {
                val alpha = Math.abs(loc[1] - statusBarHeight) * 1.0f / titleTop
                        .measuredHeight
                titleTop.alpha = alpha
            } else {
                titleTop.alpha = 0f
            }

        }
    }
}

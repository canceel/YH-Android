package com.intfocus.yonghuitest.dashboard.kpi

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.base.BaseModeFragment
import com.intfocus.yonghuitest.constant.Constant
import com.intfocus.yonghuitest.dashboard.kpi.bean.KpiGroupItem
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.fragment_number_two.*
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat

/**
 * Created by liuruilin on 2017/6/21.
 */
class NumberTwoFragment(var datas: KpiGroupItem): BaseModeFragment<Subject>() {

    private var rootView: View? = null
    internal var df = DecimalFormat("#.00")
    private val colors = Constant.colorsRGY

    companion object {
        fun newInstance(datas: KpiGroupItem): NumberTwoFragment {
            val fragment = NumberTwoFragment(datas)
            val args = Bundle()
            args.putSerializable("Datas", datas)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            datas = arguments.getSerializable("Datas") as KpiGroupItem
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater!!.inflate(R.layout.fragment_number_two, container, false)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Handler().postDelayed({ init() }, 100)
        super.onActivityCreated(savedInstanceState)
    }

    fun init() {
        tv_number_two_title.text = datas.title
        var number = datas.data!!.high_light!!.number
        tv_number_two_main.text = formatNumber(number)
        tv_number_two_main.setTextColor(colors[datas.data!!.high_light!!.arrow])
        tv_number_two_unit.text = datas.unit
        tv_number_two_compare.text = datas.data!!.high_light!!.compare
        tv_number_two_compare.setTextColor(colors[datas.data!!.high_light!!.arrow])
        tv_number_two_sub.text = datas.memo1
        ll_number_two_item.setOnClickListener {
            EventBus.getDefault().post(datas)
        }
    }
}

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
import kotlinx.android.synthetic.main.fragment_number_three.*
import kotlinx.android.synthetic.main.fragment_number_two.*
import org.greenrobot.eventbus.EventBus
import java.text.DecimalFormat

/**
 * Created by liuruilin on 2017/6/21.
 */
class NumberThreeFragment(var datas: KpiGroupItem): BaseModeFragment<Subject>() {

    private var rootView: View? = null
    internal var df = DecimalFormat("#.00")
    private val colors = Constant.colorsRGY

    companion object {
        fun newInstance(datas: KpiGroupItem): NumberThreeFragment {
            val fragment = NumberThreeFragment(datas)
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
            rootView = inflater!!.inflate(R.layout.fragment_number_three, container, false)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        Handler().postDelayed({ init() }, 100)
        super.onActivityCreated(savedInstanceState)
    }

    fun init() {
        Log.i("testlog", datas.title)
        tv_number_three_title.text = datas.title
        var number = datas.data!!.high_light!!.number
        tv_number_three_main.text = formatNumber(number)
        tv_number_three_unit.text = datas.unit
        tv_number_three_compare.text = datas.data!!.high_light!!.compare
        tv_number_three_sub.text = datas.memo1
        rl_kpi_number_three.setOnClickListener {
            EventBus.getDefault().post(datas)
        }
    }
}
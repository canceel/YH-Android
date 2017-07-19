package com.intfocus.yh_android.subject

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import com.intfocus.yh_android.R
import com.intfocus.yh_android.bean.QueryOptions
import com.zbl.lib.baseframe.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_query_options.*
import java.text.SimpleDateFormat
import java.util.*

class QueryOptionsActivity : AppCompatActivity() {

    private val testData = "{\n" +
            "\t\"data\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"text_input\",\n" +
            "\t\t\t\"data\":[\"\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"scan_input\",\n" +
            "\t\t\t\"data\":[\"\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"radio_box\",\n" +
            "\t\t\t\"data\":[\"选项一\",\"选项二\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"check_box\",\n" +
            "\t\t\t\"data\":[\"优惠活动1\",\"优惠活动2\",\"优惠活动3\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"radio_box_spinner\",\n" +
            "\t\t\t\"data\":[\"100%\",\"75%\",\"50%\",\"25%\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"check_box_spinner\",\n" +
            "\t\t\t\"data\":[\"100%\",\"75%\",\"50%\",\"25%\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"radio_list\",\n" +
            "\t\t\t\"data\":[\"门店1\",\"门店2\",\"门店3\",\"门店4\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"time_select\",\n" +
            "\t\t\t\"data\":[\"20170717\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"type\":\"start_and_end_time\",\n" +
            "\t\t\t\"data\":[\"201707171438\",\"201707171439\"]\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}"


    private var query_option_time: Long = 0
    private var query_option_start_time: Long = 0
    private var query_option_end_time: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_options)

        //解析初始数据
        val queryOptions = Gson().fromJson(testData, QueryOptions::class.java)

        val queryOptionsData = queryOptions!!.data

        val mSubmitData = hashMapOf<String, String>()

        initData(queryOptionsData)

        initListener()


        // 提交的监听
        btn_query_option_submit.setOnClickListener(View.OnClickListener {
            for (i in queryOptionsData!!.indices) {
                when (queryOptionsData!![i].type) {
                    "text_input" -> {
                        if (et_query_option_text_input.text.toString() == null || et_query_option_text_input.text.toString().equals("")) {
                            ToastUtil.showToast(this, "请输入文本")
                        } else {
                            mSubmitData.put("text_input", et_query_option_text_input.text.toString())
                        }
                    }
                    "scan_input" -> {
                        if (et_query_option_scan_input.text.toString() == null || et_query_option_scan_input.text.toString().equals("")) {
                            ToastUtil.showToast(this, "请扫一扫输入")
                        } else {
                            mSubmitData.put("scan_input", et_query_option_scan_input.text.toString())
                        }
                    }
                    "radio_box" -> {
                        mSubmitData.put("radio_box", rg_query_option.checkedRadioButtonId.toString())
                    }
                    "check_box" -> {
                        val mCheckBoxBulider: StringBuilder = StringBuilder()
                        for (s in queryOptionsData[i].data.indices) {
                            val checkbox = (ll_query_option_check_box_container.getChildAt(s)) as CheckBox
                            if (checkbox.isChecked) {
                                mCheckBoxBulider.append(s)
                                mCheckBoxBulider.append("-")
                            }
                        }
                        mCheckBoxBulider.replace(queryOptionsData[i].data.size - 2, queryOptionsData[i].data.size - 1, "")
                        mSubmitData.put("check_box", mCheckBoxBulider.toString())
                    }
                    "radio_box_spinner" -> {
                        mSubmitData.put("radio_box_spinner", sp_query_option_radio_box.selectedItem.toString())
                    }
                    "check_box_spinner" -> {
                    }
                    "radio_list" -> {

                    }
                    "time_select" -> {
                        if (tv_query_option_time_select.text == null || tv_query_option_time_select.text.equals("")) {
                            ToastUtil.showToast(this, "请选择日期")
                        } else {
                            mSubmitData.put("time_select", tv_query_option_time_select.text.toString())
                        }
                    }
                    "start_and_end_time" -> {
                        if (tv_query_option_start_time.text == null || tv_query_option_start_time.text.equals("")) {
                            ToastUtil.showToast(this, "请选择起始日期")
                        } else if (tv_query_option_end_time.text == null || tv_query_option_end_time.text.equals("")) {
                            ToastUtil.showToast(this, "请选择结束日期")
                        } else {
                            query_option_start_time = getTimeMillis(tv_query_option_start_time.text.toString())
                            query_option_end_time = getTimeMillis(tv_query_option_end_time.text.toString())
                            Log.i(TAG, "query_option_start_time = $query_option_start_time")
                            Log.i(TAG, "query_option_end_time = $query_option_end_time")
                            if (query_option_start_time > query_option_end_time) {
                                ToastUtil.showToast(this, "起始或结束日期有误")
                            } else {
                                mSubmitData.put("start_time", tv_query_option_start_time.text.toString())
                                mSubmitData.put("end_time", tv_query_option_end_time.text.toString())
                            }
                        }
                    }
                    else -> {

                    }
                }
            }

        })
        val queryResult: StringBuilder = StringBuilder()
        ibtn_search.setOnClickListener {
            for ((key, value) in mSubmitData) {
                Log.i(TAG, "key = $key, value = $value")
                queryResult.append("key = $key, value = $value  ")
            }

            ToastUtil.showToast(this, String(queryResult))
        }
    }

    /**
     * 将 yyyy年MM月dd日 格式时间转为时间戳
     */
    private fun getTimeMillis(text: String?): Long {
        return SimpleDateFormat("yyyy年MM月dd日").parse(text).time
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
        tv_query_option_time_select.setOnClickListener {
            getDatePickerDialog(tv_query_option_time_select)
        }
        ibtn_query_time_select.setOnClickListener {
            getDatePickerDialog(tv_query_option_time_select)
        }

        tv_query_option_start_time.setOnClickListener {
            getDatePickerDialog(tv_query_option_start_time)

        }
        ibtn_query_start_time_select.setOnClickListener {
            getDatePickerDialog(tv_query_option_start_time)

        }
        tv_query_option_end_time.setOnClickListener {
            getDatePickerDialog(tv_query_option_end_time)

        }
        ibtn_query_end_time_select.setOnClickListener {
            getDatePickerDialog(tv_query_option_end_time)
        }

    }

    /**
     * 获取日期选择器
     */
    private fun getDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            val df = SimpleDateFormat("yyyy年MM月dd日")
            textView.text = df.format(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    /**
     * 动态加载视图
     */
    private fun initData(queryOptionsData: List<QueryOptions.DataBean>) {
        for (i in queryOptionsData.indices) {
            when (queryOptionsData[i].type) {
                "text_input" -> {
                    ll_query_option_text_input.visibility = View.VISIBLE
                    et_query_option_text_input.text.clear()
                    et_query_option_text_input.text.insert(0, queryOptionsData[i].data[0])
                }
                "scan_input" -> {
                    ll_query_option_scan_input.visibility = View.VISIBLE
                    et_query_option_scan_input.text.clear()
                    et_query_option_scan_input.text.insert(0, queryOptionsData[i].data[0])
                }
                "radio_box" -> {
                    ll_query_option_radio_box_input.visibility = View.VISIBLE
                    (rg_query_option.getChildAt(0) as RadioButton).text = queryOptionsData[i].data[0]
                    (rg_query_option.getChildAt(1) as RadioButton).text = queryOptionsData[i].data[1]
                    (rg_query_option.getChildAt(0) as RadioButton).isChecked = true
                }
                "check_box" -> {
                    ll_query_option_check_box_input.visibility = View.VISIBLE
                    for (s in queryOptionsData[i].data) {
                        val mCheckBox = CheckBox(this)
                        mCheckBox.text = s
                        ll_query_option_check_box_container.addView(mCheckBox)
                    }
                }
                "radio_box_spinner" -> {
                    ll_query_option_radio_box_spinner_input.visibility = View.VISIBLE
                    var radioBoxSpinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, queryOptionsData[i].data)
                    radioBoxSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    sp_query_option_radio_box.adapter = radioBoxSpinnerAdapter
                }
                "check_box_spinner" -> {
                    ll_query_option_check_box_spinner_input.visibility = View.VISIBLE
                }
                "radio_list" -> {
                    ll_query_option_radio_list_input.visibility = View.VISIBLE
                    lv_query_option_radio_list.adapter = QueryOptionRadioListAdapter(this, queryOptionsData[i].data)
                }
                "time_select" -> {
                    ll_query_option_time_select_input.visibility = View.VISIBLE
                }
                "start_and_end_time" -> {
                    ll_query_option_start_time_input.visibility = View.VISIBLE
                    ll_query_option_end_time_input.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
    }

    companion object {

        private val TAG = "hjjzz"
    }
}

class QueryOptionRadioListAdapter(mContext: Context, mData: List<String>) : BaseAdapter() {
    val mContext = mContext
    val mData = mData

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        var holder: QueryOptionRadioListHolder? = null
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_query_option_radio_list, parent, false)
            holder = QueryOptionRadioListHolder(convertView)
            convertView!!.tag = holder
        } else {
            holder = convertView.tag as QueryOptionRadioListHolder
        }
        holder.mTvRadioListItem.text = mData[position]
        if (position == holder.mPos) {
            holder.mTvRadioListItem.setTextColor(ContextCompat.getColor(mContext, R.color.white))
            holder.mTvRadioListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_title_base))
        } else {
            holder.mTvRadioListItem.setTextColor(ContextCompat.getColor(mContext, R.color.query_options_grouping_title_color))
            holder.mTvRadioListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white))

        }
        holder.mTvRadioListItem.setOnClickListener {
            holder!!.mPos = position
            notifyDataSetChanged()
        }

        return convertView
    }

    override fun getItem(position: Int): Any {
        return if (mData == null) null!! else mData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return if (mData == null) 0 else mData.size
    }

    class QueryOptionRadioListHolder(view: View, mPos: Int = 0) {
        var mTvRadioListItem: TextView = view.findViewById(R.id.tv_radio_list_item) as TextView
        var mPos: Int = mPos
    }
}

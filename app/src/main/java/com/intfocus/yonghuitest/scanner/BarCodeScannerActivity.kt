package com.intfocus.yonghuitest.scanner

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.dashboard.mine.FeedbackActivity
import com.intfocus.yonghuitest.util.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import kotlinx.android.synthetic.main.activity_bar_code_scanner_v2.*
import kotlinx.android.synthetic.main.popup_input_barcode.view.*


class BarCodeScannerActivity : AppCompatActivity(), QRCodeView.Delegate, View.OnClickListener {
    companion object {
        val TAG = "hjjzz"
        val REQUEST_CODE_CHOOSE = 1
    }

    var isLightOn = false

    var view: View? = null
    var popupWindow: PopupWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_scanner_v2)
        initView()
        initListener()

        zbarview_barcode_scanner.setDelegate(this)
        zbarview_barcode_scanner.startSpot()
    }


    override fun onStart() {
        super.onStart()
        zbarview_barcode_scanner.startCamera()
    }


    override fun onStop() {
        super.onStop()
        zbarview_barcode_scanner.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        zbarview_barcode_scanner.onDestroy()
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        view = LayoutInflater.from(this).inflate(R.layout.popup_input_barcode, null)
        popupWindow = PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true)
        val mTypeFace = Typeface.createFromAsset(this.assets, "ALTGOT2N.TTF")
        view!!.et_input_barcode.typeface = mTypeFace
    }

    override fun onRestart() {
        super.onRestart()
        zbarview_barcode_scanner.startSpot()
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
        //手电筒开关
        ll_btn_light_switch.setOnClickListener {
            isLightOn = !isLightOn
            checkLightStatus(isLightOn, tv_barcode_light, cb_barcode_light)
            if (isLightOn) {
                zbarview_barcode_scanner.openFlashlight()
            } else {
                zbarview_barcode_scanner.closeFlashlight()
            }
        }
        //手动输入条码点击样式
        ll_btn_input_bar_code.setOnTouchListener { _, event ->
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    tv_input_bar_code.background = resources.getDrawable(R.drawable.btn_manual1)
                    tv_barcode_input.setTextColor(resources.getColor(R.color.co7_syr))
                }
                event.action == MotionEvent.ACTION_UP -> {
                    tv_input_bar_code.background = resources.getDrawable(R.drawable.btn_manual2)
                    tv_barcode_input.setTextColor(resources.getColor(R.color.co10_syr))
                }
            }
            false
        }
        //手动输入条码弹出popupwindow
        ll_btn_input_bar_code.setOnClickListener {
            popupWindow!!.showAsDropDown(barcode_top_reference, 0, 0)
            zbarview_barcode_scanner.stopSpot()
            checkLightStatus(isLightOn, view!!.tv_input_barcode_light, view!!.cb_input_barcode_light)
        }
        iv_barcode_back.setOnClickListener {
            finish()
        }

        //popupWindows中打开手电筒点击事件
        view!!.ll_btn_input_barcode_light_switch.setOnClickListener {
            isLightOn = !isLightOn
            checkLightStatus(isLightOn, view!!.tv_input_barcode_light, view!!.cb_input_barcode_light)
            if (isLightOn) {
                zbarview_barcode_scanner.openFlashlight()
            } else {
                zbarview_barcode_scanner.closeFlashlight()
            }
        }
        view!!.btn_input_barcode_confirm.setOnClickListener {
            var trim = view!!.et_input_barcode.text.toString()
            if (trim == "") {
                ToastUtils.show(this, "请输入条码")
                return@setOnClickListener
            }

            zbarview_barcode_scanner.closeFlashlight()
            isLightOn = false
            checkLightStatus(isLightOn, view!!.tv_input_barcode_light, view!!.cb_input_barcode_light)
            val intent = Intent(this, ScannerResultActivity::class.java)
            intent.putExtra(URLs.kCodeInfo, trim)
            intent.putExtra(URLs.kCodeType, "input")
            startActivity(intent)
            finish()
        }

        view!!.iv_input_barcode_back.setOnClickListener {
            checkLightStatus(isLightOn, tv_barcode_light, cb_barcode_light)
            popupWindow!!.dismiss()
            zbarview_barcode_scanner.startSpot()
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_barcode_gallery -> {
                Matisse.from(this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(1)
                        .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(
                                resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideEngine())
                        .forResult(FeedbackActivity.REQUEST_CODE_CHOOSE)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onScanQRCodeOpenCameraError() {
        ToastUtils.show(this@BarCodeScannerActivity, "扫描失败，请重新扫描")
        Handler().postDelayed({ zbarview_barcode_scanner.startSpot() }, 2000)
    }

    override fun onScanQRCodeSuccess(result: String?) {
        Log.i(TAG, "result:" + result)
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        var toIntOrNull = result!!.toLongOrNull()
        if (toIntOrNull == null) {
            ToastUtils.show(this@BarCodeScannerActivity, "暂时只支持条形码")
            Handler().postDelayed({ zbarview_barcode_scanner.startSpot() }, 2000)
            return
        }
        val intent = Intent(this, ScannerResultActivity::class.java)
        intent.putExtra(URLs.kCodeInfo, result)
        intent.putExtra(URLs.kCodeType, "barcode")
        startActivity(intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        zbarview_barcode_scanner.showScanRect()
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            var mProgressDialog = ProgressDialog.show(this, "稍等", "正在扫描")
            zbarview_barcode_scanner.stopSpot()
            tv_barcode_scanning.visibility = View.VISIBLE
            val picturePath = ImageUtil.handleImageOnKitKat(Matisse.obtainResult(data)[0], this)
            LogUtil.d(TAG, "picturePath:::" + picturePath)
            object : AsyncTask<Void, Void, String>() {
                override fun doInBackground(vararg params: Void): String {
                    return QRCodeDecoder.syncDecodeQRCode(picturePath)
                }

                override fun onPostExecute(result: String) {
                    mProgressDialog.dismiss()
                    if (TextUtils.isEmpty(result) || "".equals(result)) {
                        ToastUtils.show(this@BarCodeScannerActivity, "未发现条形码")
                    } else {
                        onScanQRCodeSuccess(result)
                    }
                    tv_barcode_scanning.visibility = View.GONE
                    zbarview_barcode_scanner.startSpot()
                }
            }.execute()
        }
    }

    /**
     * 改变手电筒按钮 开/关 样式
     */
    fun checkLightStatus(status: Boolean, tv: TextView, checkBox: CheckBox) {
        if (status) {
            tv.setTextColor(resources.getColor(R.color.co7_syr))
            tv.text = "关闭手电筒"
        } else {
            tv.setTextColor(resources.getColor(R.color.co10_syr))
            tv.text = "打开手电筒"
        }
        checkBox.isChecked = status
    }
}


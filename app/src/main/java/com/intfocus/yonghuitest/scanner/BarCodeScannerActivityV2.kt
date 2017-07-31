package com.intfocus.yonghuitest.scanner

import android.content.Intent
import android.graphics.Typeface
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.Toast
import cn.bingoogolapple.photopicker.activity.BGAPhotoPickerActivity
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.intfocus.yonghuitest.R
import com.intfocus.yonghuitest.util.LogUtil
import com.intfocus.yonghuitest.util.QRCodeDecoder
import com.intfocus.yonghuitest.util.URLs
import com.zbl.lib.baseframe.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_bar_code_scanner_v2.*
import kotlinx.android.synthetic.main.popup_input_barcode.view.*


class BarCodeScannerActivityV2 : AppCompatActivity(), QRCodeView.Delegate, View.OnClickListener {

    companion object {
        val TAG = "hjjzz"
        val REQUEST_CODE_CHOOSE = 1
    }

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
            if (cb_barcode_light.isChecked) {
                tv_barcode_light.text = "打开手电筒"
                cb_barcode_light.isChecked = false
                tv_barcode_light.setTextColor(resources.getColor(R.color.co10_syr))
                zbarview_barcode_scanner.closeFlashlight()
            } else {
                tv_barcode_light.text = "关闭手电筒"
                cb_barcode_light.isChecked = true
                tv_barcode_light.setTextColor(resources.getColor(R.color.co7_syr))
                zbarview_barcode_scanner.openFlashlight()
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
        }
        iv_barcode_back.setOnClickListener {
            finish()
        }

        //popupWindows中打开手电筒点击事件
        view!!.ll_btn_input_barcode_light_switch.setOnClickListener {
            if (view!!.cb_input_barcode_light.isChecked) {
                view!!.tv_input_barcode_light.setTextColor(resources.getColor(R.color.co10_syr))
                view!!.cb_input_barcode_light.isChecked = false
                view!!.tv_input_barcode_light.text = "打开手电筒"
                zbarview_barcode_scanner.closeFlashlight()
            } else {
                view!!.tv_input_barcode_light.setTextColor(resources.getColor(R.color.co7_syr))
                view!!.cb_input_barcode_light.isChecked = true
                view!!.tv_input_barcode_light.text = "关闭手电筒"
                zbarview_barcode_scanner.openFlashlight()
            }
        }
        view!!.btn_input_barcode_confirm.setOnClickListener {
            var trim = view!!.et_input_barcode.text.toString()
            if (trim == "") {
                view!!.ll_input_barcode_notice.visibility = View.VISIBLE
                view!!.ll_input_barcode_notice.postDelayed({
                    view!!.ll_input_barcode_notice.visibility = View.GONE
                }, 2000)
                return@setOnClickListener
            }

            val intent = Intent(this, ScannerResultActivity::class.java)
            intent.putExtra(URLs.kCodeInfo, trim)
            intent.putExtra(URLs.kCodeType, "input")
            startActivity(intent)
            finish()
        }

        view!!.iv_input_barcode_back.setOnClickListener {
            popupWindow!!.dismiss()
            zbarview_barcode_scanner.startSpot()
        }

    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_barcode_gallery -> {
                startActivityForResult(BGAPhotoPickerActivity.newIntent(this, null, 1, null, false), REQUEST_CODE_CHOOSE)
            }

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onScanQRCodeOpenCameraError() {
        ToastUtil.showToast(this, "扫描失败，请重新扫描")
        Handler().postDelayed({ zbarview_barcode_scanner.startSpot() }, 2000)
    }

    override fun onScanQRCodeSuccess(result: String?) {
        Log.i(TAG, "result:" + result)
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        var toIntOrNull = result!!.toLongOrNull()
        if (toIntOrNull == null) {
            ToastUtil.showToast(this, "暂时只支持条形码")
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
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            zbarview_barcode_scanner.showScanRect()
            val picturePath = BGAPhotoPickerActivity.getSelectedImages(data)[0]
            LogUtil.d(TAG, "picturePath:::" + picturePath)
            object : AsyncTask<Void, Void, String>() {
                override fun doInBackground(vararg params: Void): String {
                    return QRCodeDecoder.syncDecodeQRCode(picturePath)
                }
                override fun onPostExecute(result: String) {
                    if ("".equals(result)) {
                        Toast.makeText(this@BarCodeScannerActivityV2, "未发现二维码", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@BarCodeScannerActivityV2, result, Toast.LENGTH_SHORT).show()
                    }
                }
            }.execute()

        }
    }
}


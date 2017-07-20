package com.intfocus.yh_android.scanner

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.zxing.Result
import com.zbl.lib.baseframe.utils.ToastUtil
import me.dm7.barcodescanner.zxing.ZXingScannerView

class QueryOptionsScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {
    var mScannerView: ZXingScannerView? = null

    override fun handleResult(p0: Result?) {

        ToastUtil.showToast(this, "p0!!.text:::" + p0!!.text)
        ToastUtil.showToast(this, "p0!!.barcodeFormat.toString():::" + p0!!.barcodeFormat.toString())

//        mScannerView?.resumeCameraPreview(this)
        when {
            p0!!.barcodeFormat.toString().equals("EAN_13") -> {
                var mData: Bundle = Bundle()
                mData.putString("barcode_value", p0!!.text)
                intent.putExtra("data",mData)
                setResult(QUERY_OPTIONS_SCANNER_ACTIVITY_RESULTCODE_EAN_13, intent)
            }
            p0!!.barcodeFormat.toString().equals("QR_CODE") -> {
                var mData: Bundle = Bundle()
                mData.putString("barcode_value", p0!!.text)
                intent.putExtra("data",mData)
                setResult(QUERY_OPTIONS_SCANNER_ACTIVITY_RESULTCODE_QR_CODE, intent)
            }
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)

    }

    override fun onResume() {
        super.onResume()
        mScannerView?.setResultHandler(this)
        mScannerView?.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView?.stopCamera()
    }

    companion object {
        private val TAG = "hjjzz"
        val QUERY_OPTIONS_SCANNER_ACTIVITY_RESULTCODE_EAN_13 = 1
        val QUERY_OPTIONS_SCANNER_ACTIVITY_RESULTCODE_QR_CODE = 2
    }
}

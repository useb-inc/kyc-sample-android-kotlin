package com.example.kyc_kotlin_sample

import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView


class WebBridge {

//    private val TAG:String = "AdroidBridge"
    private var mAppView: WebView? = null
    private var mContext: MainActivity? = null
    private val handler = android.os.Handler()

    fun WebBridge(_mAppView: WebView?, _mContext: MainActivity?) {
        mAppView = _mAppView
        mContext = _mContext
    }

    @JavascriptInterface
    fun receive(data: String) {
//        Log.d("result data:", data)
    }
}
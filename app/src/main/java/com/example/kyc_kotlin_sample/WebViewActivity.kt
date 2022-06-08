package com.example.kyc_kotlin_sample;

import android.os.Bundle;
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*

class  WebViewActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        webview.settings.javaScriptEnabled = true
        webview.loadUrl("https://kyc.useb.co.kr/auth")
        webview.webViewClient = WebViewClient()
        webview.webChromeClient = WebChromeClient()
    }
    override fun onBackPressed() {
        if (webview.canGoBack())
        {
            webview.goBack()
        }
        else
        {
            finish()
        }
    }
}

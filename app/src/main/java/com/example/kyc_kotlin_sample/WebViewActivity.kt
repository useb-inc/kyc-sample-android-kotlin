package com.example.kyc_kotlin_sample;

import android.os.Bundle
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_web_view.*
import org.json.JSONObject
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class  WebViewActivity : AppCompatActivity() {

//    private val TAG2 = "WebViewActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        val url = "https://kyc.useb.co.kr/auth"

        webview.settings.javaScriptEnabled = true
        webview.webViewClient = WebViewClient()
        webview.webChromeClient = WebChromeClient()
        webview.addJavascriptInterface(WebBridge(), "android")

        // 인코딩 - javascript의 encodeURIComponent메소드를 java형식으로 만듦
        var encodedJson = URLEncoder.encode(getData().toString(), StandardCharsets.UTF_8.name())
        encodeURIComponent(encodedJson)
        var encodedData: String = encodedJson.toBase64()
//        Log.d("encoded : ", encodedData)

        webview.run {
            webview.loadUrl(url)
            webview.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    handler.post {
                        webview.loadUrl("javascript:alcherakycreceive('" + encodedData +"')")
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (webview.canGoBack())
            webview.goBack()
        else
            finish()
    }

    private fun getData(): JSONObject {

        val birth = intent.getStringExtra("birth")
        val name = intent.getStringExtra("name")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val email = intent.getStringExtra("email")
        return dataToJson(birth, name, phoneNumber, email)
    }

    private fun dataToJson (birth:String?, name:String?, phoneNumber:String?, email:String?) : JSONObject {

        val jsonObject = JSONObject()
        jsonObject.put("customer_id", "12")
        jsonObject.put("id", "demoUser")
        jsonObject.put("key", "demoUser0000!")
        jsonObject.put("name", name)
        jsonObject.put("birthday",  birth)
        jsonObject.put("phone_number", phoneNumber)
        jsonObject.put("email", email)

//        Log.d("첫번째 : ", jsonObject.toString())
        return jsonObject
    }

    private fun encodeURIComponent(encoded:String):String{

        var encodedURI = encoded
        encodedURI = encodedURI.replace("\\+".toRegex(), "%20")
        encodedURI = encodedURI.replace("%21".toRegex(), "!")
        encodedURI = encodedURI.replace("%27".toRegex(), "'")
        encodedURI = encodedURI.replace("%28".toRegex(), "(")
        encodedURI = encodedURI.replace("%29".toRegex(), ")")
        encodedURI = encodedURI.replace("%7E".toRegex(), "~")
        return encodedURI
    }

    private fun String.toBase64(): String {

        return String(
            android.util.Base64.encode(this.toByteArray(), android.util.Base64.DEFAULT),
            StandardCharsets.UTF_8
        )
    }
}

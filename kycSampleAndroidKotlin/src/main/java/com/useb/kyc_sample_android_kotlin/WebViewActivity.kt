package com.useb.kyc_sample_android_kotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder

class WebViewActivity : AppCompatActivity() {
    private var webview: WebView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        WebView.setWebContentsDebuggingEnabled(true)

        webview = findViewById(R.id.webview)
        webview?.apply {
            settings.javaScriptEnabled = true
            webViewClient = WebViewClient()
            addJavascriptInterface(this@WebViewActivity, "alcherakyc")
        }

        // 카메라 프리뷰 성능
        webview?.clearCache(true)
        webview?.clearHistory()

        val url = "https://kyc.useb.co.kr/auth"
        val param = createParam()

        val handler = Handler(Looper.getMainLooper())
        handler.post {
            cameraAuthRequest()
            webview?.loadUrl(url)
            webview?.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    webview?.loadUrl("javascript:alcherakycreceive('$param')")
                }
            }
        }
    }

    private fun createParam(): String {
        val paramObj = JSONObject().apply {
            try {
                put("customer_id", "2")
                put("id", "demoUser")
                put("key", "demoUser0000!")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        return encodeJson(paramObj)
    }

    private fun encodeJson(data: JSONObject): String {
        val uriEncodedData = encodeURIComponent(data.toString())
        return Base64.encodeToString(uriEncodedData.toByteArray(), 0)
    }

    private fun encodeURIComponent(encoded: String): String {
        return try {
            URLEncoder.encode(encoded, "UTF-8")
                .replace("\\+".toRegex(), "%20")
                .replace("\\%21".toRegex(), "!")
                .replace("\\%27".toRegex(), "'")
                .replace("\\%28".toRegex(), "(")
                .replace("\\%29".toRegex(), ")")
                .replace("\\%7E".toRegex(), "~")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }
    }

    private fun cameraAuthRequest() {
        webview = findViewById(R.id.webview)
        val ws: WebSettings = webview!!.settings
        ws.mediaPlaybackRequiresUserGesture = false

        webview?.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                // API레벨이 21이상인 경우
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    request?.let { permissionRequest ->
                        val requestedResources = permissionRequest.resources
                        for (r in requestedResources) {
                            if (r == PermissionRequest.RESOURCE_VIDEO_CAPTURE) {
                                permissionRequest.grant(arrayOf(PermissionRequest.RESOURCE_VIDEO_CAPTURE))
                                break
                            }
                        }
                    }
                }
            }
        }

        val cameraPermissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 1000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(
                    this,
                    "카메라/갤러리 접근 권한이 없습니다. 권한 허용 후 이용해주세요. no access permission for camera and gallery.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    @JavascriptInterface
    fun receive(data: String) {
        try {
            val decodedData = decodedReceiveData(data)
            val jsonObject = JSONObject(decodedData)
            val resultData = jsonObject.getString("result")

            when (resultData) {
                "success" -> {
                    Log.d("success", "KYC 작업이 성공했습니다.")
                }
                "failed" -> {
                    Log.d("failed", "KYC 작업이 실패했습니다.")
                }
                "complete" -> {
                    Log.d("complete", "KYC가 완료되었습니다.")
                }
                "close" -> {
                    Log.d("close", "KYC가 완료되지 않았습니다.")
                }
            }

            finish()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    private fun decodedReceiveData(data: String): String {
        val decoded = String(Base64.decode(data, 0))
        return decodeURIComponent(decoded)
    }

    private fun decodeURIComponent(decoded: String): String {
        return try {
            URLDecoder.decode(decoded, "UTF-8")
                .replace("%20", "\\+")
                .replace("!", "\\%21")
                .replace("'", "\\%27")
                .replace("\\(", "\\%28")
                .replace("\\)", "\\%29")
                .replace("~", "\\%7E")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            ""
        }
    }
}
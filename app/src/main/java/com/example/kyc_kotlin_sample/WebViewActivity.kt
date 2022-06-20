package com.example.kyc_kotlin_sample;

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
        webview.addJavascriptInterface(WebBridge(), "alcherakyc")

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

                    // 카메라 권한 요청
                    val cameraPermissionCheck = ContextCompat.checkSelfPermission(this@WebViewActivity, android.Manifest.permission.CAMERA)
                    if (cameraPermissionCheck != PackageManager.PERMISSION_GRANTED) { // 권한이 없는 경우
                        ActivityCompat.requestPermissions(this@WebViewActivity, arrayOf(android.Manifest.permission.CAMERA), 1000)
                    } else { //권한이 있는 경우
                        val REQUEST_IMAGE_CAPTURE = 1
                        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                            takePictureIntent.resolveActivity(packageManager)?.also {
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@WebViewActivity, "카메라/갤러리 접근 권한이 없습니다. 권한 허용 후 이용해주세요. no access permission for camera and gallery.", Toast.LENGTH_SHORT).show()
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

        val birthday = intent.getStringExtra("birthday")
        val name = intent.getStringExtra("name")
        val phoneNumber = intent.getStringExtra("phoneNumber")
        val email = intent.getStringExtra("email")
        return dataToJson(birthday, name, phoneNumber, email)
    }

    private fun dataToJson (birthday:String?, name:String?, phoneNumber:String?, email:String?) : JSONObject {

        val jsonObject = JSONObject()
        jsonObject.put("customer_id", "12")
        jsonObject.put("id", "demoUser")
        jsonObject.put("key", "demoUser0000!")
        jsonObject.put("name", name)
        jsonObject.put("birthday",  birthday)
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

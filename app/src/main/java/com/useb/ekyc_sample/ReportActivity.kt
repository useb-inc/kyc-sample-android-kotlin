package com.useb.ekyc_sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import org.json.JSONException
import java.lang.Exception

class ReportActivity : AppCompatActivity() {

    private var result: String? = ""
    private var detail: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)
        try {
            data
        } catch (e: Exception) {
            e.printStackTrace()
        }
        setData(detail, result)
    }

    @get:Throws(JSONException::class)
    private val data: Unit
        private get() {
            detail = intent.getStringExtra("detail")
            result = intent.getStringExtra("result")
        }

    private fun setData(detail: String?, result: String?) {
        val detailTv = findViewById<TextView>(R.id.detail)
        val resultTv = findViewById<TextView>(R.id.result)
        detailTv.text = detail
        resultTv.text = result
    }
}
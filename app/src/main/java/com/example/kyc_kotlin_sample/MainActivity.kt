package com.example.kyc_kotlin_sample

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.kyc_kotlin_sample.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val secondIntent = Intent(this, WebViewActivity::class.java)

        run_button.setOnClickListener {
            sendDataToWebview(secondIntent)
            startActivity(secondIntent)
        }
    }

    private fun sendDataToWebview(secondIntent:Intent){

        val yearStr: String = binding.year.text.toString()
        val monthStr: String = binding.month.text.toString()
        val dayStr: String = binding.day.text.toString()
        val birthday = if(yearStr == "" || monthStr == "" || dayStr == "") ""
        else "$yearStr-$monthStr-$dayStr"

        val name : String= binding.name.text.toString()
        val phoneNumber : String= binding.phoneNumber.text.toString()
        val email : String= binding.email.text.toString()

        secondIntent.putExtra("birthday",birthday)
        secondIntent.putExtra("name",name)
        secondIntent.putExtra("phoneNumber",phoneNumber)
        secondIntent.putExtra("email",email)
    }
}
package com.useb.kyc_sample_android_kotlin

import android.text.TextUtils
import java.util.regex.Pattern

class InputValidator {

    fun isNullOrEmpty(string: String?): Boolean {
        return TextUtils.isEmpty(string)
    }

    fun isValidEmail(input: String?): Boolean {
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(input)
        return matcher.matches()
    }
}
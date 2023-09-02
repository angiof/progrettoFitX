package com.app.progrettofitx.utils

import android.content.Context
import androidx.fragment.app.Fragment


fun Fragment.saveInteger(context: Context, key: String, value: Int) {
    val sharedPreferences = context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt(key, value)
    editor.apply()
}

fun Fragment.getInteger(context: Context, key: String): Int {
    val sharedPreferences = context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getInt(key, -1)
}
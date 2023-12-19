package com.app.progrettofitx.utils

import android.content.Context
import androidx.fragment.app.Fragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Fragment.saveInteger(context: Context, key: String, value: Int) {
    val sharedPreferences =
        context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putInt(key, value)
    editor.apply()
}

fun Fragment.getInteger(context: Context, key: String): Int {
    val sharedPreferences =
        context.getSharedPreferences("my_shared_preferences", Context.MODE_PRIVATE)
    return sharedPreferences.getInt(key, -1)
}

fun Fragment.convertTimestampsToFormattedDates(
    startDateTimestamp: Long,
    endDateTimestamp: Long,
    dateFormat: String = "yyyy-MM-dd"
): Pair<String, String> {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    val startDate = formatter.format(Date(startDateTimestamp))
    val endDate = formatter.format(Date(endDateTimestamp))
    return Pair(startDate, endDate)
}
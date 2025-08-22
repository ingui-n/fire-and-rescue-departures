package com.android.fire_and_rescue_departures.helpers

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

class ToastHelper(private val context: Context) {
    fun showShortToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    fun showLongToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}

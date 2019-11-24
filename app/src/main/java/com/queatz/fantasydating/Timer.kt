package com.queatz.fantasydating

import android.os.Handler
import android.os.Looper
import com.queatz.on.On

class Timer constructor(private val on: On) {

    private val handler = Handler(Looper.getMainLooper())

    fun post(runnable: Runnable, delayMs: Long = 0) {
        handler.postDelayed(runnable, delayMs)
    }

    fun remove(runnable: Runnable) {
        handler.removeCallbacks(runnable)
    }
}
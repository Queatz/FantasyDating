package com.queatz.fantasydating

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.queatz.fantasydating.features.ViewFeature
import com.queatz.on.On

abstract class BaseActivity : AppCompatActivity() {
    protected val on = On()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)

        on<ViewFeature>().activity = this
    }

    override fun onDestroy() {
        super.onDestroy()
        on.off()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        on<MediaRequest>().onActivityResult(requestCode, resultCode, data)
    }
}
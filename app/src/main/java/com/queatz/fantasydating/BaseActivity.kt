package com.queatz.fantasydating

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.queatz.fantasydating.features.ViewFeature
import com.queatz.on.On
import io.reactivex.subjects.BehaviorSubject

abstract class BaseActivity : AppCompatActivity() {
    protected val on = On()

    val lifecycle = BehaviorSubject.create<LifecycleEvent>()

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)

        on<ContextFeature>().context = application
        on<ViewFeature>().activity = this
    }

    override fun onResume() {
        super.onResume()
        lifecycle.onNext(LifecycleEvent.Resume)
    }

    override fun onPause() {
        super.onPause()
        lifecycle.onNext(LifecycleEvent.Pause)
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

enum class LifecycleEvent {
    Pause,
    Resume
}
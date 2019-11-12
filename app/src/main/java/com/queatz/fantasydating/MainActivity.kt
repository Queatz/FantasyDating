package com.queatz.fantasydating

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.queatz.fantasydating.features.*
import com.queatz.on.On


class MainActivity : AppCompatActivity() {

    private val on = On()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        on<ViewFeature>().activity = this
        on<GesturesFeature>().start()
        on<FeedFeature>().start()
        on<StoryFeature>().start()
        on<DiscoveryPreferencesFeature>().start()
        on<MoreOptionsFeature>().start()
        on<WalkthroughFeature>().start()
        on<PeopleFeature>().start()
    }

    override fun onDestroy() {
        super.onDestroy()
        on.off()
    }

    override fun onBackPressed() = when {
        on<StoryFeature>().onBackPressed() -> {}
        on<EditorFeature>().isOpen -> on<EditorFeature>().cancel()
        on<MoreOptionsFeature>().isOpen -> on<MoreOptionsFeature>().close()
        on<LayoutFeature>().onBackPressed() -> {}
        else -> super.onBackPressed()
    }
}

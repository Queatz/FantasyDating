package com.queatz.fantasydating

import android.content.Intent
import android.os.Bundle
import com.queatz.fantasydating.features.*


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        on<LayoutFeature>().start()
        on<GesturesFeature>().start()
        on<FeedFeature>().start()
        on<StoryFeature>().start()
        on<DiscoveryPreferencesFeature>().start()
        on<MoreOptionsFeature>().start()
        on<WalkthroughFeature>().start()
        on<CompleteProfileFeature>().start()
        on<BossFeature>().start()

        if (intent?.let { handle(it) } != true) {
            on<PeopleFeature>().start()
        } else {
            on<State>().ui = on<State>().ui.copy(showFeed = false)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.let { handle(it) }
    }

    private fun handle(intent: Intent): Boolean {
        intent.getStringExtra(NavigationFeature.ExtraPersonId)?.let {
            on<PeopleFeature>().show(it)
            return true
        }

        return false
    }

    override fun onBackPressed() = when {
        on<EditProfileFeature>().onBackPressed() -> {}
        on<StoryFeature>().onBackPressed() -> {}
        on<EditorFeature>().isOpen -> on<EditorFeature>().cancel()
        on<MoreOptionsFeature>().isOpen -> on<MoreOptionsFeature>().close()
        on<LayoutFeature>().onBackPressed() -> {}
        else -> super.onBackPressed()
    }
}

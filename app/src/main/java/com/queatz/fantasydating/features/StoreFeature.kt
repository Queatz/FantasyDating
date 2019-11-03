package com.queatz.fantasydating.features

import com.queatz.fantasydating.models.MyObjectBox
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import io.objectbox.Box
import io.objectbox.BoxStore
import kotlin.reflect.KClass

class StoreFeature constructor(private val on: On) : OnLifecycle {

    companion object {
        private var boxStore: BoxStore? = null
    }

    private lateinit var boxStore: BoxStore

    override fun on() {
        if (StoreFeature.boxStore == null) {
            StoreFeature.boxStore = MyObjectBox.builder().androidContext(on<ViewFeature>().activity.applicationContext).build()
        }

        boxStore = StoreFeature.boxStore!!
    }

    fun <T : Any> get(clazz: KClass<T>): Box<T> = boxStore.boxFor(clazz.java)
}

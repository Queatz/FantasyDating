package com.queatz.fantasydating.features

import android.text.TextWatcher
import androidx.core.widget.doOnTextChanged
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.queatz.fantasydating.*
import com.queatz.fantasydating.ui.StyleAdapter
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.add_style_modal.*
import kotlinx.android.synthetic.main.add_style_modal.view.*
import kotlinx.android.synthetic.main.fullscreen_modal.*

class EditPrefsFeature constructor(private val on: On) {

    private var searchEditTextListener: TextWatcher? = null
    private var searchCallback: CallbackHandle? = null
    private lateinit var adapter: StyleAdapter

    fun edit() {
        on<ViewFeature>().with {
            adapter = StyleAdapter(on) { style, longPress ->
                if (longPress) return@StyleAdapter

                on<LayoutFeature>().canCloseFullscreenModal = true
                fullscreenMessageText.text = "<b>${style.name}</b>${style.about?.takeIf { !it.isNullOrBlank() }?.let { "<br />$it" } ?: ""}<br /><br />${if (style.preference?.dismissed == true) "<tap data=\"undismiss\">Do</tap>" else "Show <tap data=\"promote\">More</tap>, <tap data=\"demote\">Less</tap>, or <tap data=\"dismiss\">Don't</tap>"} show people with this 氣<br /><br /><tap data=\"close\">Close</tap>"
                fullscreenMessageLayout.fadeIn()
                fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

                fullscreenMessageText.onLinkClick = {
                    when (it) {
                        "promote" -> { on<StyleApiFeature>().promote(style.id!!) { reload() } }
                        "demote" -> { on<StyleApiFeature>().demote(style.id!!) { reload() } }
                        "dismiss" -> { on<StyleApiFeature>().dismiss(style.id!!) { reload() } }
                        "undismiss" -> { on<StyleApiFeature>().undismiss(style.id!!) { reload() } }
                    }

                    fullscreenMessageLayout.fadeOut()
                }
            }

            reload()
            searchLayout.searchEditText.setText("")

            searchEditTextListener?.let { searchLayout.searchEditText.removeTextChangedListener(it) }
            searchEditTextListener = searchLayout.searchEditText.doOnTextChanged { _, _, _, _ -> reload() }

            searchLayout.searchRecyclerView.adapter = adapter
            searchLayout.searchRecyclerView.layoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)

            addStyleModalLayout.setOnClickListener {
                addStyleModalLayout.fadeOut()
            }

            searchLayout.setOnClickListener {
                searchLayout.fadeOut()
            }

            searchModalText.text = "<tap data=\"close\">Close</tap>"
            searchModalText.onLinkClick = {
                searchLayout.fadeOut()
            }

            searchLayout.fadeIn()
        }
    }

    private fun reload() {
        val search = on<ViewFeature>().with {  searchLayout }.searchEditText.text.toString().trim()

        if (search.isNotBlank()) {
            searchCallback?.cancel()
            searchCallback = on<Api>().searchStyles(search, true) {
                on<State>().person.current?.let { person ->
                    adapter.items = it.filter { style -> person.styles.all { it.id != style.id } }.toMutableList()
                }
            }
        } else {
            on<Api>().getStyles(true) {
                on<State>().person.current?.let { person ->
                    adapter.items = it.filter { style -> person.styles.all { it.id != style.id } }
                        .toMutableList()
                }
            }
        }
    }
}
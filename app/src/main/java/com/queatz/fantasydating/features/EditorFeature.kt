package com.queatz.fantasydating.features

import android.content.Context
import android.view.Gravity
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import com.queatz.fantasydating.visible
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import kotlinx.android.synthetic.main.activity_main.*

class EditorFeature constructor(private val on: On) : OnLifecycle {

    private var editorCallback: (String) -> Unit = {}

    override fun on() {
        on<ViewFeature>().with {
            editorDoneButton.setOnClickListener { confirm() }
            editorCancelButton.setOnClickListener { cancel() }

            editor.setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        confirm()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    val isOpen get() = on<ViewFeature>().with { editorLayout }.visible

    fun open(text: String = "", gravity: Int = Gravity.CENTER, callback: (String) -> Unit) {
        on<ViewFeature>().with {
            editor.setText(on<MyProfileFeature>().myProfile.name)
            editorLayout.visible = true

            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            editor.requestFocus()
            editor.selectAll()

            editor.setText(text)
            editor.gravity = gravity

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editor, 0)
        }

        editorCallback = callback
    }

    fun confirm() {
        editorCallback.invoke(on<ViewFeature>().with { editor }.text.toString())
        cancel()
    }

    fun cancel() {
        on<ViewFeature>().with {
            editorLayout.visible = false

            window.clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(editor.windowToken, 0)
        }
    }
}
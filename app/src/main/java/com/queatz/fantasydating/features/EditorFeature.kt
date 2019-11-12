package com.queatz.fantasydating.features

import android.content.Context
import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.view.Gravity
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.widget.addTextChangedListener
import com.queatz.fantasydating.visible
import com.queatz.on.On
import com.queatz.on.OnLifecycle
import kotlinx.android.synthetic.main.activity_main.*

class EditorFeature constructor(private val on: On) : OnLifecycle {

    private var editorCallback: (String) -> Unit = {}
    private var prefix: String = ""

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

            editor.onSelectionChangeListener = { start, end ->
                if (start < prefix.length && editor.text.length >= prefix.length) {
                    editor.setSelection(prefix.length)
                }
            }

            editor.filters = arrayOf(object : InputFilter {
                override fun filter(
                    text: CharSequence,
                    start: Int,
                    end: Int,
                    dest: Spanned,
                    destStart: Int,
                    destEnd: Int
                ): CharSequence? {
                    if (dest.length < prefix.length) {
                        return null
                    }

                    if (destStart < prefix.length) {
                        return dest.subSequence(destStart, destEnd)
                    }

                    return null
                }
            })

            editor.addTextChangedListener({ _, _, _, _  -> }, { text, start, count, after ->
                if (prefix.isNotBlank() && text!!.startsWith(prefix).not()) {
                    editor.setText(prefix + text!!)
                    editor.setSelection(prefix.length)
                }
            })
        }
    }

    val isOpen get() = on<ViewFeature>().with { editorLayout }.visible

    fun open(text: String = "",
             gravity: Int = Gravity.CENTER,
             inputType: Int = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE,
             prefix: String = "",
             callback: (String) -> Unit) {
        on<ViewFeature>().with {
            this@EditorFeature.prefix = prefix

            editor.setText(text)
            editorLayout.visible = true

            window.addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN)
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            editor.gravity = gravity
            editor.inputType = inputType

            editor.requestFocus()

            if (prefix.isBlank()) {
                editor.selectAll()
            } else {
                editor.setSelection(prefix.length)
            }

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
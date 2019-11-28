package com.queatz.fantasydating.ui

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.util.Size
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.queatz.fantasydating.R
import io.reactivex.subjects.BehaviorSubject
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode


class FancyTextView : TextView {

    private var bold: Boolean = false
    private var highlight: Boolean = true
    private var textChangeLock: Boolean = false
    private var theme: Resources.Theme? = null

    val firstLineWidth = BehaviorSubject.createDefault(0)

    var onLinkClick: (String) -> Unit = {}

    constructor(context: Context) : super(context) { initialize(
        context
    ) }
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) { initialize(
        context,
        attrs
    ) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { initialize(
        context,
        attrs,
        defStyleAttr
    ) }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) { initialize(context, attrs, defStyleAttr, defStyleRes) }

    private fun initialize(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0,
        defStyleRes: Int = 0
    ) {
        bold = true
        var lineSpacing = 1.4f
        theme = context.theme
        setTextAppearance(R.style.Text_Medium)

        attrs?.let {
            val styledAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.FancyTextView, defStyleAttr, defStyleRes)
            val textSize = styledAttrs.getDimension(R.styleable.FancyTextView_android_textSize, 0f)
            val textColor = styledAttrs.getColor(R.styleable.FancyTextView_android_textColor, -1)
            bold = !styledAttrs.getBoolean(R.styleable.FancyTextView_thin, false)
            highlight = styledAttrs.getBoolean(R.styleable.FancyTextView_highlight, true)
            lineSpacing = styledAttrs.getFloat(R.styleable.FancyTextView_android_lineSpacingMultiplier, lineSpacing)
            styledAttrs.recycle()

            if (textSize > 0) {
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
            }

            if (textColor > 0) {
                setTextColor(textColor)
            }
        }

        if (bold) {
            paintFlags = paintFlags or Paint.FAKE_BOLD_TEXT_FLAG
        }

        setLineSpacing(0f, lineSpacing)

        val pad = resources.getDimensionPixelSize(R.dimen.pad)
        setShadowLayer(pad.toFloat(), 0f, 0f, 0)

        highlightColor = Color.TRANSPARENT
        setLinkTextColor(resources.getColor(R.color.colorPrimary, theme))
    }

    override fun onTextChanged(
        text: CharSequence,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (textChangeLock) {
            return
        }

        scrollTo(0, 0)

        var parsed = ""
        var links = mutableListOf<Link>()
        var bolds = mutableListOf<Link>()
        var underlines = mutableListOf<Link>()

        Jsoup.parse(text.toString()).body().childNodes().forEach { node ->
            if (node is Element) {
                when (node.tagName()) {
                    "tap" -> links.add(
                        Link(
                            node.attr("data"),
                            node.attr("thin")?.toBoolean() ?: false,
                            Size(parsed.length, parsed.length + node.text().length)
                        )
                    )
                    "br" -> parsed += "\n"
                    "b" -> bolds.add(Link(pos = Size(parsed.length, parsed.length + node.text().length)))
                    "u" -> underlines.add(Link(pos = Size(parsed.length, parsed.length + node.text().length)))
                }

                parsed += node.text()
            } else if (node is TextNode) {
                parsed += node.text()
            }
        }

        val pad = resources.getDimensionPixelSize(R.dimen.pad)

        textChangeLock = true

        super.setText(SpannableString(parsed).apply {
            setSpan(
                BackgroundSpan(
                    resources.getColor(
                        if (highlight) R.color.white else android.R.color.transparent,
                        theme
                    ), pad * .75f
                ) { line, width ->
                    if (line == 0) {
                        firstLineWidth.onNext(width)
                    }
                }, 0, parsed.length, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)

            bolds.forEach { link ->
                setSpan(StyleSpan(Typeface.BOLD), link.pos.width, link.pos.height, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            underlines.forEach { link ->
                setSpan(UnderlineSpan(), link.pos.width, link.pos.height, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            links.forEach { link ->
                setSpan(object : ClickableSpan() {
                    override fun onClick(view: View) {
                        onLinkClick.invoke(link.data)
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.color = ds.linkColor

                        if (!link.thin) {
                            ds.flags = ds.flags or Paint.FAKE_BOLD_TEXT_FLAG
                        }
                    }
                }, link.pos.width, link.pos.height, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        })

        movementMethod = LinkMovementMethod.getInstance()

        textChangeLock = false
    }
}

data class Link constructor(
    val data: String = "",
    val thin: Boolean = false,
    val pos: Size
)

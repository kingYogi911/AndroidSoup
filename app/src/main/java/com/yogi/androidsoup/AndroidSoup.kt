package com.yogi.androidsoup

import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.*
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.yogi.androidsoup.data.HEADING_SIZES
import com.yogi.androidsoup.spans.EmptySpan
import com.yogi.androidsoup.spans.ListItemSpan
import com.yogi.androidsoup.spans.TableSpan
import com.yogi.androidsoup.styles.Empty
import com.yogi.androidsoup.styles.TextStyle
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.concurrent.atomic.AtomicInteger

class AndroidSoup {
    companion object {
        fun parse(html: String): SpannableStringBuilder {
            val formatted = html.replace("&nbsp;".toRegex(), " ")
            return Jsoup.parse(formatted).body()
                .getSpanned(
                    imageProvider = null,
                    replaceDelayed = { a, b -> run {} },
                    delayedCount = AtomicInteger()
                ) as SpannableStringBuilder
        }

        fun parse(
            html: String,
            imageProvider: ImageProvider,
            textView: TextView?,
            onComplete: ((SpannableStringBuilder) -> Unit)? = null
        ) {
            val formatted = html.replace("&nbsp;".toRegex(), " ")
            var result: SpannableStringBuilder? = null
            val delayedCount = AtomicInteger(0)
            fun delayedReplacement(obj: Any, replacement: Any) {
                Log.e("AndroidSoup", "delayed replacement execution")
                result?.let {
                    val s = it.getSpanStart(obj)
                    val e = it.getSpanEnd(obj)
                    it.removeSpan(obj)
                    it.setSpan(replacement, s, e, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    textView?.text = result
                    if (delayedCount.decrementAndGet() == 0) {
                        onComplete?.invoke(result!!)
                    }
                }
            }
            result = Jsoup.parse(formatted).body()
                .getSpanned(
                    imageProvider = imageProvider,
                    replaceDelayed = { a, b -> delayedReplacement(a, b) },
                    delayedCount = delayedCount
                ) as SpannableStringBuilder
            textView?.text = result
            if (delayedCount.get() == 0) {
                onComplete?.invoke(result)
            }
        }

        private operator fun SpannableStringBuilder.plusAssign(spanned: CharSequence) {
            append(spanned)
        }

        fun Element.getSpanned(
            styles: MutableList<TextStyle> = mutableListOf(),
            imageProvider: ImageProvider? = null,
            replaceDelayed: ((Any, Any) -> Unit),
            delayedCount: AtomicInteger
        ): Spannable {
            val newStyles = mutableListOf<TextStyle>().apply {
                addAll(styles)
            }
            val builder = SpannableStringBuilder("")
            when {
                tagName() == "table" -> {
                    builder += "\n"
                    builder += getSpannedFromTableTag(this, newStyles)
                    builder += "\n"
                }
                tagName() == "img" -> {
                    builder += getImageFromImageTag(
                        this,
                        imageProvider,
                        replaceDelayed,
                        delayedCount
                    )
                }
                tagName() == "br" -> {
                    builder += "\n"
                }
                tagName() == "p" -> {
                    builder += iterativeSpans(
                        newStyles,
                        imageProvider,
                        replaceDelayed,
                        delayedCount
                    )
                    builder += "\n"
                }
                tagName() == "ol" -> {
                    builder += handleNumberedList(this, replaceDelayed, delayedCount)
                }
                tagName() == "ul" -> {
                    builder += handleUnorderedList(
                        this.getElementsByTag("li"),
                        replaceDelayed,
                        delayedCount
                    )
                }
                tagName() == "a" -> {
                    builder += SpannableStringBuilder(this.html()).apply {
                        setSpan(
                            URLSpan(this@getSpanned.attr("href")),
                            0,
                            length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                tagName() == "blockquote" -> {
                    builder += SpannableStringBuilder(this.html()).apply {
                        setSpan(QuoteSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                }
                "h[1-6]".toRegex().matches(tagName()) -> {
                    builder += SpannableStringBuilder(this.html()).apply {
                        setSpan(
                            RelativeSizeSpan(HEADING_SIZES[tagName()]!!),
                            0,
                            length,
                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                else -> {
                    Log.e("Un-Specific-Tag", "name : ${tagName()}")
                    TextStyle.fromTag(this.tagName())?.let { newStyles.add(it) }
                    if (this.hasAttr("style")) {
                        TextStyle.fromCss(this.attr("style")).let { newStyles.addAll(it) }
                    }
                    builder += iterativeSpans(
                        newStyles,
                        imageProvider,
                        replaceDelayed!!,
                        delayedCount
                    )
                }
            }
            return builder
        }

        private fun getSpanned(string: String, styles: List<TextStyle> = listOf()): Spanned {
            val spanned = SpannableStringBuilder(string)
            for (style in styles) {
                spanned.setSpan(
                    style.getSpan(),
                    0,
                    spanned.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            return spanned
        }

        private fun Element.iterativeSpans(
            styles: MutableList<TextStyle> = mutableListOf(),
            imageProvider: ImageProvider? = null,
            replaceDelayed: ((Any, Any) -> Unit),
            delayedCount: AtomicInteger
        ): CharSequence {
            val builder = SpannableStringBuilder("")
            builder += getSpanned("<${tagName()}>", listOf(Empty()))
            for (node in this.childNodes()) {
                builder += if (node is Element) {
                    Log.e("iterativeSpans", "node" + node.nodeName())
                    node.getSpanned(styles, imageProvider, replaceDelayed, delayedCount)
                } else {
                    getSpanned(node.toString(), styles)
                }
            }
            builder += getSpanned("</${tagName()}>", listOf(Empty()))
            return builder
        }

        private fun handleTag(tag: String): Spanned? {

            if (tag == "div") {

            } else if (tag == "span") {

            } else if (tag == "font") {

            } else if (tag == "tt") {

            }
            return null
        }

        private fun handleUnorderedList(
            liTags: Elements,
            replaceDelayed: (Any, Any) -> Unit,
            delayedCount: AtomicInteger
        ): Spannable {
            val builder = SpannableStringBuilder("<ul>\n").apply {
                setSpan(EmptySpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            liTags.forEach { element ->
                builder += element.getSpanned(
                    replaceDelayed = replaceDelayed,
                    delayedCount = delayedCount
                ).apply {
                    setSpan(ListItemSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                builder += "\n"
            }
            builder += SpannableString("</ul>").apply {
                setSpan(EmptySpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            return builder
        }

        private fun handleNumberedList(
            olElement: Element,
            replaceDelayed: (Any, Any) -> Unit,
            delayedCount: AtomicInteger
        ): Spannable {
            val liTags = olElement.getElementsByTag("li")
            val builder = SpannableStringBuilder("<ol>\n").apply {
                setSpan(EmptySpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            liTags.forEachIndexed { index, element ->
                builder += element.getSpanned(
                    replaceDelayed = replaceDelayed,
                    delayedCount = delayedCount
                ).apply {
                    setSpan(
                        ListItemSpan(index + 1),
                        0,
                        length,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                builder += "\n"
            }
            builder += SpannableString("</ol>").apply {
                setSpan(EmptySpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            return builder
        }

        private fun getImageFromImageTag(
            element: Element,
            imageProvider: ImageProvider?,
            replaceDelayed: ((Any, Any) -> Unit),
            delayedCount: AtomicInteger
        ): Spanned {
            val builder = SpannableStringBuilder(element.toString())
            val imageSpan: ImageSpan
            ContextCompat.getDrawable(App.context, android.R.drawable.gallery_thumb)!!.apply {
                val width: Int = intrinsicWidth
                val height: Int = intrinsicHeight
                setBounds(0, 0, if (width > 0) width else 0, if (height > 0) height else 0)
            }.let {
                imageSpan = ImageSpan(it)
                builder.setSpan(
                    imageSpan,
                    0,
                    builder.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                if (imageProvider != null) {
                    delayedCount.getAndIncrement()
                    imageProvider.requestImage(element.attr("src")) { drawable ->
                        if (drawable != null) {
                            val width: Int = drawable.intrinsicWidth
                            val height: Int = drawable.intrinsicHeight
                            drawable.setBounds(
                                0,
                                0,
                                if (width > 0) width else 0,
                                if (height > 0) height else 0
                            )
                            replaceDelayed.invoke(imageSpan,ImageSpan(drawable))
                        }else{
                            replaceDelayed.invoke(imageSpan, imageSpan)// agr image null ho to us case me callback ke liye
                        }
                    }
                }
            }
            return builder
        }

        private fun getSpannedFromTableTag(
            element: Element,
            inheritedStyles: List<TextStyle> = listOf(),
            onTableClick: ((TableSpan) -> Unit)? = null
        ): Spanned {
            val spanned = SpannableStringBuilder("$element".replace("[\n]+".toRegex(), ""))
            val tableSpan = TableSpan(element, inheritedStyles)
            spanned.setSpan(
                tableSpan,
                0,
                spanned.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            spanned.setSpan(
                object : ClickableSpan() {
                    override fun onClick(p0: View) {
                        onTableClick?.invoke(tableSpan)
                    }
                },
                0,
                spanned.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return spanned
        }
    }
}
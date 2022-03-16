package com.yogi.androidsoup.jsoupExtensions

import android.app.AlertDialog
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.BulletSpan
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import com.yogi.androidsoup.App
import com.yogi.androidsoup.ImageProvider
import com.yogi.androidsoup.MainActivity
import com.yogi.androidsoup.ViewUtils
import com.yogi.androidsoup.databinding.DilaogLayoutBinding
import com.yogi.androidsoup.spans.DotListSpan
import com.yogi.androidsoup.spans.EmptySpan
import com.yogi.androidsoup.spans.NumberedListSpan
import com.yogi.androidsoup.spans.TableSpan
import com.yogi.androidsoup.styles.Empty
import com.yogi.androidsoup.styles.TextStyle
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

const val TAG = "ElementExtensions"

operator fun SpannableStringBuilder.plusAssign(spanned: CharSequence) {
    append(spanned)
}

fun Element.getSpanned(
    styles: MutableList<TextStyle> = mutableListOf(),
    imageProvider: ImageProvider? = null
): Spannable {
    val newStyles = mutableListOf<TextStyle>().apply {
        addAll(styles)
    }
    val builder = SpannableStringBuilder("")
    when (this.tagName()) {
        "table" -> {
            builder += "\n"
            builder += getSpannedFromTableTag(this, newStyles)
            builder += "\n"
        }
        "img" -> {
            builder += getImageFromImageTag(this, imageProvider)
        }
        "p" -> {
            builder += iterativeSpans(newStyles, imageProvider)
            builder += "\n"
        }
        "ol" -> {
            builder += handleNumberedList(this.getElementsByTag("li"))
        }
        "ul" -> {
            builder += handleUnorderedList(this.getElementsByTag("li"))
        }
        else -> {
            Log.e("Un-Specific-Tag", "name : ${tagName()}")
            TextStyle.fromTag(this.tagName())?.let { newStyles.add(it) }
            if (this.hasAttr("style")) {
                TextStyle.fromCss(this.attr("style")).let { newStyles.addAll(it) }
            }
            builder += iterativeSpans(newStyles, imageProvider)
        }
    }
    return builder
}

fun handleUnorderedList(liTags: Elements): Spannable {
    val builder = SpannableStringBuilder("")
    liTags.forEach { element ->
        builder += element.getSpanned().apply {
            setSpan(DotListSpan(), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        builder += "\n"
    }
    return builder
}

fun handleNumberedList(liTags: Elements): Spannable {
    val startTag="<ol>\n"
    val builder = SpannableStringBuilder(startTag).apply {
        setSpan(EmptySpan(),0,startTag.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    liTags.forEachIndexed { index, element ->
        builder += element.getSpanned().apply {
            setSpan(NumberedListSpan(index + 1), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
        builder += "\n"
    }
    val endTag="</ol>"
    builder += SpannableString(endTag).apply {
        setSpan(EmptySpan(),0,endTag.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return builder
}

fun getSpannedNumberedList(): CharSequence {
    TODO("Not yet implemented")
}

fun getSpanned(string: String, styles: List<TextStyle> = listOf()): Spanned {
    val spanned = SpannableStringBuilder(string)
    for (style in styles) {
        spanned.setSpan(style.getSpan(), 0, spanned.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spanned
}

private fun Element.iterativeSpans(
    styles: MutableList<TextStyle> = mutableListOf(),
    imageProvider: ImageProvider? = null
): CharSequence {
    val builder = SpannableStringBuilder("")
    builder += getSpanned("<${tagName()}>", listOf(Empty()))
    for (node in this.childNodes()) {
        builder += if (node is Element) {
            Log.e("iterativeSpans", "node" + node.nodeName())
            node.getSpanned(styles, imageProvider)
        } else {
            getSpanned(node.toString(), styles)
        }
    }
    builder += getSpanned("</${tagName()}>", listOf(Empty()))
    return builder
}

private fun handleTag(tag: String): Spanned? {

    if (tag == "br") {

    } else if (tag == "p") {

    } else if (tag == "ul") {

    } else if (tag == "li") {

    } else if (tag == "div") {

    } else if (tag == "span") {

    } else if (tag == "font") {

    } else if (tag == "blockquote") {

    } else if (tag == "tt") {
        //return TypefaceSpan("monospace")
    } else if (tag == "a") {

    } else if (tag.length == 2 && Character.toLowerCase(tag[0]) == 'h' && tag[1] >= '1' && tag[1] <= '6') {
        //endHeading(mSpannableStringBuilder)
    }
    return null
}

private fun getImageFromImageTag(element: Element, imageProvider: ImageProvider?): Spanned {
    val builder = SpannableStringBuilder(element.toString())
    val image = imageProvider?.getImage()
        ?: ContextCompat.getDrawable(App.context, android.R.drawable.ic_delete)!!.apply {
            val width: Int = intrinsicWidth
            val height: Int = intrinsicHeight
            setBounds(0, 0, if (width > 0) width else 0, if (height > 0) height else 0)
        }
    builder.setSpan(
        ImageSpan(image),
        0,
        builder.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return builder
}

private fun getSpannedFromTableTag(
    element: Element,
    inheritedStyles: List<TextStyle> = listOf()
): Spanned {
    Log.e("getSpannedFromTableTag", element.toString().toString())
    val spanned = SpannableStringBuilder("$element".replace("[\n]+".toRegex(),""))
    spanned.getSpans<Any>(0, spanned.length).forEach {
        spanned.removeSpan(it)
    }
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
                DilaogLayoutBinding.inflate(LayoutInflater.from(MainActivity.staticContext)).let {
                    it.imageView.setImageDrawable(tableSpan.drawable)
                    AlertDialog.Builder(MainActivity.staticContext)
                        .setView(it.root)
                        .show()
                }
            }
        },
        0,
        spanned.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spanned
}
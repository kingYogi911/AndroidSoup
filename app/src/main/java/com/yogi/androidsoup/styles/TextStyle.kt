package com.yogi.androidsoup.styles

import android.graphics.Typeface
import android.text.style.*
import androidx.core.graphics.toColorInt
import com.yogi.androidsoup.data.ColorHolder
import com.yogi.androidsoup.spans.EmptySpan
import com.yogi.androidsoup.spans.FontSpan
import org.jsoup.nodes.Element
import java.lang.IndexOutOfBoundsException

interface TextStyle {
    fun getSpan(): Any

    companion object {
        fun fromTag(tag: String): TextStyle? {
            if (tag in listOf("strong", "b")) return Bold()
            if (tag in listOf("em", "cite", "dfn", "i")) return Italic()
            if (tag in listOf("big", "small")) return RelativeSize(tag)
            if (tag == "u") return UnderLine()
            if (tag in listOf("del", "s", "strike")) return StrikeThrough()
            if (tag == "sub") return SubScript()
            if (tag == "sup") return SuperScript()
            return null
        }

        fun fromCss(css :String):List<TextStyle> {
            val styles= mutableListOf<TextStyle>()
            css.trimIndent().split(";").forEach {
                try {
                    if (it.trim().isNotEmpty()) {
                        it.split(":").let { cssPV ->
                            val (property, value) = arrayOf(cssPV[0], cssPV[1])
                            when (property) {
                                "font-weight" -> if (value == "bold") Bold() else null
                                "text-decoration" -> if (value.contains("underline")) UnderLine() else null
                                else -> null
                            }?.let { textStyle: TextStyle ->
                                styles.add(textStyle)
                            }
                        }
                    }
                }catch (e:IndexOutOfBoundsException){
                    e.printStackTrace()
                }
            }
            return styles;
        }
    }
}

class TextColor(private val color: ColorHolder) : TextStyle {
    override fun getSpan(): Any = ForegroundColorSpan(color.colorHex.toColorInt())
}

class BackgroundColor(private val color: ColorHolder) : TextStyle {
    override fun getSpan(): Any = BackgroundColorSpan(color.colorHex.toColorInt())
}

class Bold : TextStyle {
    override fun getSpan() = StyleSpan(Typeface.BOLD)
}

class Italic : TextStyle {
    override fun getSpan(): Any = StyleSpan(Typeface.ITALIC)
}

class UnderLine : TextStyle {
    override fun getSpan(): Any = UnderlineSpan()
}

class RelativeSize(private val proportion: Float) : TextStyle {
    constructor(size: String) : this(if (size == "big") 1.25f else if (size == "small") 0.8f else 1.0f)

    override fun getSpan(): Any = RelativeSizeSpan(proportion)
}

class Font(private val typeface: Typeface) : TextStyle {
    override fun getSpan(): Any = FontSpan(typeface)
}

class StrikeThrough : TextStyle {
    override fun getSpan(): Any = StrikethroughSpan()
}

class SuperScript : TextStyle {
    override fun getSpan(): Any = SuperscriptSpan()
}

class SubScript : TextStyle {
    override fun getSpan(): Any = SubscriptSpan()
}

class Empty:TextStyle{
    override fun getSpan(): Any = EmptySpan()
}

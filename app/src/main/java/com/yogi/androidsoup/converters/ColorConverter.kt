package com.yogi.androidsoup.converters

import com.yogi.androidsoup.data.ColorHolder
import com.yogi.androidsoup.exceptions.ColorNotFoundException
import com.yogi.androidsoup.App
import com.yogi.androidsoup.R
import org.jsoup.nodes.Element


open class ColorConverter(/*private val context: Context*/) {
    companion object {
        val cssAttrsList = listOf<String>("color", "background-color")
        private val colorConverter = ColorConverter()
        fun convert(element: Element, attr: String): String {
            return try {
                attr.split(":").let {
                    it[0] + " : " + colorConverter.getHex(it[1])
                }
            } catch (e: ColorNotFoundException) {
                attr
            }
        }
    }

    private val colorsList = mutableListOf<ColorHolder>()

    init {
        val inputStream = App.context.resources.openRawResource(R.raw.css_colors_name_hex_dec)
        inputStream.bufferedReader().useLines { lines ->
            lines.forEach { line ->
                line.split(" ").let {
                    if (it.size == 3) {
                        colorsList.add(ColorHolder(it[0], it[1], it[2]))
                    }
                }
            }
        }
        println("Total Colors = ${colorsList.size}")
    }

    /**
     * return hex equivalent string starting with # i.e.-> #rrggbb
     * or throws ColorNotFoundException() if color name not found
     */
    fun getHex(name: String): String {
        colorsList.find { color -> color.colorName.equals(name, ignoreCase = true) }?.let {
            return it.colorHex
        }
        throw ColorNotFoundException()
    }

    /**
     * return rgb with decimal values separated by comma  i.e.-> red,green,blue ->eg 105,255,155
     * or throws ColorNotFoundException() if color name not found
     */
    fun getRgb(name: String): String {
        colorsList.find { color -> color.colorName.equals(name, ignoreCase = true) }?.let {
            return it.rgb
        }
        throw ColorNotFoundException()
    }
}


package com.example.purehtml.converters

import com.yogi.androidsoup.converters.ColorConverter
import org.jsoup.nodes.Element

class CssToTagConverter {
    companion object {
        private val tagMap = object : HashMap<String, String>() {
            override fun get(key: String): String? {
                return when(key){
                    "bold"->"b"
                    "underline"->"u"
                    else->null
                }
            }
        }
        val cssAttrsList= listOf<String>("font-weight","text-decoration")
        val colorConverter= ColorConverter()
        fun convert( element : Element,attr:String):String{
            attr.split(":")[1].split(" ").let{
                tagMap[it[0]]?.let { tag->
                    element.wrap("<$tag></$tag>")
                }
            }
            return ""
        }
    }
}
package com.yogi.androidsoup.spans

import android.content.ContextWrapper
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.text.Spanned
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.text.style.DynamicDrawableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AlertDialogLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import com.example.purehtml.utils.ViewUtils
import com.yogi.androidsoup.App
import com.yogi.androidsoup.MainActivity
import com.yogi.androidsoup.databinding.DilaogLayoutBinding
import com.yogi.androidsoup.databinding.TableLayoutBinding
import com.yogi.androidsoup.jsoupExtensions.getSpanned
import com.yogi.androidsoup.styles.TextStyle
import org.jsoup.nodes.Element


class TableSpan(tableElement: Element,inheritedStyles: List<TextStyle>) : ImageSpan(getDrawable(tableElement, inheritedStyles)){
    companion object {
         fun getDrawable(tableElement:Element,inheritedStyles: List<TextStyle>): Drawable {
            Log.i("TableSpan", tableElement.toString())
            if (tableElement.tagName() != "table")
                return ContextCompat.getDrawable(App.context, android.R.drawable.ic_delete)!!.apply {
                    setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                }
            else {
                tableElement.getElementsByTag("tbody").let { tbody ->
                    var (rowCount, colCount) = arrayOf(0, 0)
                    val rows = tbody[0].getElementsByTag("tr").also {
                        rowCount = it.size
                        Log.e("RowCount", "c = $rowCount")
                        if (it.size > 0) {
                            colCount = it[0].getElementsByTag("td").size
                        }
                    }
                    val binding=TableLayoutBinding.inflate(LayoutInflater.from(ContextWrapper(App.context)))
                    binding.gridView.numColumns = colCount
                    val items = mutableListOf<Spanned>()
                    for (row in rows) {
                        for (col in row.getElementsByTag("td")) {
                            items.add(
                                col.getSpanned(
                                    styles = mutableListOf<TextStyle>().apply {
                                        addAll(inheritedStyles)
                                    }
                                )
                            )
                        }
                    }
                    binding.gridView.adapter = TableAdapter(items)
                    return (BitmapDrawable(
                        App.context.resources,
                        ViewUtils.createBitmapFromViewWithoutDisplay(binding.root)
                    ) as Drawable ).apply {
                        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                    }
                }
            }
         }
    }

}

class TableAdapter(private val items: MutableList<Spanned>) : BaseAdapter() {
    override fun getCount(): Int = items.size
    override fun getItem(p0: Int): Any? = null
    override fun getItemId(p0: Int): Long = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val textView:TextView
        if (convertView == null) {
            textView = TextView(ContextWrapper(App.context)).apply {
                setPadding(8, 8, 8, 8)
            }
        } else {
            textView = convertView as TextView
        }
        textView.text = items[position]
        return textView
    }
}
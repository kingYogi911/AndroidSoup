package com.yogi.androidsoup.spans

import android.content.ContextWrapper
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.text.style.AlignmentSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.yogi.androidsoup.AndroidSoup.Companion.getSpanned
import com.yogi.androidsoup.App
import com.yogi.androidsoup.ViewUtils
import com.yogi.androidsoup.databinding.TableCellLayoutBinding
import com.yogi.androidsoup.databinding.TableLayoutBinding
import com.yogi.androidsoup.styles.Bold
import com.yogi.androidsoup.styles.TextStyle
import org.jsoup.nodes.Element


class TableSpan(tableElement: Element, inheritedStyles: List<TextStyle>) :
    ImageSpan(getDrawable(tableElement, inheritedStyles)) {
    companion object {
        fun getDrawable(tableElement: Element, inheritedStyles: List<TextStyle>): Drawable {
            Log.i("TableSpan", tableElement.toString())
            if (tableElement.tagName() != "table")
                return ContextCompat.getDrawable(App.context, android.R.drawable.ic_delete)!!
                    .apply {
                        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
                    }
            else {
                tableElement.getElementsByTag("tbody").let { tbody ->
                    var (rowCount, colCount) = arrayOf(0, 0)
                    val rows = tbody[0].getElementsByTag("tr").also {
                        rowCount = it.size
                        Log.e("RowCount", "c = $rowCount")
                        if (it.size > 0) {
                            colCount = it[0].children().size
                        }
                    }
                    val binding = TableLayoutBinding.inflate(LayoutInflater.from(ContextWrapper(App.context)))
                    val items = mutableListOf<Spanned>()
                    for (row in rows) {
                        for (col in row.getElementsByTag("th").apply {
                            addAll(row.getElementsByTag("td"))
                        }) {
                            items.add(
                                col.getSpanned(
                                    styles = mutableListOf<TextStyle>().apply {
                                        addAll(inheritedStyles)
                                    }
                                ).apply {
                                    if (col.tagName() == "th") {
                                        setSpan(
                                            Bold().getSpan(),
                                            0,
                                            length,
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                        )
                                        setSpan(
                                            AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                            0,
                                            length,
                                            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                        )
                                    }
                                }
                            )
                        }
                    }
                    //binding.gridView.adapter = TableAdapter(items)
                    (binding.rv.layoutManager as GridLayoutManager).apply {
                        spanCount = colCount
                    }.also {
                        val dividerItemDecorationHor = DividerItemDecoration(
                            binding.rv.context,
                            GridLayoutManager.HORIZONTAL
                        )
                        val dividerItemDecorationVer = DividerItemDecoration(
                            binding.rv.context,
                            GridLayoutManager.VERTICAL
                        )
                        binding.rv.addItemDecoration(dividerItemDecorationHor)
                        binding.rv.addItemDecoration(dividerItemDecorationVer)
                    }
                    binding.rv.adapter = TableRecyclerViewAdapter(items)
                    return (BitmapDrawable(
                        App.context.resources,
                        ViewUtils.createBitmapFromViewWithoutDisplay(binding.rv)
                    ) as Drawable).apply {
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
        val textView: TextView
        if (convertView == null) {
            textView = TableCellLayoutBinding.inflate(LayoutInflater.from(parent!!.context)).tvCell
        } else {
            textView = convertView as TextView
        }
        textView.text = items[position]
        return textView
    }
}

class TableRecyclerViewAdapter(private val items: MutableList<Spanned>) :
    RecyclerView.Adapter<TableRecyclerViewAdapter.TVHolder>() {
    class TVHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVHolder =
        TVHolder(TableCellLayoutBinding.inflate(LayoutInflater.from(parent.context)).tvCell)

    override fun onBindViewHolder(holder: TVHolder, position: Int) {
        holder.textView.text = items[position]
    }

    override fun getItemCount(): Int = items.size

}

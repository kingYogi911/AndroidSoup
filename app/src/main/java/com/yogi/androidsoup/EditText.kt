package com.yogi.androidsoup

import android.R
import android.content.ClipboardManager
import android.content.Context
import android.util.AttributeSet
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import org.jsoup.Jsoup


class EditText(context: Context, attrs: AttributeSet?, defStyleAttr: Int?) :
    androidx.appcompat.widget.AppCompatEditText(context, attrs, defStyleAttr!!) {
    constructor(context: Context,attrs: AttributeSet?):this(context,attrs,null)
    constructor(context: Context):this(context,null,null)
    override fun onTextContextMenuItem(id: Int): Boolean {
        var consumed = super.onTextContextMenuItem(id)
        when (id) {
            R.id.cut -> onTextCut()
            R.id.paste -> {
                onTextPaste()
            }
            R.id.copy -> onTextCopy()
        }
        return consumed
    }

    /**
     * Text was cut from this EditText.
     */
    private fun onTextCut() {
        Toast.makeText(context, "Cut!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Text was copied from this EditText.
     */
    private fun onTextCopy() {
        Toast.makeText(context, "Copy!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Text was pasted into the EditText.
     */
    private fun onTextPaste() {
        val clipboard: ClipboardManager? = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        clipboard?.primaryClip?.getItemAt(0)?.text?.let {
            editableText.append(it)
            val str=editableText.toString();
            editableText.clear()
        }
        Toast.makeText(context, "Paste!", Toast.LENGTH_SHORT).show()
    }
}
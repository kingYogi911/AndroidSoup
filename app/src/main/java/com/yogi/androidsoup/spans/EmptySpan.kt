package com.yogi.androidsoup.spans

import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.text.style.*
import androidx.core.content.ContextCompat
import com.yogi.androidsoup.App
import com.yogi.androidsoup.R

class EmptySpan(): DynamicDrawableSpan() {
    override fun getDrawable(): Drawable? = ContextCompat.getDrawable(App.context, R.drawable.ic_launcher_background)
}
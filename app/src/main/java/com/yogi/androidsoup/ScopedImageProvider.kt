package com.yogi.androidsoup

import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ScopedImageProvider(private val coroutineScope: CoroutineScope) :ImageProvider {
    override fun requestImage(src: String, result: (Drawable?) -> Unit) {
        coroutineScope.launch {
            delay(4000)
            ContextCompat.getDrawable(App.context,R.drawable.ic_launcher_background).let{
                result.invoke(it)
            }
        }
    }
}
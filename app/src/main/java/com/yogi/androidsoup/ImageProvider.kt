package com.yogi.androidsoup

import android.graphics.drawable.Drawable

interface ImageProvider {
    fun requestImage(src: String, result: (Drawable?) -> Unit)
}
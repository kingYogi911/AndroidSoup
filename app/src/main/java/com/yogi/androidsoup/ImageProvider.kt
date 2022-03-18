package com.yogi.androidsoup

import android.graphics.drawable.Drawable

interface ImageProvider {
    fun getImage(src:String): Drawable?
}
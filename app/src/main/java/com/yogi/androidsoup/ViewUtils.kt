package com.yogi.androidsoup

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.TypedValue
import android.view.View
import org.jetbrains.annotations.NotNull
import kotlin.math.roundToInt


class ViewUtils {
    companion object {
        fun createBitmapFromView(@NotNull view: View): Bitmap? {
            val bitmap = Bitmap.createBitmap(
                view.width,
                view.height,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        fun createBitmapFromViewWithoutDisplay(@NotNull view:View):Bitmap{
            val width = 800
            val height = App.context.resources.displayMetrics.heightPixels

            val measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST)
            val measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.AT_MOST)

            //Cause the view to re-layout
            view.measure(measuredWidth, measuredHeight)
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        fun createBitmapFromView(@NotNull view: View, width: Int, height: Int): Bitmap {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            view.draw(canvas)
            return bitmap
        }

        fun dpToPx(dp: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp, Resources.getSystem().displayMetrics
            ).roundToInt()
        }
    }
}
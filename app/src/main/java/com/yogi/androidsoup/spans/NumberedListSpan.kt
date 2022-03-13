package com.yogi.androidsoup.spans

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.text.Layout
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.Px
import com.yogi.androidsoup.App
import com.yogi.androidsoup.ViewUtils

class NumberedListSpan(
    val n: Int,
    @Px private val leadingMargin: Int = ViewUtils.dpToPx(50f)
) :
    LeadingMarginSpan,
    Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt(), parcel.readInt())

    override fun getLeadingMargin(p0: Boolean): Int = leadingMargin + (dotRadius * 2)

    override fun drawLeadingMargin(
        @NonNull canvas: Canvas, @NonNull paint: Paint, x: Int, dir: Int,
        top: Int, baseline: Int, bottomm: Int,
        @NonNull text: CharSequence, start: Int, end: Int,
        first: Boolean, @Nullable layout: Layout?
    ) {
        if ((text as Spanned).getSpanStart(this) == start) {
            val oldStyle=paint.style
            paint.style = Paint.Style.FILL
            val bounds = Rect()
            paint.getTextBounds("$n", 0, "$n".length, bounds)
            val xPos = leadingMargin - bounds.width() - (2 * dotRadius) - if("$n".endsWith("1")) 6 else 0
            canvas.drawText("$n", xPos.toFloat(), baseline.toFloat(), paint)
            canvas.drawCircle(
                leadingMargin.toFloat(),
                baseline.toFloat(),
                dotRadius.toFloat(),
                paint
            )
            paint.style=oldStyle
        }
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flag: Int) {
        dest.writeInt(n)
        dest.writeInt(leadingMargin)
    }

    companion object CREATOR : Parcelable.Creator<NumberedListSpan> {
        private val dotRadius = ViewUtils.dpToPx(1f);
        override fun createFromParcel(parcel: Parcel): NumberedListSpan {
            return NumberedListSpan(parcel)
        }

        override fun newArray(size: Int): Array<NumberedListSpan?> {
            return arrayOfNulls(size)
        }
    }


}
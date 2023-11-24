package com.gl4.tp3

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.icu.util.Calendar
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.RequiresApi


class CustomAnalogClock : View {
    /** height, width of the clock's view  */
    private var mHeight = 0
    /** height, width of the clock's view  */
    private  var mWidth:Int = 0

    /** numeric numbers to denote the hours  */
    private val mClockHours = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)

    /** spacing and padding of the clock-hands around the clock round  */
    private var mPadding = 0
    private val mNumeralSpacing = 0

    /** truncation of the heights of the clock-hands,
     * hour clock-hand will be smaller comparetively to others  */
    private var mHandTruncation = 0
    /** truncation of the heights of the clock-hands,
     * hour clock-hand will be smaller comparetively to others  */
    private  var mHourHandTruncation:Int = 0

    /** others attributes to calculate the locations of hour-points  */
    private var mRadius = 0
    private var mPaint: Paint? = null
    private val mRect: Rect = Rect()
    private var isInit // it will be true once the clock will be initialized.
            = false
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    @RequiresApi(Build.VERSION_CODES.N)
    protected override fun onDraw(canvas: Canvas?) {
        /** initialize necessary values */
        /** initialize necessary values  */
        if (!isInit) {
            mPaint = Paint()
            mHeight = height
            mWidth = width
            mPadding = mNumeralSpacing + 50 // spacing from the circle border
            val minAttr = Math.min(mHeight, mWidth)
            mRadius = minAttr / 2 - mPadding

            // for maintaining different heights among the clock-hands
            mHandTruncation = minAttr / 20
            mHourHandTruncation = minAttr / 17
            isInit = true // set true once initialized
        }

        /** draw the canvas-color */
        /** draw the canvas-color  */
        canvas!!.drawColor(Color.WHITE)
        /** circle border */
        mPaint?.reset();
        mPaint?.setColor(Color.DKGRAY);
        mPaint?.setStyle(Paint.Style.STROKE);
        mPaint?.setStrokeWidth(4F);
        mPaint?.setAntiAlias(true);
        mPaint?.let {
            canvas.drawCircle((mWidth / 2).toFloat(),
                (mHeight / 2).toFloat(), (mRadius + mPadding - 10).toFloat(), it
            )
        };

        /** clock-center */
        mPaint?.setStyle(Paint.Style.FILL);
        mPaint?.let { canvas.drawCircle((mWidth / 2).toFloat(), (mHeight / 2).toFloat(), 12F, it) };  // the 03 clock hands will be rotated from this center point.
        /** border of hours */

        /** border of hours  */
        val fontSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14f, resources.displayMetrics)
                .toInt()
        mPaint!!.textSize = fontSize.toFloat() // set font size (optional)


        for (hour in mClockHours) {
            val tmp = hour.toString()
            mPaint!!.getTextBounds(tmp, 0, tmp.length, mRect) // for circle-wise bounding

            // find the circle-wise (x, y) position as mathematical rule
            val angle = Math.PI / 6 * (hour - 3)
            val x = (mWidth / 2 + Math.cos(angle) * mRadius - mRect.width() / 2).toInt()
            val y = (mHeight / 2 + Math.sin(angle) * mRadius + mRect.height() / 2).toInt()
            canvas.drawText(
                hour.toString(),
                x.toFloat(),
                y.toFloat(),
                mPaint!!
            ) // you can draw dots to denote hours as alternative
        }
        /** draw clock hands to represent the every single time */

        /** draw clock hands to represent the every single time  */
        val calendar: Calendar = Calendar.getInstance()
        var hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        hour = if (hour > 12) hour - 12 else hour

        drawHandLine(
            canvas,
            ((hour + calendar.get(Calendar.MINUTE) / 60) * 5f).toInt(),
            true,
            false
        ) // draw hours

        drawHandLine(canvas, calendar.get(Calendar.MINUTE), false, false) // draw minutes

        drawHandLine(canvas, calendar.get(Calendar.SECOND), false, true) // draw seconds


        /** invalidate the appearance for next representation of time  */
        /** invalidate the appearance for next representation of time   */
        postInvalidateDelayed(500)
        invalidate()
    }

    private fun drawHandLine(canvas: Canvas, moment: Int, isHour: Boolean, isSecond: Boolean) {
        val angle = Math.PI * moment / 30 - Math.PI / 2
        val handRadius =
            if (isHour) mRadius - mHandTruncation - mHourHandTruncation else mRadius - mHandTruncation
        if (isSecond) mPaint!!.color = Color.rgb(98,0,238)
        canvas.drawLine(
            (mWidth / 2).toFloat(),
            (mHeight / 2).toFloat(),
            (mWidth / 2 + Math.cos(angle) * handRadius).toFloat(),
            (mHeight / 2 + Math.sin(angle) * handRadius).toFloat(),
            mPaint!!
        )
    }
}
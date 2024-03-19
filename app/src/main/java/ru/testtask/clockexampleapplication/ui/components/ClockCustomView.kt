package ru.testtask.clockexampleapplication.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import ru.testtask.clockexampleapplication.R
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val START_ANGLE = -Math.PI / 2

private const val REFRESH_PERIOD = 180L

private const val DEFAULT_WIDTH_IN_DP = 240

private const val DEFAULT_HEIGHT_IN_DP = 240

class ClockCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr){
    private var clockRadius = 0.0f
    private var centerX = 0.0f
    private var centerY = 0.0f
    private val position: PointF = PointF(0.0f, 0.0f)

    var ringColor = 0
    var hourHandColor = 0
    var minuteHandColor = 0
    var secondHandColor = 0
    var dotsColor = 0
    var textColor = 0
    var bgColor = 0

    private val hours = Array(12) { i -> i + 1 }

    init {
        context.withStyledAttributes(attrs, R.styleable.ClockView) {
            ringColor = getColor(
                R.styleable.ClockView_clock_ring_color,
                ContextCompat.getColor(context, R.color.black)
            )
            textColor = getColor(
                R.styleable.ClockView_clock_text_color,
                ContextCompat.getColor(context, R.color.black)
            )
            hourHandColor = getColor(
                R.styleable.ClockView_clock_hour_color,
                ContextCompat.getColor(context, R.color.black)
            )
            minuteHandColor = getColor(
                R.styleable.ClockView_clock_min_color,
                ContextCompat.getColor(context, R.color.black)
            )
            secondHandColor = getColor(
                R.styleable.ClockView_clock_second_color,
                ContextCompat.getColor(context, R.color.transparent)
            )
            dotsColor = getColor(
                R.styleable.ClockView_clock_dots_color,
                ContextCompat.getColor(context, R.color.transparent)
            )
            bgColor = getColor(
                R.styleable.ClockView_clock_bg_color,
                ContextCompat.getColor(context, R.color.transparent)
            )
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        textScaleX = 0.9f
        letterSpacing = -0.15f
        typeface = Typeface.DEFAULT
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        clockRadius = min(width, height) / 2f
        centerX = width / 2f
        centerY = height / 2f
    }

    private fun PointF.computeXYForPoints(pos: Int, radius: Float) {
        val angle = (pos * (Math.PI / 30)).toFloat()
        x = radius * cos(angle) + centerX
        y = radius * sin(angle) + centerY
    }

    private fun PointF.computeXYForHourLabels(hour: Int, radius: Float) {
        val angle = (START_ANGLE + hour * (Math.PI / 6)).toFloat()
        x = radius * cos(angle) + centerX
        // Distance from the baseline to the center needed to adjust text position
        val textBaselineToCenter = (paint.descent() + paint.ascent()) / 2
        y = radius * sin(angle) + centerY - textBaselineToCenter
    }

    private fun drawClockBase(canvas: Canvas) {
        paint.color = bgColor
        paint.style = Paint.Style.FILL
        canvas.drawCircle(centerX, centerY, clockRadius, paint)
    }

    private fun drawDots(canvas: Canvas) {
        paint.color = dotsColor
        paint.style = Paint.Style.FILL
        val dotsDrawLineRadius = clockRadius * 5 / 6
        for (i in 0 until 60) {
            position.computeXYForPoints(i, dotsDrawLineRadius)
            val dotRadius = if (i % 5 == 0) clockRadius / 96 else clockRadius / 128
            canvas.drawCircle(position.x, position.y, dotRadius, paint)
        }
    }

    private fun drawClockFrame(canvas: Canvas) {
        paint.color = ringColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = clockRadius / 12
        val boundaryRadius = clockRadius - paint.strokeWidth / 2
        canvas.drawCircle(centerX, centerY, boundaryRadius, paint)
        paint.strokeWidth = 0f
    }

    // Draw the hour labels on the clock
    private fun drawHourLabels(canvas: Canvas) {
        paint.textSize = clockRadius * 2 / 7
        paint.strokeWidth = 0f
        paint.color = textColor
        val labelsDrawLineRadius = clockRadius * 11 / 16
        for (i in 1..12) {
            position.computeXYForHourLabels(i, labelsDrawLineRadius)
            val label = i.toString()
            canvas.drawText(label, position.x, position.y, paint)
        }
    }

    // Draw clock hands
    private fun drawClockHands(canvas: Canvas) {
        val calendar: Calendar = Calendar.getInstance()
        var hour = calendar.get(Calendar.HOUR_OF_DAY)
        // Convert to 12-hour format from 24-hour format
        hour = if (hour > 12) hour - 12 else hour
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        paint.style = Paint.Style.STROKE
        drawHourHand(canvas, hour + minute / 60f)
        drawMinuteHand(canvas, minute)
        drawSecondHand(canvas, second)
    }

    // Draw the hour hand
    private fun drawHourHand(canvas: Canvas, hourWithMinutes: Float) {
        paint.color = hourHandColor
        paint.strokeWidth = clockRadius / 15
        val angle = (Math.PI * hourWithMinutes / 6 + START_ANGLE).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * clockRadius * 3 / 14,
            centerY - sin(angle) * clockRadius * 3 / 14,
            centerX + cos(angle) * clockRadius * 7 / 14,
            centerY + sin(angle) * clockRadius * 7 / 14,
            paint
        )
    }

    // Draw the minute hand
    private fun drawMinuteHand(canvas: Canvas, minute: Int) {
        paint.color = minuteHandColor
        paint.strokeWidth = clockRadius / 40
        val angle = (Math.PI * minute / 30 + START_ANGLE).toFloat()
        canvas.drawLine(
            centerX - cos(angle) * clockRadius * 2 / 7,
            centerY - sin(angle) * clockRadius * 2 / 7,
            centerX + cos(angle) * clockRadius * 5 / 7,
            centerY + sin(angle) * clockRadius * 5 / 7,
            paint
        )
    }

    // Draw the second hand
    private fun drawSecondHand(canvas: Canvas, second: Int) {
        paint.color = secondHandColor
        val angle = (Math.PI * second / 30 + START_ANGLE).toFloat()
        // Draw the thin part of the hand
        paint.strokeWidth = clockRadius / 80
        canvas.drawLine(
            centerX - cos(angle) * clockRadius * 1 / 14,
            centerY - sin(angle) * clockRadius * 1 / 14,
            centerX + cos(angle) * clockRadius * 5 / 7,
            centerY + sin(angle) * clockRadius * 5 / 7,
            paint
        )
        // Draw the broad part of the hand
        paint.strokeWidth = clockRadius / 50
        canvas.drawLine(
            centerX - cos(angle) * clockRadius * 2 / 7,
            centerY - sin(angle) * clockRadius * 2 / 7,
            centerX - cos(angle) * clockRadius * 1 / 14,
            centerY - sin(angle) * clockRadius * 1 / 14,
            paint
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawClockBase(canvas)
        drawClockFrame(canvas)
        drawDots(canvas)
        drawHourLabels(canvas)
        drawClockHands(canvas)
        postInvalidateDelayed(REFRESH_PERIOD)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val defaultWidth = (DEFAULT_WIDTH_IN_DP * resources.displayMetrics.density).toInt()
        val defaultHeight = (DEFAULT_HEIGHT_IN_DP * resources.displayMetrics.density).toInt()

        val widthToSet = resolveSize(defaultWidth, widthMeasureSpec)
        val heightToSet = resolveSize(defaultHeight, heightMeasureSpec)

        setMeasuredDimension(widthToSet, heightToSet)
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())

        bundle.putInt("ringColor", ringColor)
        bundle.putInt("textColor", textColor)
        bundle.putInt("dotsColor", dotsColor)
        bundle.putInt("hourHandColor", hourHandColor)
        bundle.putInt("minuteHandColor", minuteHandColor)
        bundle.putInt("secondHandColor", secondHandColor)
        bundle.putInt("backgroundColor", bgColor)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var superState: Parcelable? = null
        if (state is Bundle) {
            ringColor = state.getInt("ringColor")
            textColor = state.getInt("textColor")
            dotsColor = state.getInt("dotsColor")
            hourHandColor = state.getInt("hourHandColor")
            minuteHandColor = state.getInt("minuteHandColor")
            secondHandColor = state.getInt("secondHandColor")
            bgColor = state.getInt("backgroundColor")
            superState =
                if (Build.VERSION.SDK_INT >= 33) state.getParcelable("superState", Parcelable::class.java)
                else @Suppress("DEPRECATION") state.getParcelable("superState")
        }
        super.onRestoreInstanceState(superState)
    }
}
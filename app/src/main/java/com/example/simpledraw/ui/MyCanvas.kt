package com.example.simpledraw.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.compose.ui.graphics.Color

class MyCanvas(context: Context, tPoints: List<Point>, val AddPointToViewModel: (Point) -> Unit) : View(context) {

    private val DEBUG_TAG = "MyCanvas"

    private var drawable: ShapeDrawable? = null

    val points: MutableList<Point> = tPoints.toMutableList()


    private val paint = Paint()

    override fun onDraw(canvas: Canvas) {
        points.forEach {
            canvas.drawCircle(it.x.toFloat(), it.y.toFloat(), 5f, paint)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        paint.style = Paint.Style.FILL
        drawable = ShapeDrawable(RectShape()).apply {
            // If the color isn't set, the shape uses black as the default.
            paint.color = 0xffD0BCFF.toInt()
            // If the bounds aren't set, the shape can't be drawn.
            Log.d(DEBUG_TAG, "size $w $h")
            setBounds(0, 0, w, h)
        }
    }

    private fun addPoint(x: Float, y: Float) {
        val point = Point(x.toInt(), y.toInt())
        AddPointToViewModel(point)
        points.add(point)
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.getX(0)
        val y = event.getY(0)

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(DEBUG_TAG, "Action was DOWN")
                addPoint(x, y)
                true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(DEBUG_TAG, "Action was MOVE")
                addPoint(x, y)
                true
            }
            MotionEvent.ACTION_UP -> {
                Log.d(DEBUG_TAG, "Action was UP")
                addPoint(x, y)
                true
            }
            MotionEvent.ACTION_CANCEL -> {
                Log.d(DEBUG_TAG, "Action was CANCEL")
                true
            }
            MotionEvent.ACTION_OUTSIDE -> {
                Log.d(DEBUG_TAG, "Movement occurred outside bounds of current screen element")
                true
            }
            else -> super.onTouchEvent(event)
        }
    }
}
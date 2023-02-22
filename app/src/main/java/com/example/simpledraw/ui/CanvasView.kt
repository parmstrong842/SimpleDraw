package com.example.simpledraw.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color.argb
import android.graphics.Color.rgb
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View

@SuppressLint("ViewConstructor")
class CanvasView(context: Context, tPoints: List<Point>, val AddPointToViewModel: (Point) -> Unit) : View(context) {

    private val tag = "MyCanvas"

    private val paths: MutableList<PathWrapper> = mutableListOf()


    var strokeWidth = 10f
        set(value) {
            field = value
            paint = updatePaint()
        }
    var color = argb(255, 255, 0, 0)
        set(value) {
            field = value
            paint = updatePaint()
        }

    var bgColor = rgb(255, 255, 255)

    private var paint = Paint()

    private fun updatePaint() = Paint().apply {
        this.color = color
        this.strokeWidth = strokeWidth
        this.style = Paint.Style.STROKE
    }

    private fun updateLatestPath(newPoint: Point) {
        paths.last().path.lineTo(newPoint.x.toFloat(), newPoint.y.toFloat())
        invalidate()
    }

    private fun insertNewPath(newPoint: Point) {
        val pathWrapper = PathWrapper(
            path = startNewPath(newPoint),
            paint = Paint().apply {
                this.style = Paint.Style.STROKE
                this.color = color
                this.strokeWidth = strokeWidth
            }
        )
        paths.add(pathWrapper)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        paths.forEach {
            canvas.drawPath(it.path, it.paint)
        }
    }

    private fun startNewPath(newPoint: Point) = Path().apply {
        this.moveTo(newPoint.x, newPoint.y)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        paint.strokeWidth = strokeWidth
        paint.color = color
        paint.style = Paint.Style.STROKE
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.getX(0)
        val y = event.getY(0)

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(tag, "Action was DOWN")
                insertNewPath(Point(x, y))
                true
            }
            MotionEvent.ACTION_MOVE -> {
                Log.d(tag, "Action was MOVE")
                updateLatestPath(Point(x, y))
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    data class PathWrapper(
        val path: Path,
        val paint: Paint
    )

    data class Point(
        val x: Float,
        val y: Float
    )
}

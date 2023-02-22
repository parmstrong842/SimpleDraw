package com.example.simpledraw.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.Log
import android.view.MotionEvent
import android.view.View

@SuppressLint("ViewConstructor")
class CanvasView(context: Context, tPoints: List<Point>, val AddPointToViewModel: (Point) -> Unit) : View(context) {

    private val tag = "MyCanvas"

    private val paths: MutableList<PathWrapper> = mutableListOf()

    var currentStrokeWidth = 10f
    var currentColor = Color.RED
    var currentBgColor = Color.rgb(255, 255, 255)

    private fun updateLatestPath(newPoint: Point) {
        val path = paths.last().path
        if (path.isEmpty) {
            path.moveTo(newPoint.x, newPoint.y)
        } else {
            path.lineTo(newPoint.x, newPoint.y)
        }
        invalidate()
    }

    private fun startNewPath(newPoint: Point) {
        val pathWrapper = PathWrapper(
            path = Path(),
            startPoint = newPoint,
            paint = Paint().apply {
                this.style = Paint.Style.STROKE
                this.color = currentColor
                this.strokeWidth = currentStrokeWidth
            }
        )
        paths.add(pathWrapper)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        paths.forEach {
            if (!it.path.isEmpty) {
                canvas.drawPath(it.path, it.paint)
            } else {
                canvas.drawCircle(it.startPoint.x, it.startPoint.y, it.paint.strokeWidth, it.paint)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.getX(0)
        val y = event.getY(0)

        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(tag, "Action was DOWN")
                startNewPath(Point(x, y))
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
        val startPoint: Point,
        val paint: Paint
    )

    data class Point(
        val x: Float,
        val y: Float
    )
}

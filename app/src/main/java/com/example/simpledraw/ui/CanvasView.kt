package com.example.simpledraw.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathEffect
import android.util.Log
import android.view.MotionEvent
import android.view.View

@SuppressLint("ViewConstructor")
class CanvasView(
    context: Context,
    pathsState: MutableList<PathWrapper>,
    redoPathsState: MutableList<PathWrapper>,
    var currentColor: Int,
    val startNewPathViewModel: (PathWrapper) -> Unit,
    val updateLastPathViewModel: (Point) -> Unit
) : View(context) {

    private val tag = "MyCanvas"

    private val paths: MutableList<PathWrapper> = pathsState
    private val redoPaths: MutableList<PathWrapper> = redoPathsState

    var currentStrokeWidth = 10f
    var currentPathEffect: PathEffect? = null
    var currentBgColor = Color.WHITE
        set(value) {
            field = value
            setBackgroundColor(currentBgColor)
        }

    private fun updateLatestPath(newPoint: Point) {
        paths.last().pointCount++
        paths.last().path.lineTo(newPoint.x, newPoint.y)
        updateLastPathViewModel(newPoint)
        invalidate()
    }

    private fun startNewPath(newPoint: Point) {
        val pathWrapper = PathWrapper(
            path = Path().apply {
                this.moveTo(newPoint.x, newPoint.y)
            },
            startPoint = newPoint,
            pointCount = 1,
            paint = Paint().apply {
                this.color = currentColor
                this.strokeWidth = currentStrokeWidth
                this.style = Paint.Style.STROKE
                this.strokeCap = Paint.Cap.ROUND
                this.strokeJoin = Paint.Join.ROUND
                this.isAntiAlias = true
                this.pathEffect = currentPathEffect
            }
        )
        startNewPathViewModel(pathWrapper)
        paths.add(pathWrapper)
        redoPaths.clear()
        invalidate()
    }

    fun undo() {
        if(paths.isNotEmpty()) {
            redoPaths.add(paths.last())
            paths.remove(paths.last())
            invalidate()
        }
    }

    fun redo() {
        if (redoPaths.isNotEmpty()) {
            paths.add(redoPaths.last())
            redoPaths.remove(redoPaths.last())
            invalidate()
        }
    }

    fun reset() {
        paths.clear()
        redoPaths.clear()
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        paths.forEach {
            if (it.pointCount > 3) {
                canvas.drawPath(it.path, it.paint)
            } else {
                canvas.drawPoint(it.startPoint.x, it.startPoint.y, it.paint)
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
                //Log.d(tag, "Action was MOVE")
                updateLatestPath(Point(x, y))
                true
            }
            else -> super.onTouchEvent(event)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        setBackgroundColor(currentBgColor)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    data class PathWrapper(
        val path: Path,
        val startPoint: Point,
        var pointCount: Int,
        val paint: Paint
    )

    data class Point(
        val x: Float,
        val y: Float
    )
}

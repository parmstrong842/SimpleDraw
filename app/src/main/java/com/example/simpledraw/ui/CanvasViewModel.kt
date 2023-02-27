package com.example.simpledraw.ui

import android.graphics.Color
import android.graphics.PathEffect
import androidx.lifecycle.ViewModel
import com.example.simpledraw.ui.CanvasView.Point
import com.example.simpledraw.ui.CanvasView.PathWrapper

class CanvasViewModel: ViewModel() {

    val paths: MutableList<PathWrapper> = mutableListOf()
    val redoPaths: MutableList<PathWrapper> = mutableListOf()
    var currentColor = Color.RED
    var currentPathEffect: PathEffect? = null


    fun updateLastPath(newPoint: Point) {
        paths.last().pointCount++
        paths.last().path.lineTo(newPoint.x, newPoint.y)
    }

    fun startNewPath(path: PathWrapper) {
        paths.add(path)
        redoPaths.clear()
    }

    fun undo() {
        if (paths.isNotEmpty()) {
            redoPaths.add(paths.last())
            paths.remove(paths.last())
        }
    }

    fun redo() {
        if (redoPaths.isNotEmpty()) {
            paths.add(redoPaths.last())
            redoPaths.remove(redoPaths.last())
        }
    }

    fun reset() {
        paths.clear()
        redoPaths.clear()
    }
}
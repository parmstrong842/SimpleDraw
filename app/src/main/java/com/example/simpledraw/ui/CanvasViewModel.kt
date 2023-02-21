package com.example.simpledraw.ui

import android.graphics.Point
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.simpledraw.drawbox.PathWrapper
import kotlinx.coroutines.flow.*

data class CanvasUiState(
    val points: List<Point>
)

class CanvasViewModel: ViewModel() {

    private val _redoPathList = mutableStateListOf<PathWrapper>()
    private val _undoPathList = mutableStateListOf<PathWrapper>()
    internal val pathList: SnapshotStateList<PathWrapper> = _undoPathList

    private val _historyTracker = MutableSharedFlow<String>(extraBufferCapacity = 1)
    private val historyTracker = _historyTracker.asSharedFlow()

    private val _uiState: MutableStateFlow<CanvasUiState> = MutableStateFlow(CanvasUiState(listOf()))
    val uiState: StateFlow<CanvasUiState> = _uiState.asStateFlow()

    fun addPoint(point: Point) {
        val newPoints: MutableList<Point> = _uiState.value.points.toMutableList()
        newPoints.add(point)
        _uiState.update {
            it.copy(points = newPoints)
        }
    }
}
package com.example.simpledraw.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.*
import com.example.simpledraw.ui.CanvasView.Point

data class CanvasUiState(
    val points: List<Point>
)

class CanvasViewModel: ViewModel() {

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
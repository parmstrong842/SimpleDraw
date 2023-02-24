package com.example.simpledraw.ui

import androidx.lifecycle.ViewModel
import com.example.simpledraw.ui.CanvasView.Point
import com.example.simpledraw.ui.CanvasView.PathWrapper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CanvasUiState(
    val paths: MutableList<PathWrapper>
)

class CanvasViewModel: ViewModel() {

    var paths: MutableList<PathWrapper> = mutableListOf()

    private val _uiState: MutableStateFlow<CanvasUiState> = MutableStateFlow(CanvasUiState(mutableListOf()))
    val uiState = _uiState.asStateFlow()

    fun startNewPathViewModel(path: PathWrapper) {
        paths.add(path)
    }

    fun updateLastPathViewModel(newPoint: Point) {
        paths.last().pointCount++
        paths.last().path.lineTo(newPoint.x, newPoint.y)
    }

    fun undo() {
        if (uiState.value.paths.isNotEmpty()) {
            val newList = uiState.value.paths.toMutableList()
            newList.remove(newList.last())
            _uiState.update {
                it.copy(
                    paths = newList
                )
            }
        }
    }

    fun redo() {

    }

    fun reset() {
        paths = mutableListOf()
        _uiState.update {
            it.copy(
                paths = mutableListOf()
            )
        }
    }
}
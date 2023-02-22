package com.example.simpledraw.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun DrawBox(viewModel: CanvasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    AndroidView(modifier = Modifier.fillMaxSize(),
        factory = { context ->
            CanvasView(context, uiState.points) { viewModel.addPoint(it) }
        }
    )
}
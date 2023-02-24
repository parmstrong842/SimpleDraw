package com.example.simpledraw

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpledraw.ui.CanvasView
import com.example.simpledraw.ui.CanvasViewModel
import com.example.simpledraw.ui.theme.SimpleDrawTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleDrawTheme {
                val viewModel: CanvasViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()

                Scaffold(
                    topBar = {
                        Column() {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(onClick = { viewModel.undo() }) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                                IconButton(onClick = { viewModel.redo() }) {
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Forward")
                                }
                                IconButton(onClick = { viewModel.reset() }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Reset")
                                }
                            }
                        }
                    }
                ) {
                    var sliderPosition by remember { mutableStateOf(0.5f) }

                    Box {
                        AndroidView(modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                CanvasView(
                                    context = context,
                                    uiState = viewModel.paths,
                                    startNewPathViewModel = { viewModel.startNewPathViewModel(it) },
                                    updateLastPathViewModel = { viewModel.updateLastPathViewModel(it) }
                                )
                            },
                            update = {
                                it.currentStrokeWidth = sliderPosition * 70
                                it.paths = uiState.paths
                                it.invalidate()
                            }
                        )

                        Slider(
                            modifier = Modifier
                                .padding(it)
                                .semantics { contentDescription = "pen size" }
                                .fillMaxWidth(0.5f)
                                .align(Alignment.BottomCenter),
                            value = sliderPosition,
                            onValueChange = { sliderPosition = it }
                        )
                    }
                }
            }
        }
    }
}


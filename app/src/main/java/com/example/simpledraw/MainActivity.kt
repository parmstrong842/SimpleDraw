package com.example.simpledraw

import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpledraw.ui.CanvasView
import com.example.simpledraw.ui.CanvasViewModel
import com.example.simpledraw.ui.theme.SimpleDrawTheme
import yuku.ambilwarna.AmbilWarnaDialog

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleDrawTheme {
                val viewModel: CanvasViewModel = viewModel()
                val context = LocalContext.current
                val canvasView = remember {
                    CanvasView(
                        context = context,
                        pathsState = viewModel.paths,
                        redoPathsState = viewModel.redoPaths,
                        currentColor = viewModel.currentColor,
                        startNewPathViewModel = { viewModel.startNewPath(it) },
                        updateLastPathViewModel = { viewModel.updateLastPath(it) }
                    )
                }

                Scaffold(
                    topBar = {
                        Column {
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                IconButton(onClick = {
                                    viewModel.undo()
                                    canvasView.undo()
                                }) {
                                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                                }
                                IconButton(onClick = {
                                    viewModel.redo()
                                    canvasView.redo()
                                }) {
                                    Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Forward")
                                }
                                IconButton(onClick = {
                                    viewModel.reset()
                                    canvasView.reset()
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Reset")
                                }
                                var paletteColor by remember { mutableStateOf(canvasView.currentColor) }
                                IconButton(onClick = {
                                    val handler = object: AmbilWarnaDialog.OnAmbilWarnaListener {
                                        override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                                            viewModel.currentColor = color
                                            canvasView.currentColor = color
                                            paletteColor = color
                                        }
                                        override fun onCancel(dialog: AmbilWarnaDialog?) {}
                                    }
                                    AmbilWarnaDialog(
                                        context,
                                        canvasView.currentColor,
                                        handler
                                    ).show()
                                }) {
                                    Icon(
                                        painter = painterResource(R.drawable.palette_24px),
                                        contentDescription = "palette",
                                        modifier = Modifier.drawBehind {
                                            Log.d("MainActivity", "$size")
                                            drawCircle(Color(paletteColor), size.height)
                                        },
                                    )
                                }
                            }
                        }
                    }
                ) {
                    var sliderPosition by remember { mutableStateOf(0.5f) }

                    Box {
                        AndroidView(modifier = Modifier.fillMaxSize(),
                            factory = {
                                canvasView
                            },
                            update = {
                                it.currentStrokeWidth = sliderPosition * 70
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


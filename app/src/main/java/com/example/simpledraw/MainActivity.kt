package com.example.simpledraw

import android.graphics.Point
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpledraw.ui.CanvasViewModel
import com.example.simpledraw.ui.MyCanvas
import com.example.simpledraw.ui.theme.SimpleDrawTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleDrawTheme {
                val viewModel: CanvasViewModel = viewModel()
                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            title = { Text(text = "Draw") }
                        )
                    }
                ) {

                    val uiState by viewModel.uiState.collectAsState()

                    Column {
                        AndroidView(modifier = Modifier.fillMaxSize(),
                            factory = { context ->
                                MyCanvas(context, uiState.points) { viewModel.addPoint(it) }
                            }
                        )
                    }
                }
            }
        }
    }
}


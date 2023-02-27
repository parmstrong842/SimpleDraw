package com.example.simpledraw

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.DashPathEffect
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import androidx.core.view.drawToBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simpledraw.ui.CanvasView
import com.example.simpledraw.ui.CanvasViewModel
import com.example.simpledraw.ui.theme.SimpleDrawTheme
import yuku.ambilwarna.AmbilWarnaDialog
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

//TODO add path effects
//TODO make paths smoother
//TODO zoom
//TODO change background color
//TODO multi-touch
//TODO dialog when you delete
class MainActivity : ComponentActivity() {

    private val tag = "MainActivity"

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

                                //TODO grey circle is bigger than palette circle
                                var toggle by remember { mutableStateOf(false) }
                                IconButton(
                                    onClick = {
                                        if (!toggle) {
                                            val effect = DashPathEffect(floatArrayOf(100f, 100f), 0f)
                                            viewModel.currentPathEffect = effect
                                            canvasView.currentPathEffect = effect
                                        } else {
                                            viewModel.currentPathEffect = null
                                            canvasView.currentPathEffect = null
                                        }
                                        toggle = !toggle
                                    },
                                    modifier = if (toggle) Modifier.drawBehind {
                                        Log.d(tag, "$size")
                                        drawCircle(Color.Gray, size.height/2)
                                    } else Modifier
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.outline_draw_24),
                                        contentDescription = "path effects"
                                    )
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
                                            Log.d(tag, "$size")
                                            drawCircle(Color(paletteColor), size.height)
                                        },
                                    )
                                }

                                IconButton(onClick = { save(canvasView.drawToBitmap()) }) {
                                    Icon(painter = painterResource(R.drawable.ic_baseline_save_24), contentDescription = null)
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                IconButton(onClick = {
                                    viewModel.reset()
                                    canvasView.reset()
                                }) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Reset")
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

    private fun save(bitmap: Bitmap) {
        var uri: Uri? = null
        try {

            val fileName = DateTimeFormatter.ofPattern("u.MM.dd_hh.mma").format(LocalDateTime.now()) + ".png"
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }

            uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                contentResolver.openOutputStream(it).use { output ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                }
                values.apply {
                    clear()
                    put(MediaStore.Audio.Media.IS_PENDING, 0)
                }
                contentResolver.update(uri, values, null, null)
            }
        } catch (e: java.lang.Exception) {
            if (uri != null) {
                // Don't leave an orphan entry in the MediaStore
                contentResolver.delete(uri, null, null)
            }
            throw e
        }

        startActivity(
            Intent.createChooser(
                Intent().apply {
                    setDataAndType(uri, "image/*")
                    action = Intent.ACTION_VIEW
                },
                "Select Gallery App")
        )
    }
}


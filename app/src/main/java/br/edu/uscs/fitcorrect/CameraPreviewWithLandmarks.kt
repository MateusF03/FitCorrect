package br.edu.uscs.fitcorrect

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import br.edu.uscs.fitcorrect.PoseLandmarkerHelper.LandmarkerListener
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker

@Composable
fun CameraPreviewWithLandmarks() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // State to hold the most recent detection result.
    var currentResultBundle by remember { mutableStateOf<PoseLandmarkerHelper.ResultBundle?>(null) }

    var isFrontCamera by remember { mutableStateOf(false) }

    // Create the helper with a listener that updates our Compose state.
    val poseLandmarkerHelper = remember {
        PoseLandmarkerHelper(
            runningMode = RunningMode.LIVE_STREAM,
            context = context,
            poseLandmarkerHelperListener = object : LandmarkerListener {
                override fun onError(error: String, errorCode: Int) {
                    Log.e("PoseLandmarker", error)
                }

                override fun onResults(resultBundle: PoseLandmarkerHelper.ResultBundle) {
                    // Directly update the state with the new result.
                    currentResultBundle = resultBundle
                }
            }
        )
    }

    // Create a PreviewView to show the CameraX preview.
    val previewView = remember { PreviewView(context) }

    // Set up the camera when the composable is first launched.
    LaunchedEffect(isFrontCamera) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val cameraProvider = cameraProviderFuture.get()

        // Build the preview use-case.
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }

        // Build the analysis use-case.
        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build().also { analysis ->
                analysis.setAnalyzer(ContextCompat.getMainExecutor(context)) { imageProxy ->
                    // Pass the frame to the pose detector. Make sure is ARGB-888
                    poseLandmarkerHelper.detectLiveStream(imageProxy, isFrontCamera = isFrontCamera)
                }
            }

        // Select back camera.
        val cameraSelector = if (isFrontCamera) {
            CameraSelector.DEFAULT_FRONT_CAMERA
        } else {
            CameraSelector.DEFAULT_BACK_CAMERA
        }

        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (exc: Exception) {
            Log.e("CameraPreview", "Camera binding failed", exc)
        }
    }

    // Compose UI: a Box that stacks the camera preview and a Canvas overlay.
    Box(modifier = Modifier.fillMaxSize()) {
        // Display the camera preview.
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )

        // Draw landmarks over the preview.
        Canvas(modifier = Modifier.fillMaxSize()) {
            currentResultBundle?.results?.forEach { result ->
                // Adjust this block to access your landmarks.
                // For example, if your PoseLandmarkerResult has a method like poseLandmarks()
                // that returns a list of landmarks, do something like:
                for(landmark in result.landmarks()) {
                    for(normalizedLandmark in landmark) {
                        drawPoints(
                            points = listOf(
                                Offset(
                                    normalizedLandmark.x() * size.width,
                                    normalizedLandmark.y() * size.height
                                )
                            ),
                            pointMode = PointMode.Points,
                            color = Color.Blue,
                            strokeWidth = 8f
                        )
                    }

                    PoseLandmarker.POSE_LANDMARKS.forEach {
                        drawLine(
                            start = Offset(
                                landmark[it.start()].x() * size.width,
                                landmark[it.start()].y() * size.height
                            ),
                            end = Offset(
                                landmark[it.end()].x() * size.width,
                                landmark[it.end()].y() * size.height
                            ),
                            color = Color.Red,
                            strokeWidth = 4f
                        )
                    }
                }
            }

        }
        Button(
            onClick = {
                isFrontCamera = !isFrontCamera
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text(text = "Switch Camera")
        }
    }
}

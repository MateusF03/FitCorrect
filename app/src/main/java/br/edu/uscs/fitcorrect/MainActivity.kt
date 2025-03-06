package br.edu.uscs.fitcorrect

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import br.edu.uscs.fitcorrect.ui.theme.FitCorrectTheme

class MainActivity : ComponentActivity() {

    // Request camera permission
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
            // Handle permission result if needed.
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Request permission if not already granted.
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            FitCorrectTheme {
                ExerciseAssistantScreen()
            }
        }
    }
}

@Composable
fun ExerciseAssistantScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        CameraPreviewWithLandmarks(modifier = Modifier.fillMaxSize())
    }
}
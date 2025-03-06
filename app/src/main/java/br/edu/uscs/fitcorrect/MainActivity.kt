package br.edu.uscs.fitcorrect

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        CameraPreviewWithLandmarks(modifier = Modifier.fillMaxSize().padding(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            //ExerciseHeader()
        }
    }
}

@Composable
fun ExerciseHeader() {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("PLACEHOLDER", fontSize = 18.sp)
                Text("PLACEHOLDER", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}
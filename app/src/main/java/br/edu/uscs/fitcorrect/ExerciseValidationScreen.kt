package br.edu.uscs.fitcorrect

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import br.edu.uscs.fitcorrect.exercise.Exercise
import br.edu.uscs.fitcorrect.exercise.ExerciseRepository
import br.edu.uscs.fitcorrect.exercise.ExerciseSessionRepository
import br.edu.uscs.fitcorrect.exercise.SessionResultHolder
import br.edu.uscs.fitcorrect.utils.AngleUtils
import androidx.core.net.toUri
import kotlinx.coroutines.delay

@Composable
fun ExerciseValidationScreen(navController: NavHostController) {
    // Hold the selected exercise. Default to the first one.
    var currentExercise by remember { mutableStateOf(ExerciseRepository.getAllExercises().first()) }
    // Hold the latest pose detection results.
    var currentResultBundle by remember { mutableStateOf<PoseLandmarkerHelper.ResultBundle?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Your existing camera preview with landmarks.
        // NOTE: You may need to modify CameraPreviewWithLandmarks to pass the results via a callback.
        CameraPreviewWithLandmarks(
            modifier = Modifier.fillMaxSize(),
            onPoseResult = { currentResultBundle = it },
            currentExercise = currentExercise
        )
        // Overlay a column that shows the exercise selector and the feedback.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
                .align(Alignment.TopCenter)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExerciseSelector(
                    exercises = ExerciseRepository.getAllExercises(),
                    currentExercise = currentExercise,
                    onExerciseSelected = { currentExercise = it }
                )
                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { navController.navigate("session") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green.copy(alpha = 0.4f))
                ) {
                    Text("Iniciar Sessão", color = Color.White)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { navController.navigate("profile") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.4f))
                ) {
                    Text("Profile", color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            ExerciseFeedback(
                exercise = currentExercise,
                resultBundle = currentResultBundle
            )
        }
    }
}

/**
 * A dropdown to select an exercise from the repository.
 */
@Composable
fun ExerciseSelector(
    exercises: List<Exercise>,
    currentExercise: Exercise,
    onExerciseSelected: (Exercise) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Button(
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White.copy(alpha = 0.2f)
            )
        ) {
            Text(
                text = currentExercise.name,
                color = Color.White
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color.White.copy(alpha = 0.9f))
        ) {
            exercises.forEach { exercise ->
                DropdownMenuItem(
                    text = { Text(text = exercise.name) },
                    onClick = {
                        onExerciseSelected(exercise)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Displays feedback for each validation in the selected exercise.
 * For each validation, it calculates the angle from the detected landmarks,
 * compares it with the expected range, and shows a green check or red cross.
 */
@Composable
fun ExerciseFeedback(
    exercise: Exercise,
    resultBundle: PoseLandmarkerHelper.ResultBundle?
) {
    val landmarks = resultBundle?.results?.firstOrNull()?.landmarks()?.firstOrNull()
    var validationsPassed = 0

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = exercise.description,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (landmarks == null) {
            Text(
                text = "Detectando pose...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        } else {
            val totalValidations = exercise.validations.size

            exercise.validations.forEach { validation ->
                val p1 = landmarks[validation.anglePoints.first.index]
                val p2 = landmarks[validation.anglePoints.second.index]
                val p3 = landmarks[validation.anglePoints.third.index]
                val angle = AngleUtils.calculate3DAngle(p1, p2, p3)
                val isValid = angle in (validation.minAngle - validation.errorMargin)..(validation.maxAngle + validation.errorMargin)

                if (isValid) validationsPassed++

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isValid) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (isValid) "Bom" else "Ajustar",
                        tint = if (isValid) Color.Green else Color.Red,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ângulo: ${angle.toInt()}° (ideal: ${validation.minAngle.toInt()}° - ${validation.maxAngle.toInt()}°)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White
                    )
                }
            }

            val accuracyPercentage = (validationsPassed.toFloat() / exercise.validations.size) * 100

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Precisão do movimento: ${accuracyPercentage.toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                color = if (accuracyPercentage >= 80) Color.Green else Color.Yellow
            )
        }
    }
}

fun calculateAccuracy(exercise: Exercise, resultBundle: PoseLandmarkerHelper.ResultBundle?): Float {
    val landmarks = resultBundle?.results?.firstOrNull()?.landmarks()?.firstOrNull() ?: return 0f

    val passed = exercise.validations.count { validation ->
        val p1 = landmarks[validation.anglePoints.first.index]
        val p2 = landmarks[validation.anglePoints.second.index]
        val p3 = landmarks[validation.anglePoints.third.index]
        val angle = AngleUtils.calculate3DAngle(p1, p2, p3)
        angle in (validation.minAngle - validation.errorMargin)..(validation.maxAngle + validation.errorMargin)
    }

    return (passed.toFloat() / exercise.validations.size) * 100
}


@Composable
fun ExerciseSessionScreen(
    navController: NavHostController
) {
    val sessionSteps = ExerciseSessionRepository.sessionSteps
    var currentStepIndex by rememberSaveable { mutableStateOf(0) }
    var timeLeft by rememberSaveable { mutableStateOf(0) }
    var resultBundle by remember { mutableStateOf<PoseLandmarkerHelper.ResultBundle?>(null) }

    val currentStep = sessionSteps[currentStepIndex]
    val exercise = ExerciseRepository.getExerciseById(currentStep.exerciseId)
    var showVideo by remember { mutableStateOf(true) }

    // Start timer only after video is dismissed
    LaunchedEffect(currentStepIndex, showVideo) {
        if (!showVideo) {
            timeLeft = currentStep.durationSeconds
            while (timeLeft > 0) {
                delay(1000L)
                timeLeft--
            }
            // Move to next step or results
            if (currentStepIndex < sessionSteps.lastIndex) {
                currentStepIndex++
                showVideo = true // reset for next
            } else {
                navController.navigate("results")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (showVideo && !currentStep.isRest && exercise != null) {
            // Video introduction (maior)
            ExerciseVideoPlayer(
                videoUri = "android.resource://${LocalContext.current.packageName}/${exercise.videoResId}".toUri()
            )

            Button(
                onClick = { showVideo = false },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            ) {
                Text("Começar Exercício")
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                if (!currentStep.isRest && exercise != null) {
                    CameraPreviewWithLandmarks(
                        modifier = Modifier.fillMaxSize(),
                        onPoseResult = { resultBundle = it },
                        currentExercise = exercise
                    )
                    resultBundle?.let {
                        val accuracy = calculateAccuracy(exercise, it)
                        SessionResultHolder.addAccuracy(exercise.name, accuracy)
                    }
                }

                // Timer overlay no topo para melhor visibilidade
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .background(Color.Black.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (currentStep.isRest) "Pausa" else exercise?.name ?: "Exercício",
                        fontSize = 24.sp,
                        color = if (currentStep.isRest) Color.Yellow else Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$timeLeft s",
                        fontSize = 48.sp,
                        color = Color.Cyan
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (!currentStep.isRest && exercise != null) {
                        ExerciseFeedback(
                            exercise = exercise,
                            resultBundle = resultBundle
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SessionResultScreen(navController: NavHostController) {
    val results = SessionResultHolder.results
    val overallAverage = results.flatMap { it.accuracyList }.average().toFloat()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Resultados da Sessão", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(16.dp))

        Text("Média geral: ${overallAverage.toInt()}%", color = Color.Green)
        Spacer(modifier = Modifier.height(24.dp))

        results.forEach { result ->
            Text(
                "${result.exerciseName}: ${result.averageAccuracy.toInt()}%",
                style = MaterialTheme.typography.bodyLarge,
                color = if (result.averageAccuracy >= 80) Color.Green else Color.Yellow
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            SessionResultHolder.clear()
            navController.navigate("profile")
        }) {
            Text("Voltar ao Perfil")
        }
    }
}


@Composable
fun ExerciseVideoPlayer(videoUri: Uri) {
    val context = LocalContext.current
    val player = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        factory = {
            PlayerView(context).apply {
                this.player = player
                useController = true
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp) // Aumentado para maior visibilidade
    )

    DisposableEffect(Unit) {
        onDispose {
            player.release()
        }
    }
}

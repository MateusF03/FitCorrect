package br.edu.uscs.fitcorrect

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import br.edu.uscs.fitcorrect.exercise.Exercise
import br.edu.uscs.fitcorrect.exercise.ExerciseRepository
import br.edu.uscs.fitcorrect.exercise.ExerciseSessionRepository
import br.edu.uscs.fitcorrect.utils.AngleUtils

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

@Composable
fun ExerciseSessionScreen(
    navController: NavHostController
) {
    val sessionSteps = ExerciseSessionRepository.sessionSteps
    var currentStepIndex by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(sessionSteps[currentStepIndex].durationSeconds) }
    var resultBundle by remember { mutableStateOf<PoseLandmarkerHelper.ResultBundle?>(null) }

    val currentStep = sessionSteps[currentStepIndex]
    val exercise = ExerciseRepository.getExerciseById(currentStep.exerciseId)

    // Temporizador
    LaunchedEffect(currentStepIndex) {
        timeLeft = currentStep.durationSeconds
        while (timeLeft > 0) {
            kotlinx.coroutines.delay(1000L)
            timeLeft--
        }
        if (currentStepIndex < sessionSteps.lastIndex) {
            currentStepIndex++
        } else {
            navController.navigate("profile")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!currentStep.isRest && exercise != null) {
            CameraPreviewWithLandmarks(
                modifier = Modifier.fillMaxSize(),
                onPoseResult = { resultBundle = it },
                currentExercise = exercise
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                .padding(12.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (currentStep.isRest) "Pausa" else exercise?.name ?: "Exercício",
                style = MaterialTheme.typography.headlineMedium,
                color = if (currentStep.isRest) Color.Yellow else Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tempo restante: $timeLeft s",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (!currentStep.isRest && exercise != null) {
                ExerciseFeedback(
                    exercise = exercise,
                    resultBundle = resultBundle
                )
            }
        }
    }
}
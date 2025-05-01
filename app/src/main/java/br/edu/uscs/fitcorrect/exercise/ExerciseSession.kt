package br.edu.uscs.fitcorrect.exercise

data class ExerciseSessionStep(
    val exerciseId: Int,
    val durationSeconds: Int,
    val isRest: Boolean = false
)

object ExerciseSessionRepository {
    val sessionSteps = listOf(
        ExerciseSessionStep(exerciseId = 1, durationSeconds = 30), // Squats 30s
        ExerciseSessionStep(exerciseId = 1, durationSeconds = 15, isRest = true), // Pausa 15s
        ExerciseSessionStep(exerciseId = 1, durationSeconds = 30), // Squats 30s
        ExerciseSessionStep(exerciseId = 1, durationSeconds = 15, isRest = true)  // Pausa 15s
    )
}
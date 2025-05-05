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


data class SessionResult(
    val exerciseName: String,
    val accuracyList: MutableList<Float>
) {
    val averageAccuracy: Float
        get() = if (accuracyList.isNotEmpty()) accuracyList.average().toFloat() else 0f
}

object SessionResultHolder {
    private val _results = mutableMapOf<String, SessionResult>()
    val results: List<SessionResult> get() = _results.values.toList()

    fun addAccuracy(exerciseName: String, accuracy: Float) {
        val result = _results.getOrPut(exerciseName) {
            SessionResult(exerciseName, mutableListOf())
        }
        result.accuracyList.add(accuracy)
    }

    fun clear() {
        _results.clear()
    }
}
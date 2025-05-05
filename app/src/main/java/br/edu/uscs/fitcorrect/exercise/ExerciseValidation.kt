package br.edu.uscs.fitcorrect.exercise

data class ExerciseValidation(
    val anglePoints: Triple<BodyLandmark, BodyLandmark, BodyLandmark>,
    val minAngle: Float,
    val maxAngle: Float,
    val errorMargin: Float = 5f
)

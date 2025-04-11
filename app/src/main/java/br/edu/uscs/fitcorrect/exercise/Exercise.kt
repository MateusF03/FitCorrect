package br.edu.uscs.fitcorrect.exercise

data class Exercise(
    val id: Int,
    val name: String,
    val description: String,
    val validations: List<ExerciseValidation>
)

object ExerciseRepository {
    private val exercises = listOf(
        Exercise(
            id = 1,
            name = "Squat",
            description = "Proper squat form validation",
            validations = listOf(
                ExerciseValidation(
                    anglePoints = Triple(
                        BodyLandmark.LEFT_HIP,
                        BodyLandmark.LEFT_KNEE,
                        BodyLandmark.LEFT_ANKLE
                    ),
                    minAngle = 80f,
                    maxAngle = 100f
                ),
                ExerciseValidation(
                    anglePoints = Triple(
                        BodyLandmark.RIGHT_HIP,
                        BodyLandmark.RIGHT_KNEE,
                        BodyLandmark.RIGHT_ANKLE
                    ),
                    minAngle = 80f,
                    maxAngle = 100f
                )
            )
        ),
        Exercise(
            id = 2,
            name = "Bicep Curl",
            description = "Arm curl validation",
            validations = listOf(
                ExerciseValidation(
                    anglePoints = Triple(
                        BodyLandmark.LEFT_SHOULDER,
                        BodyLandmark.LEFT_ELBOW,
                        BodyLandmark.LEFT_WRIST
                    ),
                    minAngle = 145f,
                    maxAngle = 160f
                )
            )
        )
    )

    fun getAllExercises(): List<Exercise> = exercises
    fun getExerciseById(id: Int): Exercise? = exercises.find { it.id == id }
}
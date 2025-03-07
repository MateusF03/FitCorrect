package br.edu.uscs.fitcorrect

enum class AngleType(val angleConnections: List<Pair<Int, Int>>, val stringKey: Int) {
    LEFT_KNEE(listOf(23 to 25, 25 to 27), R.string.left_knee),
    LEFT_ARM(listOf(11 to 13, 13 to 15), R.string.left_arm);
}
package br.edu.uscs.fitcorrect

import br.edu.uscs.fitcorrect.utils.LandmarkUtils

enum class AngleType(val angleConnections: List<Pair<Int, Int>>, val stringKey: Int) {
    LEFT_KNEE(listOf(LandmarkUtils.LEFT_HIP to LandmarkUtils.LEFT_KNEE, LandmarkUtils.LEFT_KNEE to LandmarkUtils.LEFT_ANKLE), R.string.left_knee),
    LEFT_ARM(listOf(LandmarkUtils.LEFT_SHOULDER to LandmarkUtils.LEFT_ELBOW, LandmarkUtils.LEFT_ELBOW to LandmarkUtils.LEFT_WRIST), R.string.left_arm),
    RIGHT_KNEE(listOf(LandmarkUtils.RIGHT_HIP to LandmarkUtils.RIGHT_KNEE, LandmarkUtils.RIGHT_KNEE to LandmarkUtils.RIGHT_ANKLE), R.string.right_knee),
    RIGHT_ARM(listOf(LandmarkUtils.RIGHT_SHOULDER to LandmarkUtils.RIGHT_ELBOW, LandmarkUtils.RIGHT_ELBOW to LandmarkUtils.RIGHT_WRIST), R.string.right_arm);

    fun getTriplePoints(): Triple<Int, Int, Int> {
        return Triple(angleConnections[0].first, angleConnections[0].second, angleConnections[1].second)
    }
}
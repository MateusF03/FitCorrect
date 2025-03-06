package br.edu.uscs.fitcorrect.utils

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.acos
import kotlin.math.sqrt

class AngleUtils {
    companion object {
        fun calculate3DAngle(
            a: NormalizedLandmark,  // First joint (e.g., hip)
            b: NormalizedLandmark,  // Center joint (e.g., knee)
            c: NormalizedLandmark   // End joint (e.g., ankle)
        ): Float {
            val abX = a.x() - b.x()
            val abY = a.y() - b.y()
            val abZ = a.z() - b.z()

            val bcX = c.x() - b.x()
            val bcY = c.y() - b.y()
            val bcZ = c.z() - b.z()

            val dotProduct = (abX * bcX) + (abY * bcY) + (abZ * bcZ)
            val magnitudeAB = sqrt(abX * abX + abY * abY + abZ * abZ)
            val magnitudeBC = sqrt(bcX * bcX + bcY * bcY + bcZ * bcZ)

            if (magnitudeAB == 0f || magnitudeBC == 0f) {
                return 0f
            }

            val angleRad = acos(dotProduct / (magnitudeAB * magnitudeBC))
            return Math.toDegrees(angleRad.toDouble()).toFloat()
        }
    }
}
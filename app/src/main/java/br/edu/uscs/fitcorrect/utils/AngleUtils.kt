package br.edu.uscs.fitcorrect.utils

import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import kotlin.math.abs
import kotlin.math.atan2

class AngleUtils {
    companion object {
        fun calculate3DAngle(
            a: NormalizedLandmark,  // First joint (e.g., hip)
            b: NormalizedLandmark,  // Center joint (e.g., knee)
            c: NormalizedLandmark   // End joint (e.g., ankle)
        ): Float {
            var angleBA = atan2(b.y() - a.y(), b.x() - a.x())
            var angleBC = atan2(c.y() - b.y(), c.x() - b.x())

            if (angleBA < 0) {
                angleBA += 2 * Math.PI.toFloat()
            }
            if (angleBC < 0) {
                angleBC += 2 * Math.PI.toFloat()
            }

            val angle = abs(angleBA - angleBC)
            return Math.toDegrees(angle.toDouble()).toFloat()
        }
    }
}
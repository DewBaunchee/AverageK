package by.varyvoda.matvey.averagek.util

import javafx.geometry.Point2D
import java.time.Instant
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

fun getDistance(from: Point2D, to: Point2D): Double {
    return sqrt((to.x - from.x) * (to.x - from.x) + (to.y - from.y) * (to.y - from.y))
}

fun getExtremes(points: List<Point2D>): Extremes {
    var minX: Double = Double.POSITIVE_INFINITY
    var maxX: Double = Double.NEGATIVE_INFINITY
    var minY: Double = Double.POSITIVE_INFINITY
    var maxY: Double = Double.NEGATIVE_INFINITY
    points.forEach { point ->
        run {
            minX = min(point.x, minX)
            maxX = max(point.x, maxX)
            minY = min(point.y, minY)
            maxY = max(point.y, maxY)
        }
    }
    return Extremes(minX, maxX, minY, maxY)
}

fun getRandom(): Random {
    return Random(Instant.now().toEpochMilli())
}

fun Random.nextUnsignedInt(): Int {
    return abs(nextInt())
}
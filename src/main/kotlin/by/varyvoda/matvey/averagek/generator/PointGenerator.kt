package by.varyvoda.matvey.averagek.generator

import by.varyvoda.matvey.averagek.util.getRandom
import by.varyvoda.matvey.averagek.util.nextUnsignedInt
import by.varyvoda.matvey.averagek.view.Generator
import javafx.geometry.Point2D
import kotlin.random.Random

class PointGenerator : Generator<Point2D> {

    private var maxX: Int = 0
    private var maxY: Int = 0
    private val randomizer: Random = getRandom()

    fun setLimits(maxX: Int, maxY: Int) {
        this.maxX = maxX
        this.maxY = maxY
    }

    override fun next(): Point2D {
        return Point2D((randomizer.nextUnsignedInt() % maxX).toDouble(), (randomizer.nextUnsignedInt() % maxY).toDouble())
    }

    fun generate(count: Int): Array<Point2D> {
        return Array(count) { next() }
    }
}
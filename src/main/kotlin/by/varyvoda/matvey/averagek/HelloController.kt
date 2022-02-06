package by.varyvoda.matvey.averagek

import by.varyvoda.matvey.averagek.domain.KAverage
import by.varyvoda.matvey.averagek.domain.KClass
import by.varyvoda.matvey.averagek.domain.maximin.Maximin
import by.varyvoda.matvey.averagek.generator.ColorGenerator
import by.varyvoda.matvey.averagek.generator.PointGenerator
import by.varyvoda.matvey.averagek.util.getAverageDistance
import by.varyvoda.matvey.averagek.util.getDistance
import by.varyvoda.matvey.averagek.view.DrawableKClass
import by.varyvoda.matvey.averagek.view.KAverageDrawer
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.control.TextField

class HelloController {

    @FXML
    private lateinit var canvas: Canvas

    @FXML
    private lateinit var ratio: TextField

    @FXML
    private lateinit var stepMs: TextField

    private lateinit var kAverageDrawer: KAverageDrawer

    private val maximin: Maximin<Point2D, Double> =
        Maximin(
            KAverage(
                { kClass, point -> getDistance(kClass, point) },
                { points ->
                    run {
                        var sumX = 0.0
                        var sumY = 0.0
                        points.forEach { point ->
                            run {
                                sumX += point.x
                                sumY += point.y
                            }
                        }
                        val center = Point2D(sumX / points.size, sumY / points.size)
                        return@run points.stream()
                            .min(Comparator.comparing { point -> getDistance(point, center) })
                            .orElse(null)
                    }
                }
            )
        ) { candidate, parentCore, cores ->
            getAverageDistance(cores) / getNumber(ratio, 2) < getDistance(candidate, parentCore)
        }

    private val pointGenerator: PointGenerator = PointGenerator()

    private val colorGenerator: ColorGenerator = ColorGenerator()

    @FXML
    fun initialize() {
        kAverageDrawer = KAverageDrawer(canvas.graphicsContext2D)
        pointGenerator.setLimits(canvas.width.toInt(), canvas.height.toInt())
    }

    @FXML
    private fun onHelloButtonClick() {
        maximin.stop()
        val points: List<Point2D> = pointGenerator.generate(canvas.width.toInt() * canvas.height.toInt()).toList()
        maximin.calculate(points) { result ->
            Platform.runLater { drawResult(result) }
            try {
                Thread.sleep(getNumber(stepMs).toLong())
            } catch (e: InterruptedException) {
                // ignored
            }
        }
    }

    private fun getNumber(textField: TextField, or: Int = 0): Int {
        return try {
            Integer.parseInt(textField.text)
        } catch (e: NumberFormatException) {
            or
        }
    }

    private fun drawResult(result: List<KClass<Point2D>>) {
        canvas.graphicsContext2D.clearRect(0.0, 0.0, canvas.width, canvas.height)
        var colorIndex = 0
        kAverageDrawer.draw(result.map { kClass ->
            DrawableKClass(
                kClass.core,
                colorGenerator.get(colorIndex++),
                kClass.items
            )
        })
    }
}
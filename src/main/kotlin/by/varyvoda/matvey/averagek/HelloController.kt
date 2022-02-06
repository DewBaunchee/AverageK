package by.varyvoda.matvey.averagek

import by.varyvoda.matvey.averagek.domain.KAverage
import by.varyvoda.matvey.averagek.domain.KClass
import by.varyvoda.matvey.averagek.generator.ColorGenerator
import by.varyvoda.matvey.averagek.generator.PointGenerator
import by.varyvoda.matvey.averagek.util.getDistance
import by.varyvoda.matvey.averagek.view.DrawableKClass
import by.varyvoda.matvey.averagek.view.Generator
import by.varyvoda.matvey.averagek.view.KAverageDrawer
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.Point2D
import javafx.scene.canvas.Canvas
import javafx.scene.control.TextField
import javafx.scene.paint.Color
import java.lang.NumberFormatException

class HelloController {

    @FXML
    private lateinit var canvas: Canvas

    @FXML
    private lateinit var kCount: TextField

    @FXML
    private lateinit var stepMs: TextField

    private lateinit var kAverageDrawer: KAverageDrawer

    private val kAverage: KAverage<Point2D, Double> =
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

    private val pointGenerator: PointGenerator = PointGenerator()

    private val colorGenerator: ColorGenerator = ColorGenerator()

    @FXML
    fun initialize() {
        kAverageDrawer = KAverageDrawer(canvas.graphicsContext2D)
        pointGenerator.setLimits(canvas.width.toInt(), canvas.height.toInt())
    }

    @FXML
    private fun onHelloButtonClick() {
        kAverage.stop()
        val points: List<Point2D> = pointGenerator.generate(canvas.width.toInt() * canvas.height.toInt()).toList()
        kAverage.calculate(getNumber(kCount, 10), points, getNumber(stepMs, 0)) { result ->
            Platform.runLater { drawResult(result) }
        }
    }

    private fun getNumber(textField: TextField, or: Int): Int {
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
package by.varyvoda.matvey.averagek.view

import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color

class KAverageDrawer(private val graphicsContext: GraphicsContext) {

    fun draw(result: List<DrawableKClass>) {
        val pixelDrawer = graphicsContext.pixelWriter
        result.forEach { kClass ->
            run {
                kClass.items.forEach { item -> pixelDrawer.setColor(item.x.toInt(), item.y.toInt(), kClass.color) }
            }
        }
        result.forEach { kClass ->
            val x: Int = kClass.core.x.toInt()
            val y: Int = kClass.core.y.toInt()

            for(deltaX in -1..1) {
                for(deltaY in -1..1) {
                    pixelDrawer.setColor(x + deltaX, y + deltaY, Color.BLACK)
                }
            }
        }
    }
}
package by.varyvoda.matvey.averagek.generator

import by.varyvoda.matvey.averagek.view.Generator
import javafx.scene.paint.Color

class ColorGenerator : Generator<Color> {

    private companion object {
        val colors = arrayOf(
            Color.GREEN,
            Color.YELLOW,
            Color.RED,
            Color.BLUE,
            Color.GREENYELLOW,
            Color.GRAY,
            Color.GOLDENROD,
            Color.PALEVIOLETRED,
            Color.PERU,
            Color.PINK,
            Color.PURPLE,
            Color.SALMON,
            Color.SANDYBROWN,
            Color.BLUEVIOLET,
            Color.AQUAMARINE,
            Color.BROWN,
            Color.CYAN,
            Color.GRAY,
            Color.DARKKHAKI,
            Color.ORCHID,
            Color.BLANCHEDALMOND,
            Color.NAVY,
            Color.AQUAMARINE,
            Color.PLUM,
        )
    }

    private var nextIndex: Int = -1

    override fun next(): Color {
        nextIndex = (nextIndex + 1) % colors.size
        return colors[nextIndex]
    }

    fun get(index: Int): Color {
        return colors[index % colors.size]
    }
}
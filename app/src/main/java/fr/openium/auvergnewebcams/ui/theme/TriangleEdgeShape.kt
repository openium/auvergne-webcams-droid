package fr.openium.auvergnewebcams.ui.theme

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

class TriangleEdgeShape(private val offsetX: Int, private val offsetY: Int) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val trianglePath = Path().apply {
            val middleX = size.width / 2

            moveTo(x = middleX - offsetX, y = size.height - offsetY)
            lineTo(x = middleX + offsetX + 1, y = size.height - offsetY)
            lineTo(x = middleX + 1, y = size.height + (offsetY / 2))

            close()
        }

        return Outline.Generic(path = trianglePath)
    }
}
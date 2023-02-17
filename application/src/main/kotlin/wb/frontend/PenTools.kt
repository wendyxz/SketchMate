package wb.frontend

import javafx.scene.paint.Color

class PenTools() {
    var strokeColor = Color.RED
    var strokeWidth = 2.0
    var lineStyle = "solid"
    fun updatePen(style: String) {
        lineStyle = style
    }

    fun updatePen(size: Double) {
        strokeWidth = size
    }

    fun updatePen(color: Color) {
        strokeColor = color
    }
}
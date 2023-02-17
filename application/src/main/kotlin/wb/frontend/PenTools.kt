package wb.frontend

import javafx.scene.paint.Color

class PenTools() {
    public var strokeColor = Color.RED
    public var strokeWidth = 2.0
    public var lineStyle = "solid"
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
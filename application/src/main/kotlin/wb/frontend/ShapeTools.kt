package wb.frontend

import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import kotlin.math.max


class ShapeTools(resizableCanvas: Pane) {
    var cursorAnchorX = 0.0
    var cursorAnchorY = 0.0
    var mouseOffsetX = 0.0
    var mouseOffsetY = 0.0
    var canvas = resizableCanvas

    init {
        println("ShapeTools initialized")
    }

    private fun onPressedEvent(shape: Shape, event: MouseEvent) {
        if (cursorType == CursorType.pen) return
        cursorAnchorX = event.sceneX
        cursorAnchorY = event.sceneY
        mouseOffsetX = event.sceneX - shape.layoutX
        mouseOffsetY = event.sceneY - shape.layoutY
    }

    private fun onDraggedEvent(shape: Shape, event: MouseEvent) {
        if (cursorType == CursorType.pen) return
        shape.translateX = event.sceneX - cursorAnchorX
        shape.translateY = event.sceneY - cursorAnchorY
    }

    private fun onReleasedEvent(shape: Shape, event: MouseEvent) {
        if (cursorType == CursorType.pen) return

        shape.layoutX = max(-shape.layoutBounds.minX, event.sceneX - mouseOffsetX)
        shape.layoutY = max(-shape.layoutBounds.minY, event.sceneY - mouseOffsetY)

        shape.translateX = 0.0
        shape.translateY = 0.0
    }

    private fun makeDraggable(shape: Shape) {
        shape.setOnMousePressed { event -> onPressedEvent(shape, event) }
        shape.setOnMouseDragged { event -> onDraggedEvent(shape, event) }
        shape.setOnMouseReleased { event -> onReleasedEvent(shape, event) }
    }

    // create shapes
    fun createRectangle() {
        val r = Rectangle(50.0, 50.0, 50.0, 50.0)
        canvas.children.add(r)
        makeDraggable(r)
    }

    fun createCircle() {
        val c = Circle(200.0, 100.0, 25.0, Color.BLUE)
        canvas.children.add(c)
        makeDraggable(c)
    }
}
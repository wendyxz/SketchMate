package wb.frontend

import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape


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
        cursorAnchorX = event.sceneX
        cursorAnchorY = event.sceneY
        mouseOffsetX = event.sceneX-shape.layoutX
        mouseOffsetY = event.sceneY-shape.layoutY
    }
    private fun onDraggedEvent(shape: Shape, event: MouseEvent) {
        shape.translateX = event.getSceneX()-cursorAnchorX
        shape.translateY = event.getSceneY()-cursorAnchorY
    }
    private fun onReleasedEvent(shape: Shape, event: MouseEvent) {
        shape.layoutX = event.getSceneX() - mouseOffsetX
        shape.layoutY = event.getSceneY() - mouseOffsetY
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
        val r = Rectangle(50.0,50.00, Color.RED)
        r.x = 50.0
        r.y = 50.0
        canvas.children.add(r)
        makeDraggable(r)
    }

    fun createCircle() {
        val c = Circle(20.0, Color.BLUE)
        c.centerX = 300.0
        c.centerY = 100.0
        canvas.children.add(c)
        makeDraggable(c)
    }
}
package wb.frontend

import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.scene.transform.Scale
import kotlin.math.max


class ShapeTools(resizableCanvas: Pane) {
    var cursorAnchorX = 0.0
    var cursorAnchorY = 0.0
    var mouseOffsetX = 0.0
    var mouseOffsetY = 0.0
    var canvas = resizableCanvas
    private val scale = Scale()

    init {
        scale.pivotX = 0.0
        scale.pivotY = 0.0
        println("ShapeTools initialized")
    }

    fun setScale(scene: Scene) {
        scale.xProperty().bind(scene.widthProperty())
        scale.yProperty().bind(scene.heightProperty())
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
        shape.translateX = max(-shape.layoutBounds.minX, event.sceneX - cursorAnchorX)
        shape.translateY = max(-shape.layoutBounds.minY,event.sceneY - cursorAnchorY)
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
        shape.transforms.add(Scale(1.0 / scale.x, 1.0 / scale.y))
        shape.transforms.add(scale)
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
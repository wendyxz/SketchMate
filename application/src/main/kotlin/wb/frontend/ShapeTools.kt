package wb.frontend

import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.scene.transform.Scale
import javafx.stage.Popup
import java.util.*
import kotlin.math.max

private fun colorToHex(color: Color): String? {
    val hex2: String
    val hex1: String = Integer.toHexString(color.hashCode()).uppercase(Locale.getDefault())
    hex2 = when (hex1.length) {
        2 -> "000000"
        3 -> String.format("00000%s", hex1.substring(0, 1))
        4 -> String.format("0000%s", hex1.substring(0, 2))
        5 -> String.format("000%s", hex1.substring(0, 3))
        6 -> String.format("00%s", hex1.substring(0, 4))
        7 -> String.format("0%s", hex1.substring(0, 5))
        else -> hex1.substring(0, 6)
    }
    return hex2
}
fun addSubmenu(shape :Shape) {
    // Create font, color, and size controls

    var fillPicker = ColorPicker(shape.fill as Color?)
    fillPicker.prefWidth = 50.0;
    var borderPicker = ColorPicker(shape.stroke as Color?)
    borderPicker.prefWidth = 50.0;
    var sizeComboBox = ComboBox<Double>()
    sizeComboBox.items.addAll(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0, 11.0, 12.0, 13.0, 14.0, 15.0)
    sizeComboBox.selectionModel.select(4.0)
    sizeComboBox.prefWidth = 10.0

    // Create a horizontal box to hold the controls
    var controlsBox = HBox()
    controlsBox.spacing =0.0
    controlsBox.children.addAll(fillPicker, borderPicker, sizeComboBox)

    fillPicker.setOnAction {
        val hexcolor = colorToHex(fillPicker.value)
        shape.fill = Color.web(hexcolor)
    }
    borderPicker.setOnAction {
        val hexcolor= colorToHex(borderPicker.value)
        shape.stroke = Color.web(hexcolor)
    }
    sizeComboBox.setOnAction {
        val size = sizeComboBox.value
        shape.strokeWidth = size
    }
    val popup = Popup()
    popup.content.add(controlsBox)
    popup.isAutoHide = true
    shape.setOnMouseClicked { event ->
        val x: Double = event.screenX - 30.0
        val y: Double = event.screenY - shape.layoutBounds.height/2 - 10.0
        popup.show(shape, x, y)
    }
//        canvas.children.add(controlsBox)

}

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
        if (cursor == CursorType.pen) return
        if (cursor == CursorType.eraser){
            canvas.children.remove(shape)
            return
        }

        cursorAnchorX = event.sceneX
        cursorAnchorY = event.sceneY
        mouseOffsetX = event.sceneX - shape.layoutX
        mouseOffsetY = event.sceneY - shape.layoutY
    }

    private fun onDraggedEvent(shape: Shape, event: MouseEvent) {
        if (cursor == CursorType.pen) return
        shape.translateX = max(-shape.layoutBounds.minX, event.sceneX - cursorAnchorX)
        shape.translateY = max(-shape.layoutBounds.minY,event.sceneY - cursorAnchorY)
    }

    private fun onReleasedEvent(shape: Shape, event: MouseEvent) {
        if (cursor == CursorType.pen) return

        shape.layoutX = max(-shape.layoutBounds.minX, event.sceneX - mouseOffsetX)
        shape.layoutY = max(-shape.layoutBounds.minY, event.sceneY - mouseOffsetY)

        shape.translateX = 0.0
        shape.translateY = 0.0
    }

//    private fun makeDraggable(shape: Shape) {
//        shape.setOnMousePressed { event -> onPressedEvent(shape, event) }
//        shape.setOnMouseDragged { event -> onDraggedEvent(shape, event) }
//        shape.setOnMouseReleased { event -> onReleasedEvent(shape, event) }
//        shape.transforms.add(Scale(1.0 / scale.x, 1.0 / scale.y))
//        shape.transforms.add(scale)
//    }


    // create shapes
    fun createRectangle() {
        val r = Rectangle(0.0, 0.0, 50.0, 50.0)
        r.fill = Color.RED


        canvas.children.add(r)
        addSubmenu(r)
        DragResize.makeResizable(r, canvas);
    }

    fun createCircle() {
        val c = Circle(0.0, 0.0, 25.0, Color.BLUE)
        c.layoutX = 250.0
        c.layoutY = 200.0
        canvas.children.add(c)
        addSubmenu(c)
        DragResize.makeResizable(c, canvas);
    }
}
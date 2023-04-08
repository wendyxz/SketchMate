package wb.frontend

import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.scene.shape.Shape
import javafx.scene.transform.Scale
import javafx.stage.Popup
import wb.rootcanvas
import java.util.*
import kotlin.random.Random
private fun randomColor() : Color {
    return Color.rgb(Random.nextInt(256), Random.nextInt(256), Random.nextInt(256))
}
fun createRectangle() {
    val r = Rectangle(0.0, 0.0, 50.0, 50.0)
    r.fill = randomColor()
    rootcanvas.children.add(r)
    addSubmenu(r)
    DragResize.makeResizable(r, rootcanvas);
}

fun createCircle() {
    val c = Circle(0.0, 0.0, 25.0, randomColor())
    c.layoutX = 250.0
    c.layoutY = 200.0
    rootcanvas.children.add(c)
    addSubmenu(c)
    DragResize.makeResizable(c, rootcanvas);
}

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

    // create shapes

}
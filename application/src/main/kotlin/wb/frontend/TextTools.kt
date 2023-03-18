package wb.frontend

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.ColorPicker
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.transform.Scale
import java.util.*


class TextTools(resizableCanvas: Pane) {
    var canvas = resizableCanvas
    private val scale = Scale()
    private var oldfont = "System";
    private var oldfill = colorToHex(Color.BLACK);
    private var oldsize = 12.0;

    init {
        scale.pivotX = 0.0
        scale.pivotY = 0.0
    }

    fun setScale(scene: Scene) {
        scale.xProperty().bind(scene.widthProperty())
        scale.yProperty().bind(scene.heightProperty())
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

    private fun setStyle(textBox: TextArea) {
        textBox.style = "-fx-font-family: ${oldfont}; -fx-text-fill: #${oldfill}; -fx-font-size: ${oldsize}px;"
    }

    fun createTextBox() {
        var textBox = TextArea()
        textBox.isWrapText = true
        textBox.prefRowCount = 5
        textBox.prefColumnCount = 10

        // Create font, color, and size controls
        var fontComboBox = ComboBox<String>()
        fontComboBox.items.addAll(Font.getFamilies())
        fontComboBox.selectionModel.select("System")
        var colorPicker = ColorPicker(Color.BLACK)
        var sizeComboBox = ComboBox<Double>()
        sizeComboBox.items.addAll(8.0, 10.0, 12.0, 14.0, 16.0, 18.0, 20.0, 22.0, 24.0)
        sizeComboBox.selectionModel.select(12.0)

        // Create a horizontal box to hold the controls
        var controlsBox = HBox()
        controlsBox.spacing = 10.0
        controlsBox.children.addAll(fontComboBox, colorPicker, sizeComboBox)

        var group = VBox()
        group.alignment = Pos.TOP_CENTER
        var drag = Label("drag")
        group.children.addAll(drag, controlsBox, textBox)

        drag.isVisible = false
        textBox.focusedProperty().addListener { obs, oldVal, newVal ->
            drag.isVisible = newVal
        }

        makeDraggable(drag, group)
        DragResizeMod.makeResizable(group, null);
        group.layoutX = 100.0
        group.layoutY = 100.0
        group.transforms.add(Scale(1.0 / scale.x, 1.0 / scale.y))
        group.transforms.add(scale)
        canvas.children.add(group)

        // Add event listeners to update the text area's font, color, and size
        fontComboBox.setOnAction {
            oldfont = fontComboBox.value
            setStyle(textBox)
        }
        colorPicker.setOnAction {
            oldfill = colorToHex(colorPicker.value)
            setStyle(textBox)
        }
        sizeComboBox.setOnAction {
            oldsize = sizeComboBox.value
            setStyle(textBox)
        }
    }
}

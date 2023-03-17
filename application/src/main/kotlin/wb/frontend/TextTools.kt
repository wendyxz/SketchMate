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


class TextTools(resizableCanvas: Pane) {
    var canvas = resizableCanvas
    private val scale = Scale()

    init {
        scale.pivotX = 0.0
        scale.pivotY = 0.0
    }

    fun setScale(scene: Scene) {
        scale.xProperty().bind(scene.widthProperty())
        scale.yProperty().bind(scene.heightProperty())
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
            textBox.style = "-fx-font-family: ${fontComboBox.value};"
        }
        colorPicker.setOnAction {
            textBox.style = "-fx-text-fill: ${colorPicker.value};"
        }
        sizeComboBox.setOnAction {
            textBox.style = "-fx-font-size: ${sizeComboBox.value}px;"
        }
    }
}

package wb.frontend

import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox


class TextTools(resizableCanvas: Pane) {
    var canvas = resizableCanvas
    fun createTextBox() {
        var textBox = TextArea()
        textBox.isWrapText=true
        textBox.prefRowCount = 5
        textBox.prefColumnCount = 10
        var group = VBox()
        group.alignment = Pos.TOP_CENTER
        var drag = Label("drag")
        group.children.addAll(drag, textBox)

        drag.isVisible = false
        textBox.focusedProperty().addListener {
                obs, oldVal, newVal -> if (newVal) { drag.isVisible=true } else { drag.isVisible=false }
        }

        makeDraggable(drag, group)
        group.layoutX = 100.0
        group.layoutY = 100.0
        canvas.children.add(group)
    }
}
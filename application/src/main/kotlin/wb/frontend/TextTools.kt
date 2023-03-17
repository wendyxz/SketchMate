package wb.frontend

import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
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
        var group = VBox()
        group.alignment = Pos.TOP_CENTER
        var drag = Label("drag")
        group.children.addAll(drag, textBox)

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
    }
}

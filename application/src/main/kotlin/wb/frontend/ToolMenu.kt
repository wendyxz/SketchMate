package wb.frontend

import javafx.geometry.Orientation
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.ToolBar

class ToolMenu() : ToolBar() {
    private val textOption = MenuButton("Text")
    private val rectangle = MenuItem("rect")
    private val circle = MenuItem("circ")
    private val shapeOption = MenuButton("SHAPE")
    private val penOption = MenuButton("Pen")
    private val eraserOption = MenuButton("Eraser")

    init {
        orientationProperty().set(Orientation.VERTICAL)
        shapeOption.items.add(rectangle)
        shapeOption.items.add(circle)
        items.add(textOption)
        items.add(shapeOption)
        items.add(penOption)
        items.add(eraserOption)
    }
}
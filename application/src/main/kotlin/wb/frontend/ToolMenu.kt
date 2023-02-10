package wb.frontend

import javafx.collections.FXCollections.observableArrayList
import javafx.geometry.Orientation
import javafx.scene.control.*

class ToolMenu() : ToolBar() {
    private val cursorOption = Button("Cursor")
    private val textOption = Button("Text")

    private val penOption = MenuButton("pen")
    private val st1 = MenuItem("style")
    private val st2 = MenuItem("style")
    private val st3 = MenuItem("custom style")
    private val sp1 = SeparatorMenuItem()
    private val co1 = MenuItem("colour")
    private val co2 = MenuItem("colour")
    private val co3 = MenuItem("custom colour")
    private val sp2 = SeparatorMenuItem()
    private val sz1 = MenuItem("size")
    private val sz2 = MenuItem("size")
    private val sz3 = MenuItem("custom size")


    private val shapeOption = MenuButton("shape")
    private val rectangle = MenuItem("rect")
    private val circle = MenuItem("circ")

    private val eraserOption = Button("Eraser")
    init {
        orientationProperty().set(Orientation.VERTICAL)

        penOption.items.addAll(st1, st2, st3)
        penOption.items.add(sp1)
        penOption.items.addAll(co1, co2, co3)
        penOption.items.add(sp2)
        penOption.items.addAll(sz1, sz2, sz3)

        shapeOption.items.addAll(rectangle, circle)

        items.addAll(cursorOption, textOption, penOption, shapeOption, eraserOption)
    }
}
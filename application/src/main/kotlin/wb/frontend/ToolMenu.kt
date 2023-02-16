package wb.frontend

import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import wb.CursorType


class ToolMenu(
    setCursorType: (ct: CursorType) -> Unit,
    penTools: PenTools
) : ToolBar() {
    private val cursorOption = Button("Cursor")
    private val textOption = Button("Text")
    private val penOption = PenOption(penTools)
    private val shapeOption = MenuButton("Shape")
    private val eraserOption = Button("Eraser")

    private val eraserMenu = ContextMenu()

    private val eraser1 = MenuItem("1.0")
    private val eraser2 = MenuItem("3.0")
    private val eraser3 = MenuItem("5.0")
    private val eraser4 = MenuItem("8.0")
    private val eraser5 = MenuItem("10.0")

    private val rectangle = MenuItem("rect")
    private val circle = MenuItem("circ")

    init {
        orientationProperty().set(Orientation.VERTICAL)

        // Cursor
        cursorOption.setOnMouseClicked { setCursorType(CursorType.cursor) }
        // Text
        textOption.setOnMouseClicked { setCursorType(CursorType.textbox) }
        // Pen
        penOption.setOnMouseClicked { setCursorType(CursorType.pen) }

        // Eraser
        eraserMenu.items.addAll(eraser1, eraser2, eraser3, eraser4, eraser5)
        eraserOption.setOnMouseClicked {
            setCursorType(CursorType.pen)
            penTools.updatePen(Color.WHITE)
            if (!eraserMenu.isShowing) {
                eraserMenu.show(eraserOption, Side.RIGHT, 0.0, -100.0)
            } else {
                eraserMenu.hide()
            }
        }

        eraser1.setOnAction { penTools.updatePen(1.0) }
        eraser2.setOnAction { penTools.updatePen(3.0) }
        eraser3.setOnAction { penTools.updatePen(5.0) }
        eraser4.setOnAction { penTools.updatePen(8.0) }
        eraser5.setOnAction {penTools.updatePen(10.0)}

        // Shapes: Rectangle, Circle
        shapeOption.items.addAll(rectangle, circle)
        rectangle.setOnAction { setCursorType(CursorType.rectangle) }
        circle.setOnAction { setCursorType(CursorType.circle) }

        // Tool Menu
        items.addAll(cursorOption, textOption, penOption, shapeOption, eraserOption)
    }
}


class PenOption(penTools: PenTools) : ToggleButton("Pen") {
    private val penMenu = ContextMenu()
    private val styleToggles = ToggleGroup()
    private val colourToggles = ToggleGroup()
    private val sizeToggles = ToggleGroup()

    fun getStyles(): List<ToggleButton> {
        return listOf(
            ToggleButton("solid"),
            ToggleButton("dashed"),
            ToggleButton("dotted"))
    }
    fun getColours(): List<ToggleButton> {
        return listOf(
            ToggleButton("black"),
            ToggleButton("blue"),
            ToggleButton("red"),
            ToggleButton("green"),
            ToggleButton("yellow"),
            ToggleButton("orange"),
            ToggleButton("purple"),
        )
    }
    fun getSizes(): List<ToggleButton> {
        return listOf(
            ToggleButton("1.0"),
            ToggleButton("2.0"),
            ToggleButton("3.0"),
            ToggleButton("4.0"),
            ToggleButton("5.0"),
        )
    }

    private fun groupButtons(buttons: List<ToggleButton>, group: ToggleGroup): CustomMenuItem {
        var vbox = VBox()
        vbox.children.addAll(buttons)
        for (button in buttons) {
            button.toggleGroup = group
        }
        var menuItem = CustomMenuItem(vbox)
        menuItem.isHideOnClick = false
        return menuItem
    }


    init {
        var styles = getStyles()
        var colors = getColours()
        var sizes = getSizes()
        for (button in styles) {
            button.setOnMouseClicked { penTools.updatePen(button.text) }
        }
        for (button in colors) {
            button.setOnMouseClicked { penTools.updatePen(Color.valueOf(button.text)) }
        }
        for (button in sizes) {
            button.setOnMouseClicked { penTools.updatePen(button.text.toDouble()) }
        }
        penMenu.isAutoHide = false
        penMenu.items.addAll(
            groupButtons(styles, styleToggles),
            SeparatorMenuItem(),
            groupButtons(colors, colourToggles),
            SeparatorMenuItem(),
            groupButtons(sizes, sizeToggles),
        )
        this.setOnAction {
            if (!penMenu.isShowing) {
                penMenu.show(this, Side.RIGHT, 0.0, -100.0)
            } else {
                penMenu.hide()
            }
        }
    }
}
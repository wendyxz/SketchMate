package wb.frontend.Tools

import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class PenTools() {
    var strokeColor = Color.RED
    var strokeWidth = 2.0
    var lineStyle = "solid"
    var eraser = false
    fun updatePen(style: String) {
        lineStyle = style
    }

    fun updatePen(size: Double) {
        strokeWidth = size
    }

    fun updatePen(color: Color) {
        strokeColor = color
    }

    fun updateEraser(turnOn: Boolean){
        eraser = turnOn
    }
}



class PenOption(penTools: PenTools) : ToggleButton("Pen (CMD +D)") {
    private val penMenu = ContextMenu()
    private val styleToggles = ToggleGroup()
    private val colourToggles = ToggleGroup()
    private val sizeToggles = ToggleGroup()

    fun getStyles(): List<ToggleButton> {
        return listOf(
            ToggleButton("solid"),
            ToggleButton("dashed"),
            ToggleButton("dotted")
        )
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
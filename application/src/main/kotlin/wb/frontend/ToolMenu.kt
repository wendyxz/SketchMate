package wb.frontend

import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.paint.Color
import wb.frontend.Tools.CursorType
import wb.frontend.Tools.PenOption
import wb.frontend.Tools.PenTools


class ToolMenu(
    setCursorType: (ct: CursorType) -> Unit,
    penTools: PenTools
) : ToolBar() {
    private val cursorOption = Button("Cursor (CMD+V)")
    private val textOption = Button("Text (CMD+B)")
    private val penOption = PenOption(penTools)
    private val shapeOption = MenuButton("Shape")
    private val eraserOption = Button("Eraser (CMD+E)")

    private val eraserMenu = ContextMenu()

    private val eraser1 = MenuItem("1.0")
    private val eraser2 = MenuItem("3.0")
    private val eraser3 = MenuItem("5.0")
    private val eraser4 = MenuItem("8.0")
    private val eraser5 = MenuItem("10.0")

    private val rectangle = MenuItem("rect (CMD+R)")
    private val circle = MenuItem("circ (CMD+C)")
    private val triangle = MenuItem("tri (CMD+T)")

    val lightStyle = "-fx-text-fill: crimson  ; -fx-font-size: 14px;"

    init {
        orientationProperty().set(Orientation.VERTICAL)

        // Cursor
        cursorOption.setOnMouseClicked { setCursorType(CursorType.cursor) }

        // Text
        textOption.setOnMouseClicked { setCursorType(CursorType.textbox) }
        textOption.setOnAction { setCursorType(CursorType.cursor) }

        // Pen
        penOption.setOnMouseClicked {
            setCursorType(CursorType.pen)
            penTools.updateEraser(false)
        }

        // Shapes: Rectangle, Circle
        shapeOption.items.addAll(rectangle, circle, triangle)
        rectangle.setOnAction {
            setCursorType(CursorType.rectangle)
        }
        circle.setOnAction {
            setCursorType(CursorType.circle)
        }
        triangle.setOnAction {
            setCursorType(CursorType.triangle)
        }

        // Eraser
        eraserMenu.items.addAll(eraser1, eraser2, eraser3, eraser4, eraser5)
        eraserOption.setOnMouseClicked {
            setCursorType(CursorType.eraser)
            penTools.updatePen(Color.WHITE)
            penTools.updateEraser(true)
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
        eraser5.setOnAction { penTools.updatePen(10.0) }

        // Tool Menu
        items.addAll(cursorOption, textOption, penOption, shapeOption, eraserOption)
        setTheme("light")
    }

    fun setTheme(theme: String) {
        if (theme == "light") {
            style = "-fx-background-color: lightblue;"
            cursorOption.style = "-fx-background-color: lightcyan;"
            textOption.style = "-fx-background-color: lightcyan;"
            penOption.style = "-fx-background-color: lightcyan;"
            shapeOption.style = "-fx-background-color: lightcyan;"
            eraserOption.style = "-fx-background-color: lightcyan;"
            eraser1.style = lightStyle
            eraser2.style = lightStyle
            eraser3.style = lightStyle
            eraser4.style = lightStyle
            eraser5.style = lightStyle
            rectangle.style = lightStyle
            circle.style = lightStyle
            triangle.style = lightStyle
        } else {
            style = "-fx-background-color: darkslategray;"
            cursorOption.style = "-fx-text-fill: aliceblue   ;-fx-background-color: indigo  ;"
                textOption.style = "-fx-text-fill: aliceblue   ;-fx-background-color: indigo  ;"
            penOption.style = "-fx-text-fill: aliceblue   ;-fx-background-color: indigo  ;"
            shapeOption.style = "-fx-text-fill: aliceblue   ;-fx-background-color: indigo  ;"
            eraserOption.style = "-fx-text-fill: aliceblue   ;-fx-background-color: indigo  ;"
        }
    }
}

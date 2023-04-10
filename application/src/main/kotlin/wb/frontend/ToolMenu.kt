package wb.frontend

import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.layout.VBox
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
    }
}

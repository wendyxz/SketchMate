package wb.frontend

import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.control.*
import wb.CursorType
import javafx.scene.paint.Color
import kotlin.reflect.KMutableProperty0

class ToolMenu(
    setCursorType: (ct: CursorType) -> Unit,
    strokecolor: KMutableProperty0<Color>,
    strokewidth: KMutableProperty0<Double>,
    linestyle: KMutableProperty0<String>
) : ToolBar() {
    private val cursorOption = Button("Cursor")
    private val textOption = Button("Text")
    private val penOption = ToggleButton("Pen")
    private val shapeOption = MenuButton("Shape")
    private val eraserOption = Button("Eraser")

    private val penMenu = ContextMenu()
    private val eraserMenu = ContextMenu()

    private val stSolid = MenuItem("solid")
    private val stDashed = MenuItem("dashed")
    private val stDotted = MenuItem("dotted")

    private val sp1 = SeparatorMenuItem()

    private val coBlack = MenuItem("black")
    private val coBlue = MenuItem("blue")
    private val coRed = MenuItem("red")
    private val coGreen = MenuItem("green")
    private val coPurple = MenuItem("purple")
    private val coYellow = MenuItem("yellow")
    private val coOrange = MenuItem("orange")

    private val sp2 = SeparatorMenuItem()
    private val sz1 = MenuItem("1.0")
    private val sz2 = MenuItem("2.0")
    private val sz3 = MenuItem("3.0")
    private val sz4 = MenuItem("4.0")
    private val sz5 = MenuItem("5.0")

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
        textOption.setOnMouseClicked {
            setCursorType(CursorType.textbox)
        }

        // Pen
        penMenu.isAutoHide = false

        stSolid.setOnAction {
            linestyle.set("Solid")
        }
        stDashed.setOnAction {
            linestyle.set("Dashed")
        }
        stDotted.setOnAction {
            linestyle.set("Dotted")
        }

        coBlack.setOnAction { strokecolor.set(Color.BLACK) }
        coRed.setOnAction { strokecolor.set(Color.RED) }
        coOrange.setOnAction { strokecolor.set(Color.ORANGE) }
        coYellow.setOnAction { strokecolor.set(Color.YELLOW) }
        coGreen.setOnAction { strokecolor.set(Color.GREEN) }
        coBlue.setOnAction { strokecolor.set(Color.BLUE) }
        coPurple.setOnAction { strokecolor.set(Color.PURPLE) }

        sz1.setOnAction { strokewidth.set(1.0) }
        sz2.setOnAction { strokewidth.set(2.0) }
        sz3.setOnAction { strokewidth.set(3.0) }
        sz4.setOnAction { strokewidth.set(4.0) }
        sz5.setOnAction { strokewidth.set(5.0) }

        penMenu.items.addAll(
            stSolid,
            stDashed,
            stDotted,
            sp1,
            coBlack,
            coRed,
            coOrange,
            coYellow,
            coGreen,
            coBlue,
            coPurple,
            sp2,
            sz1,
            sz2,
            sz3,
            sz4,
            sz5
        )
        penOption.setOnMouseClicked {
            setCursorType(CursorType.pen)
            strokewidth.set(2.0)
            if (!penMenu.isShowing) {
                penMenu.show(penOption, Side.RIGHT, 0.0, -100.0)
            } else {
                penMenu.hide()
            }
        }

        eraser1.setOnAction { strokewidth.set(1.0) }
        eraser2.setOnAction { strokewidth.set(3.0) }
        eraser3.setOnAction { strokewidth.set(5.0) }
        eraser4.setOnAction { strokewidth.set(8.0) }
        eraser5.setOnAction { strokewidth.set(10.0) }

        eraserMenu.items.addAll(eraser1, eraser2, eraser3, eraser4, eraser5)

        eraserOption.setOnMouseClicked {
            setCursorType(CursorType.pen)
            strokecolor.set(Color.WHITE)
            if (!eraserMenu.isShowing) {
                eraserMenu.show(eraserOption, Side.RIGHT, 0.0, -100.0)
            } else {
                eraserMenu.hide()
            }
        }

        // Shapes: Rectangle, Circle
        shapeOption.items.addAll(rectangle, circle)

        // Tool Menu
        items.addAll(cursorOption, textOption, penOption, shapeOption, eraserOption)


    }
}
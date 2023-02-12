package wb.frontend

import javafx.collections.FXCollections.observableArrayList
import javafx.event.EventHandler
import javafx.geometry.Orientation
import javafx.geometry.Side
import javafx.scene.control.*
import javafx.scene.layout.VBox
import javafx.stage.PopupWindow.AnchorLocation
import wb.CursorType

class ToolMenu(setCursorType: (ct : CursorType) -> Unit) : ToolBar() {
    private val cursorOption = Button("Cursor")
    private val textOption = Button("Text")
    private val penOption = ToggleButton("Pen")
    private val shapeOption = MenuButton("shape")
    private val eraserOption = Button("Eraser")
    private val penMenu = ContextMenu()

    private val st1 = CustomMenuItem(Label("solid"))
    private val st2 = CustomMenuItem(Label("dotted"))
    private val st3 = CustomMenuItem(Label("style"))
    private val sp1 = SeparatorMenuItem()
    private val co1 = CustomMenuItem(Label("black"))
    private val co2 = CustomMenuItem(Label("red"))
    private val co3 = CustomMenuItem(Label("choose colour"))
    private val sp2 = SeparatorMenuItem()
    private val sz1 = CustomMenuItem(Label("size"))
    private val sz2 = CustomMenuItem(Label("size"))
    private val sz3 = CustomMenuItem(Label("choose size"))

    private val rectangle = MenuItem("rect")
    private val circle = MenuItem("circ")

    init {
        orientationProperty().set(Orientation.VERTICAL)

        // Cursor

        cursorOption.setOnMouseClicked { setCursorType(CursorType.cursor) }
        textOption.setOnMouseClicked {
            setCursorType(CursorType.textbox)
        }

        // Eraser
        eraserOption.setOnMouseClicked { setCursorType(CursorType.eraser) }

        // Pen
        penMenu.isAutoHide = false
        st1.hideOnClickProperty().set(false)
        st2.hideOnClickProperty().set(false)
        st3.hideOnClickProperty().set(false)
        co1.hideOnClickProperty().set(false)
        co2.hideOnClickProperty().set(false)
        co3.hideOnClickProperty().set(false)
        sz1.hideOnClickProperty().set(false)
        sz2.hideOnClickProperty().set(false)
        sz3.hideOnClickProperty().set(false)
        penMenu.items.addAll(st1, st2, st3, sp1, co1, co2, co3, sp2, sz1, sz2, sz3)
        penOption.setOnMouseClicked {
            setCursorType(CursorType.pen)
            if (!penMenu.isShowing) {
                penMenu.show(penOption, Side.RIGHT, 0.0, -100.0)
            } else {
                penMenu.hide()
            }
        }
        // Shapes: Rectangle, Circle
        shapeOption.items.addAll(rectangle, circle)
        
        // Tool Menu
        items.addAll(cursorOption, textOption, penOption, shapeOption, eraserOption)
    }
}
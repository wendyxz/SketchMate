package wb.frontend

import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import wb.*


fun setCursorType(ctype: CursorType) {
    pathTools.getPenTools().updateEraser(false)
    cursor = ctype

    when (cursor) {
        CursorType.cursor -> pathTools.cancelPath()
        CursorType.textbox -> {
            setCursorType(CursorType.cursor)
            textTools.createTextBox()
        }
        CursorType.pen -> pathTools.initPath()
        CursorType.eraser -> {
            pathTools.getPenTools().updateEraser(true)
            pathTools.initPath()
        }
        CursorType.circle -> {
            createCircle()
            setCursorType(CursorType.cursor)
        }
        CursorType.rectangle -> {
            println("creating rect")
            createRectangle()
            setCursorType(CursorType.cursor)
        }
    }
}

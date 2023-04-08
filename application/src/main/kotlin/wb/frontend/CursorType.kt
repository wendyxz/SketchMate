package wb.frontend

import wb.pathTools
import wb.save
import wb.textTools

enum class CursorType {
    cursor,
    textbox,
    pen,
    rectangle,
    circle,
    eraser
}

var cursor = CursorType.cursor

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
            createRectangle()
            setCursorType(CursorType.cursor)
        }
    }
    save()
}

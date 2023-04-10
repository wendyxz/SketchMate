package wb.frontend.Tools

import wb.helper.save
import wb.pathTools
import wb.textTools

enum class CursorType {
    cursor,
    textbox,
    pen,
    rectangle,
    circle,
    triangle,
    eraser
}

var cursor = CursorType.cursor

fun setCursorType(ctype: CursorType) {
    pathTools.getPenTools().updateEraser(false)
    cursor = ctype

    when (cursor) {
        CursorType.cursor -> pathTools.cancelPath()
        CursorType.textbox -> {
            textTools.createTextBox()
            setCursorType(CursorType.cursor)
        }
        CursorType.pen -> pathTools.initPath()
        CursorType.circle -> {
            createCircle()
            setCursorType(CursorType.cursor)
        }
        CursorType.rectangle -> {
            createRectangle()
            setCursorType(CursorType.cursor)
        }
        CursorType.triangle -> {
            createTriangle()
            setCursorType(CursorType.cursor)
        }

        CursorType.eraser -> {
            pathTools.getPenTools().updateEraser(true)
            pathTools.initPath()
        }
    }
    if(cursor != CursorType.cursor) save()
}

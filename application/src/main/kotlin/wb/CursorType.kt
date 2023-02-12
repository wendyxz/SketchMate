package wb


enum class CursorType {
    cursor,
    textbox,
    pen,
    rectangle,
    circle,
    eraser
}

var ct = CursorType.cursor

fun setCursorType(ctype: CursorType) {
    ct = ctype
    when (ct) {
        CursorType.cursor -> println("cursor")
        CursorType.textbox -> println("text")
        CursorType.pen -> println("pen")
        CursorType.rectangle -> println("rectangle")
        CursorType.circle -> println("circle")
        CursorType.eraser -> println("eraser")
    }
}

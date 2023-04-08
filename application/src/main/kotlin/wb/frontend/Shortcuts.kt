package wb.frontend

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import wb.backend.createBoard
import wb.load
import wb.pathTools
import wb.save


fun addKeyListener(stage: Stage) {
    stage.scene.addEventFilter(KeyEvent.KEY_PRESSED) { event -> keyPressHandler(event, stage) }
}

fun keyPressHandler(event: KeyEvent, stage: Stage) {
    println("keyhandler")
    println(event.isShortcutDown)
    println(event.code)
    if (event.isShortcutDown or event.isControlDown) {
        when (event.code) {
            // Edit Functions
            KeyCode.S -> { save() }     // Save
            KeyCode.O -> { load() }     // Open
            KeyCode.N -> { createBoard("newboard", "") } // New board
            KeyCode.P -> { exportPDF(stage) }   // Pdf
            KeyCode.I -> { exportPNG(stage) }   // Image (png)

            // Canvas Functions
            KeyCode.V -> { setCursorType(CursorType.cursor)}        // Void cursor
            KeyCode.R -> { setCursorType(CursorType.rectangle) }    // Rectangle
            KeyCode.C -> { setCursorType(CursorType.circle) }       // Circle
            KeyCode.D -> { setCursorType(CursorType.pen) }          // Draw
            KeyCode.E -> { setCursorType(CursorType.eraser) }       // Eraser
            KeyCode.T -> { setCursorType(CursorType.textbox) }      // Textbox

        }
    }
}


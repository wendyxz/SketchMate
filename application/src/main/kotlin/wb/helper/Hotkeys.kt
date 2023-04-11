package wb.frontend

import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.stage.Stage
import wb.backend.createBoard
import wb.frontend.Tools.CursorType
import wb.frontend.Tools.setCursorType
import wb.helper.exportPDF
import wb.helper.exportPNG
import wb.helper.load
import wb.helper.save

fun addKeyListener(stage: Stage) {
    stage.scene.addEventFilter(KeyEvent.KEY_PRESSED) { event ->
        keyPressHandler(event, stage)
    }
}

fun keyPressHandler(event: KeyEvent, stage: Stage) {
    if (event.isShortcutDown or event.isControlDown) {
        when (event.code) {
            // Shortcuts for Top Menu
            KeyCode.S -> {
                save()
            }

            KeyCode.O -> {
                load()
            }

            KeyCode.N -> {
                createBoard("newboard", "")
            }

            KeyCode.P -> {
                exportPDF(stage)
            }

            KeyCode.I -> {
                exportPNG(stage)
            }

            // Shortcuts for Tool Menu
            KeyCode.V -> {
                setCursorType(CursorType.cursor)
            }

            KeyCode.R -> {
                setCursorType(CursorType.rectangle)
            }

            KeyCode.C -> {
                setCursorType(CursorType.circle)
            }

            KeyCode.T -> {
                setCursorType(CursorType.triangle)
            }

            KeyCode.D -> {
                setCursorType(CursorType.pen)
            } // with previous style
            KeyCode.E -> {
                setCursorType(CursorType.eraser)
            }

            KeyCode.B -> {
                setCursorType(CursorType.textbox)
            }
        }
    }
}


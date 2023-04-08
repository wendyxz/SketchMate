package wb.frontend

import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import wb.load
import wb.save
import java.security.Key


fun keyPressHandler(event: KeyEvent) {
    println("keyhandler")
    println(event.isShortcutDown)
    println(event.code)
    if (event.isShortcutDown or event.isControlDown) {
        when (event.code) {
            KeyCode.S -> { save("application/data.json"); println("saving") }
            KeyCode.O -> { load("application/data.json"); println("loading") }
            KeyCode.N -> {} // new board
            KeyCode.P -> {} // print png/pdf

        }
    }
}
fun addShortcuts(scene: Scene) {
//    scene.setOnKeyPressed { event ->
//        if (event.isControlDown) {
//            when (event.code) {
//                KeyCode.S -> { save("local.json"); println("saving") }
//                KeyCode.O -> { load("local.json"); println("loading") }
//                KeyCode.N -> {} // new board
//                KeyCode.P -> {} // print png/pdf
//
//            }
//        }
//    }
    scene.addEventFilter(KeyEvent.KEY_PRESSED) { event -> keyPressHandler(event) }
//    scene.addEventFilter(
//        KeyEvent.KEY_PRESSED
//    ) { event -> System.out.println("Pressed: " + event.code) }

}
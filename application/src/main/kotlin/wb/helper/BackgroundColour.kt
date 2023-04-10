package wb.helper

import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color
import wb.rootcanvas

fun setBackgroundColour(color: Color) {
    rootcanvas.background = Background(BackgroundFill(color, null, null))
}
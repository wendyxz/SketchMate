package wb

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import wb.frontend.TopMenu

class Main : Application() {

    override fun start(stage: Stage) {
        val border = BorderPane()
        stage.scene = Scene(border, 800.0, 600.0)
        stage.title = "WhiteBoard"
        stage.minWidth = 320.0
        stage.minHeight = 240.0

        border.top = TopMenu()

        stage.show()
    }

}
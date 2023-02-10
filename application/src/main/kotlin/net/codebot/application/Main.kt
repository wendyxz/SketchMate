package net.codebot.application

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.stage.Stage

class Main : Application() {

    override fun start(stage: Stage) {
        val button = Button("First Button")
        val pane = Pane(button)
        val scene = Scene(pane, 860.0, 605.0)

        stage.scene = scene
        stage.title = "WhiteBoard"
        stage.minWidth = 640.0
        stage.minHeight = 480.0
        stage.maxWidth = 1200.0
        stage.maxHeight = 800.0

        stage.show()
    }

}
package net.codebot.application

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import kotlin.system.exitProcess

class Main : Application() {
    private val width = 400.0
    private val height = 300.0

    override fun start(stage: Stage) {
        val group = StackPane()
        val scene = Scene(group, width, height)

        // setup background
        scene.fill = Color.GRAY
        val sceneHandler =
            EventHandler<MouseEvent> { e ->
                println("Scene clicked (" + e.x + "," + e.y + ")")
                if (scene.fill === Color.GRAY) {
                    scene.fill = Color.DARKGRAY
                } else {
                    scene.fill = Color.GRAY
                }
            }
        // add the handler as an EventFilter, which is fired during the CAPTURE (down) phase
        // scene.addEventFilter(MouseEvent.MOUSE_CLICKED, sceneHandler);

        // add the handler as an EventHandler, which is fired during the BUBBLE (up) phase
        scene.addEventHandler(MouseEvent.MOUSE_CLICKED, sceneHandler)

        // add foreground shape
        val rectangle = Rectangle(125.0, 150.0, Color.YELLOW)
        val rectangleHandler =
            EventHandler { event : MouseEvent ->
                println("Shape clicked (" + event.x + "," + event.y + ")")
                if (rectangle.fill === Color.YELLOW) {
                    rectangle.fill = Color.DARKGOLDENROD
                } else {
                    rectangle.fill = Color.YELLOW
                }
            }
        // add the handler as an EventFilter, which is fired during the CAPTURE (down) phase
        // rectangle.addEventFilter(MouseEvent.MOUSE_CLICKED, rectangleHandler);

        // add the handler as an EventHandler, which is fired during the BUBBLE (up) phase
        rectangle.addEventHandler(MouseEvent.MOUSE_CLICKED, rectangleHandler)

        // add to the scene and display
        group.children.add(rectangle)
        stage.onCloseRequest = EventHandler {
            println("Quitting")
            exitProcess(0)
        }
        stage.title = "EventHandlers2: Event Handlers"
        stage.scene = scene
        stage.show()
    }

}
package wb

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import wb.backend.createBoard
import wb.frontend.*
import java.io.*

@Serializable
class TypeWrapper(val type: String, val string: String)

@Serializable
class TimeSerializer(val time: Long, val whiteboard: List<String>)

@Serializable
data class Window(val width: Double, val height: Double, val x: Double, val y: Double)

@Serializable
class UpdateBoardRequestBody(val name: String, val json: TypeWrapper)


var root = BorderPane()
val rootcanvas = Pane()
val shapeTools = ShapeTools(rootcanvas)
val textTools = TextTools(rootcanvas)
val pathTools = PathTools(rootcanvas)
var autoSyncTimeStamp = -1L




class Main : Application() {
    private var backgroundFill = BackgroundFill(Color.WHITE, null, null)
    private var background = Background(backgroundFill)

    override fun start(stage: Stage) {
        val topMenu = TopMenu(stage)
        stage.title = "WhiteBoard"
        val file = File("window.json")
        var sceneWidth = 800.0
        var sceneHeight = 600.0
        if (file.length() != 0L) {
            val json = file.readText()
            val window = Json.decodeFromString<Window>(json)
            stage.x = window.x
            stage.y = window.y
            sceneWidth = window.width
            sceneHeight = window.height
        }
        stage.minWidth = 480.0
        stage.minHeight = 320.0

        var loginMenu = LoginMenu(root, stage, sceneWidth, sceneHeight)
        root.center = rootcanvas
        root.top = topMenu
        root.left = ToolMenu(::setCursorType, pathTools.getPenTools(), ::createShape)
        rootcanvas.background = background

        pathTools.setScale(stage.scene)
        shapeTools.setScale(stage.scene)
        textTools.setScale(stage.scene)
        saveWindow(stage)
    }

    private fun saveWindow(stage: Stage) {
        stage.setOnCloseRequest {
            val file = File("window.json")
            val window = Window(stage.scene.width, stage.scene.height, stage.x, stage.y)
            val json = Json.encodeToString(window)
            file.writeText(json)
        }
    }
//    fun wb.frontend.addKeyListener(stage: Stage) {
//        stage.scene.addEventFilter(KeyEvent.KEY_PRESSED) { event -> wb.frontend.keyPressHandler(event, stage) }
//    }
//
//    private fun wb.frontend.keyPressHandler(event: KeyEvent, stage: Stage) {
//        println("keyhandler")
//        println(event.isShortcutDown)
//        println(event.code)
//        if (event.isShortcutDown or event.isControlDown) {
//            when (event.code) {
//                KeyCode.S -> { save();}     // save
//                KeyCode.O -> { load();}     // load
//                KeyCode.N -> { createBoard("newboard", "") } // new board
//                KeyCode.P -> { topMenu.exportPDF(stage) } // print png/pdf
//                KeyCode.E -> { topMenu.exportPNG(stage) }
//
//            }
//        }
//    }
    private fun createShape(shape: String) {
        if (shape === "r") {
            createRectangle()
        } else {
            createCircle()
        }
    }


}


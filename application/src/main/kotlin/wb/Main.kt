package wb

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.application.Application
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
import wb.frontend.*
import wb.helper.objectMapper
import wb.helper.saveWindow
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
var autoSyncTimeStamp = -1L
val rootcanvas = Pane()
val shapeTools = ShapeTools(rootcanvas)
val textTools = TextTools(rootcanvas)
val pathTools = PathTools(rootcanvas)


class Main : Application() {
    private var backgroundFill = BackgroundFill(Color.WHITE, null, null)
    private var background = Background(backgroundFill)

    override fun start(stage: Stage) {
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
        root.center = rootcanvas
        root.top = TopMenu(stage)
        root.left = ToolMenu(::setCursorType, pathTools.getPenTools())
        rootcanvas.background = background
        var loginMenu = LoginMenu(root, stage, sceneWidth, sceneHeight)
        pathTools.setScale(stage.scene)
        shapeTools.setScale(stage.scene)
        textTools.setScale(stage.scene)
        saveWindow(stage)
    }
}


package wb

import javafx.application.Application
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.stage.Stage
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import wb.frontend.LoginPage
import wb.frontend.ToolMenu
import wb.frontend.Tools.PathTools
import wb.frontend.Tools.ShapeTools
import wb.frontend.Tools.TextTools
import wb.frontend.Tools.setCursorType
import wb.frontend.TopMenu
import wb.helper.saveWindow
import java.io.File

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
val shapeTools = ShapeTools()
val textTools = TextTools()
val pathTools = PathTools()
val toolMenu = ToolMenu(::setCursorType, pathTools.getPenTools())

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
        val menuBar = TopMenu(stage)
        menuBar.styleClass.add("menuBar")
        root.top = menuBar

        toolMenu.styleClass.add("tool-menu")
        root.left = toolMenu
        rootcanvas.background = background
        LoginPage(root, stage, sceneWidth, sceneHeight)
        root.styleClass.add("border-pane")

        // Load the CSS file
        val css = Main::class.java.getResource("css/light.css")?.toExternalForm()
        stage.scene.stylesheets.add(css)

        pathTools.setScale(stage.scene)
        shapeTools.setScale(stage.scene)
        textTools.setScale(stage.scene)
        saveWindow(stage)
    }
}


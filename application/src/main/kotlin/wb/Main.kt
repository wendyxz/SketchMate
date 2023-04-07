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
fun setCursorType(ctype: CursorType) {
    cursor = ctype
    when (cursor) {
        CursorType.cursor -> pathTools.cancelPath()
        CursorType.textbox -> textTools.createTextBox()
        CursorType.pen -> pathTools.initPath()
        CursorType.eraser -> {
            pathTools.initPath()
        }
    }
}

// Serializer/Deserializer
val objectMapper = jacksonObjectMapper().registerModule(
    SimpleModule()
        .addSerializer(Rectangle::class.java, RectangleSerializer())
        .addDeserializer(Rectangle::class.java, RectangleDeserializer())
        .addSerializer(Circle::class.java, CircleSerializer())
        .addDeserializer(Circle::class.java, CircleDeserializer())
        .addSerializer(Path::class.java, PathSerializer())
        .addDeserializer(Path::class.java, PathDeserializer())
        .addSerializer(VBox::class.java, TextSerializer())
        .addDeserializer(VBox::class.java, TextDeserializer())
)

fun save() {
    // we need to write smth like:
    // val data = serializeCanvas(rootcanvas)
    val elements = mutableListOf<String>()
    for (element in rootcanvas.children) {
//        println(element)
        when (element) {
            is Rectangle -> elements.add(
                Json.encodeToString(
                    TypeWrapper("Rectangle", objectMapper.writeValueAsString(element))
                )
            )

            is Circle -> elements.add(
                Json.encodeToString(
                    TypeWrapper("Circle", objectMapper.writeValueAsString(element))
                )
            )

            is Path -> elements.add(
                Json.encodeToString(
                    TypeWrapper("Path", objectMapper.writeValueAsString(element))
                )
            )

            is VBox ->
                elements.add(
                    Json.encodeToString(
                        TypeWrapper("VBox", objectMapper.writeValueAsString(element))
                    )
                )

            else -> print("not defined")
        }
    }
    val currentTime = System.currentTimeMillis()
    autoSyncTimeStamp = currentTime

    val timestampedFile = TimeSerializer(currentTime, elements)

    if (wb.backend.boardname == "") {
        var filename = "${wb.backend.username}_${wb.backend.boardname}_data.json"
        val file = File(filename)
        val writer = BufferedWriter(FileWriter(file))
//        writer.write(objectMapper.writeValueAsString(elements))
        writer.write(Json.encodeToString(timestampedFile))
        writer.close()
    } else {
        try {
            val escapedJsonString = Json.encodeToString(timestampedFile).replace("\\", "")
            val escapedJson = Json.encodeToString(escapedJsonString).replace("\"\"", "\"\\\"")

            val str = wb.backend.updateBoard(wb.backend.boardname, escapedJson)
            when (str) {
                "Success" -> {
                    showWarnDialog("Success!", "Successful Save to ${wb.backend.boardname}!")
                }
                else -> {
                    // this should be not finding such user case
                    showWarnDialog("Board not found!", "Please check and try again!")
                }
            }

        } catch (e: Exception) {
            e.stackTrace.forEach { println(it) }
            showWarnDialog("Error", e.toString())
        }
    }

    print("done")
}

fun load() {
    var data = ""
    if (wb.backend.boardname == "") {
        var filename = "${wb.backend.username}_${wb.backend.boardname}_data.json"
        val file = File(filename)
        val reader = BufferedReader(FileReader(file))
        data = reader.readText()
        reader.close()
    } else {
        try {
            // todo: add some output to this
            data = wb.backend.getSingleBoard()
            data = wb.helper.processJsonString(data)
            println("!!!!!!!!!!!!!!!!!")
            println("!!!!!!!!!!!!!!!!!")
            println(data)
            when (data) {
                "" -> {
                    // this should be not finding such user case
                    showWarnDialog("Board not found!", "Please check and try again!")
                    return
                }
            }
        } catch (e: Exception) {
            e.stackTrace.forEach { println(it) }
            showWarnDialog("Error", e.toString())
            return
        }
    }
    // print("reader closed")
    // we need smth like:
    // unserializeCanvas(data, rootcanvas)
    // to replace the bottom section
    val timeFile = Json.decodeFromString<TimeSerializer>(data)

    val fileTime = timeFile.time
    if (fileTime == autoSyncTimeStamp) return

    autoSyncTimeStamp = fileTime

    // timeFile.time to see the timestamp
    rootcanvas.children.removeAll(rootcanvas.children)
    var elements = timeFile.whiteboard
    if (elements != null) {
        for (wrapper in elements) {
            var element = Json.decodeFromString<TypeWrapper>(wrapper)
            when (element.type) {
                "Rectangle" -> {
                    val r = objectMapper.readValue(element.string, Rectangle::class.java)
                    addSubmenu(r)
                    DragResize.makeResizable(r, rootcanvas);
                    rootcanvas.children.add(r)
                }

                "Circle" -> {
                    val c = objectMapper.readValue(element.string, Circle::class.java)
                    addSubmenu(c)
                    DragResize.makeResizable(c, rootcanvas);
                    rootcanvas.children.add(c)
                }

                "Path" -> rootcanvas.children.add(objectMapper.readValue(element.string, Path::class.java))
                "VBox" -> {
                    rootcanvas.children.add(objectMapper.readValue(element.string, VBox::class.java))
                }

                else -> error("LOAD ERROR !!! ")
            }
        }
    }


    // println("decoded")
}


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
        root.top = TopMenu(::setBackgroundColour, ::save, ::load, stage)
        root.left = ToolMenu(::setCursorType, pathTools.getPenTools(), ::createShape)
        rootcanvas.background = background
        var loginMenu = LoginMenu(root, stage, sceneWidth, sceneHeight)
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

    private fun createShape(shape: String) {
        if (shape === "r") {
            shapeTools.createRectangle()
        } else {
            shapeTools.createCircle()
        }
    }


    private fun setBackgroundColour(color: Color) {
        rootcanvas.background = Background(BackgroundFill(color, null, null))
    }
}


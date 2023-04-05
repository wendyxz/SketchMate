package wb

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Stage
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import wb.frontend.*
import java.io.*
import java.security.Timestamp

@Serializable
class TypeWrapper(val type: String, val string: String)

@Serializable
class TimeSerializer(val time: Long, val whiteboard: List<String>)

@Serializable
data class Window(val width: Double, val height: Double, val x: Double, val y: Double)

class Main : Application() {
    private val rootcanvas = Pane()
    private var root = BorderPane()
    private var backgroundFill = BackgroundFill(Color.WHITE, null, null)
    private var background = Background(backgroundFill)
    private var shapeTools = ShapeTools(rootcanvas)
    private var textTools = TextTools(rootcanvas)
    private var pathTools = PathTools(rootcanvas)
    // Serializer/Deserializer
    private val objectMapper = jacksonObjectMapper().registerModule(SimpleModule()
        .addSerializer(Rectangle::class.java, RectangleSerializer())
        .addDeserializer(Rectangle::class.java, RectangleDeserializer())
        .addSerializer(Circle::class.java, CircleSerializer())
        .addDeserializer(Circle::class.java, CircleDeserializer())
        .addSerializer(Path::class.java, PathSerializer())
        .addDeserializer(Path::class.java, PathDeserializer())
        .addSerializer(VBox::class.java, TextSerializer())
        .addDeserializer(VBox::class.java, TextDeserializer()))
    
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
        if (shape==="r") {
            shapeTools.createRectangle()
        } else {
            shapeTools.createCircle()
        }

    }

    private fun setCursorType(ctype: CursorType) {
        cursorType = ctype
        when (cursorType) {
            CursorType.cursor -> pathTools.cancelPath()
            CursorType.textbox -> textTools.createTextBox()
            CursorType.pen -> pathTools.initPath()
            CursorType.rectangle -> {
                pathTools.cancelPath()
                shapeTools.createRectangle()
            }

            CursorType.circle -> {
                pathTools.cancelPath()
                shapeTools.createCircle()
            }

            CursorType.eraser -> {
                pathTools.initPath()
                //pathTools.cancelPath()
                // shapeTools.createRectangle()
            };
        }
    }

    private fun setBackgroundColour(color: Color) {
        rootcanvas.background = Background(BackgroundFill(color, null, null))
    }

    private fun save(filename: String) {
        // we need to write smth like:
        // val data = serializeCanvas(rootcanvas)
        val elements = mutableListOf<String>()
        for (element in rootcanvas.children) {
            println(element)
            when (element) {
                is Rectangle -> elements.add(Json.encodeToString(
                    TypeWrapper("Rectangle", objectMapper.writeValueAsString(element))))
                is Circle -> elements.add(Json.encodeToString(
                        TypeWrapper("Circle", objectMapper.writeValueAsString(element))))
                is Path -> elements.add(Json.encodeToString(
                    TypeWrapper("Path", objectMapper.writeValueAsString(element))))
                is VBox ->
                    elements.add(Json.encodeToString(
                    TypeWrapper("VBox", objectMapper.writeValueAsString(element))))
                else -> print("not defined")
            }
        }
        val timestampedFile = TimeSerializer(System.currentTimeMillis(), elements)
        val file = File(filename)
        val writer = BufferedWriter(FileWriter(file))
//        writer.write(objectMapper.writeValueAsString(elements))
        writer.write(Json.encodeToString(timestampedFile))
        writer.close()
        print("done")
    }
    private fun load(filename: String) {
        val file = File(filename)
        val reader = BufferedReader(FileReader(file))
        val data = reader.readText()
        reader.close()
        print("reader closed")
        // we need smth like:
        // unserializeCanvas(data, rootcanvas)
        // to replace the bottom section
        val timeFile = Json.decodeFromString<TimeSerializer>(data)
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
                    else -> print("otherwise")
                }
            }
        }


        println("decoded")
    }
}


package wb

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.application.Application
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import kotlinx.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import wb.frontend.*
import java.io.*

@Serializable
class TypeWrapper(val type: String, val string: String)

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
        .addDeserializer(Path::class.java, PathDeserializer()))
    
    override fun start(stage: Stage) {
        stage.title = "WhiteBoard"
        stage.minWidth = 480.0
        stage.minHeight = 320.0

        pathTools = PathTools(rootcanvas)
        root.top = TopMenu(::setBackgroundColour, ::save, ::load)
        root.left = ToolMenu(::setCursorType, pathTools.getPenTools())
        root.center = rootcanvas
        rootcanvas.background = background
        stage.scene = Scene(root, 800.0, 600.0)
        pathTools.setScale(stage.scene)
        shapeTools = ShapeTools(rootcanvas)
        shapeTools.setScale(stage.scene)
        textTools.setScale(stage.scene)

        stage.show()
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
            when (element) {
                is Rectangle -> elements.add(Json.encodeToString(
                    TypeWrapper("Rectangle", objectMapper.writeValueAsString(element))))
                is Circle -> elements.add(Json.encodeToString(
                        TypeWrapper("Circle", objectMapper.writeValueAsString(element))))
                is Path -> elements.add(Json.encodeToString(
                    TypeWrapper("Path", objectMapper.writeValueAsString(element))))
                else -> print("not defined")
            }
        }

        val file = File(filename)
        val writer = BufferedWriter(FileWriter(file))
        writer.write(objectMapper.writeValueAsString(elements))
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

        rootcanvas.children.removeAll(rootcanvas.children)
        var elements = Json.decodeFromString<List<String>?>(data)
        if (elements != null) {
            for (wrapper in elements) {
                var element = Json.decodeFromString<TypeWrapper>(wrapper)
                when (element.type) {
                    "Rectangle" -> rootcanvas.children.add(objectMapper.readValue(element.string, Rectangle::class.java))
                    "Circle" -> rootcanvas.children.add(objectMapper.readValue(element.string, Circle::class.java))
                    "Path" -> root.children.add(objectMapper.readValue(element.string, Path::class.java))
                    else -> print("otherwise")
                }
            }
        }


        println("decoded")
    }
}


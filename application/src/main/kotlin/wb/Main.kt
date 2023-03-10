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
import javafx.scene.shape.*
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
        .addDeserializer(Rectangle::class.java, RectangleDeserializer()))
    
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

    data class Point(val x: Double, val y: Double)

    private fun distance(A: Point, B: Point): Double{
        return (A.x-B.x)*(A.x-B.x)+(A.y-B.y)*(A.y-B.y)
    }

    private fun touchPath(point: Point, path: Path): Boolean{
        for(i in 1 until path.elements.size){
            val currentLine = path.elements[i]
            val previousLine = path.elements[i-1]

            val currentX: Double = when(currentLine){
                is LineTo -> currentLine.x
                is MoveTo -> currentLine.x
                else -> return false
            }
            val currentY: Double = when(currentLine){
                is LineTo -> currentLine.y
                is MoveTo -> currentLine.y
                else -> return false
            }

            val previousX: Double = when(previousLine){
                is LineTo -> previousLine.x
                is MoveTo -> previousLine.x
                else -> return false
            }
            val previousY: Double = when(previousLine){
                is LineTo -> previousLine.y
                is MoveTo -> previousLine.y
                else -> return false
            }

            val currentPoint = Point(currentX, currentY)
            val previousPoint = Point(previousX, previousY)

            if(distance(previousPoint, point) + distance(currentPoint, point) == distance(previousPoint, currentPoint))
                return true
        }
        return false
    }

    private fun touchShape(point: Point, shape: Shape): Boolean{
        return false
    }

    private fun touch(point: Point, item: Node) = when {
        item is Path -> touchPath(point, item)
        item is Shape -> touchShape(point, item)
        else -> false
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
                val currentPoint = Point(5.0, 5.0)
                rootcanvas.children.add(Circle(currentPoint.x, currentPoint.y, 0.5, Color.BLACK))

                val objectsToRemove: MutableList<Node> = mutableListOf()
                for(eachObject in rootcanvas.children){
                    if(touch(currentPoint, eachObject))
                        objectsToRemove.add(eachObject)
                }

                for(removeObject in objectsToRemove)
                    rootcanvas.children.remove(removeObject)
            }
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
            if (element is Rectangle)  {
                val str = TypeWrapper("Rectangle", objectMapper.writeValueAsString(element))
                elements.add(Json.encodeToString(str))
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

        var elements = Json.decodeFromString<List<String>?>(data)
        if (elements != null) {
            for (wrapper in elements) {
                var element = Json.decodeFromString<TypeWrapper>(wrapper)
                when (element.type) {
                    "Rectangle" -> rootcanvas.children.add(objectMapper.readValue(element.string, Rectangle::class.java))
                    else -> print("otherwise")
                }
            }
        }


        println("decoded")
    }
}


package wb.frontend

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import wb.*
import java.io.*

fun setBackgroundColour(color: Color) {
    rootcanvas.background = Background(BackgroundFill(color, null, null))
}
fun setCursorType(ctype: CursorType) {
    cursor = ctype
    when (cursor) {
        CursorType.cursor -> pathTools.cancelPath()
        CursorType.textbox -> textTools.createTextBox()
        CursorType.pen -> pathTools.initPath()
        CursorType.eraser -> {
            pathTools.initPath()
        }
        CursorType.circle -> {
            createCircle();
            setCursorType(CursorType.cursor)
        }
        CursorType.rectangle -> {
            createRectangle()
            setCursorType(CursorType.cursor)
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
        if (!file.exists()) {
            file.createNewFile()
        }
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
                    showWarnDialog("2Board not found!", "Please check and try again!")
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
        if (!file.exists()) {
            return
        }
        val reader = BufferedReader(FileReader(file))
        data = reader.readText()
        reader.close()
    } else {
        try {
            // todo: add some output to this
            data = wb.backend.getSingleBoard()
            data = wb.helper.processJsonString(data)
            data = wb.helper.removeDoubleQuotes(data)
            when (data) {
                "" -> {
                    // this should be not finding such user case
                    showWarnDialog("3Board not found!", "Please check and try again!")
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

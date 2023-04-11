package wb.helper

import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.codebot.shared.processJsonString
import net.codebot.shared.removeDoubleQuotes
import wb.TimeSerializer
import wb.TypeWrapper
import wb.autoSyncTimeStamp
import wb.frontend.DragResize
import wb.frontend.Tools.addSubmenu
import wb.frontend.objectMapper
import wb.rootcanvas
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


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
            data = processJsonString(data)
            data = removeDoubleQuotes(data)
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
                    DragResize.makeResizable(r);
                    rootcanvas.children.add(r)
                }

                "Circle" -> {
                    val c = objectMapper.readValue(element.string, Circle::class.java)
                    addSubmenu(c)
                    DragResize.makeResizable(c);
                    rootcanvas.children.add(c)
                }

                "Path" -> {
                    rootcanvas.children.add(objectMapper.readValue(element.string, Path::class.java))
                }

                "VBox" -> {
                    rootcanvas.children.add(objectMapper.readValue(element.string, VBox::class.java))
                }

                "Triangle" -> {
                    val t = objectMapper.readValue(element.string, Polygon::class.java)
                    DragResize.makeResizable(t)
                    rootcanvas.children.add(t)
                }

                else -> error("LOAD ERROR !!! ")
            }
        }
    }
}
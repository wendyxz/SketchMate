package wb.helper

import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.stage.Stage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import wb.*
import wb.frontend.showWarnDialog
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

fun saveWindow(stage: Stage) {
    stage.setOnCloseRequest {
        val file = File("window.json")
        val window = Window(stage.scene.width, stage.scene.height, stage.x, stage.y)
        val json = Json.encodeToString(window)
        file.writeText(json)
    }
}

fun save() {
    val elements = mutableListOf<String>()
    for (element in rootcanvas.children) {
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

        writer.write(Json.encodeToString(timestampedFile))
        writer.close()
    } else {
        try {
            val escapedJsonString = Json.encodeToString(timestampedFile).replace("\\", "")
            val escapedJson = Json.encodeToString(escapedJsonString).replace("\"\"", "\"\\\"")

            val str = wb.backend.updateBoard(wb.backend.boardname, escapedJson)
            when (str) {
                "Success" -> {
//                    showWarnDialog("Success!", "Successful Save to ${wb.backend.boardname}!")
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
}


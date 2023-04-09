package wb.helper

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.scene.layout.VBox
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import wb.frontend.*

fun processJsonString(jsonString: String): String {
    var depth = -1
    var result = ""
    for (char in jsonString) {
        when (char) {
            '{' -> {
                depth++
                result += char
            }

            '}' -> {
                depth--
                result += char
            }

            '"' -> {
                var tmp = ""
                if (depth == 1) {
                    tmp = "\\"
                } else if (depth > 1) {
                    tmp = "\\\\\\"
                }
                val quoteReplacement = tmp + char
                result += quoteReplacement
            }

            else -> {
                result += char
            }
        }
    }
    return result
}

fun removeDoubleQuotes(str: String): String {
    if (str.startsWith("\"") && str.endsWith("\"")) {
        return str.substring(1, str.length - 1)
    }
    return str
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
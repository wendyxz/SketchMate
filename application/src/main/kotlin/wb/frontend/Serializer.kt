package wb.frontend

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import javafx.geometry.Point2D
import javafx.scene.paint.Color
import javafx.scene.shape.*


class RectangleSerializer : JsonSerializer<Rectangle>() {
    override fun serialize(value: Rectangle?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeNumberField("x", value?.x ?: 0.0)
        gen?.writeNumberField("y", value?.y ?: 0.0)
        gen?.writeNumberField("layoutX", value?.layoutX ?: 0.0)
        gen?.writeNumberField("layoutY", value?.layoutY ?: 0.0)
        gen?.writeNumberField("translateX", value?.translateX ?: 0.0)
        gen?.writeNumberField("translateY", value?.translateY ?: 0.0)
        gen?.writeNumberField("width", value?.width ?: 0.0)
        gen?.writeNumberField("height", value?.height ?: 0.0)
        gen?.writeFieldName("fill")
        gen?.writeStartObject()
        val color: Color? = value?.fill as? Color
        if (color != null) {
            gen?.writeBooleanField("opaque", color.isOpaque)
            gen?.writeNumberField("hue", color.hue)
            gen?.writeNumberField("saturation", color.saturation)
            gen?.writeNumberField("brightness", color.brightness)
        }
        gen?.writeEndObject()
        gen?.writeEndObject()
    }
}

class RectangleDeserializer : JsonDeserializer<Rectangle>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Rectangle? {
        p?.let {
            val codec = it.codec
            val node: JsonNode = codec.readTree(p)

            val x = node.get("x").asDouble()
            val y = node.get("y").asDouble()
            val layoutX = node.get("layoutX").asDouble()
            val layoutY = node.get("layoutY").asDouble()
            val translateX = node.get("translateX").asDouble()
            val translateY = node.get("translateY").asDouble()
            val width = node.get("width").asDouble()
            val height = node.get("height").asDouble()

            val fillNode = node.get("fill")
            val opaque = fillNode.get("opaque").asBoolean()
            val hue = fillNode.get("hue").asDouble()
            val saturation = fillNode.get("saturation").asDouble()
            val brightness = fillNode.get("brightness").asDouble()

            val fill = Color.hsb(hue, saturation, brightness, if (opaque) 1.0 else 0.0)
            var rectangle = Rectangle(x, y, width, height).apply {
                this.fill = fill
                this.layoutX = layoutX
                this.layoutY = layoutY
                this.translateX = translateX
                this.translateY = translateY
            }
            makeDraggable(rectangle)
            return rectangle
        }

        return null
    }
}


class CircleSerializer : JsonSerializer<Circle>() {
    override fun serialize(value: Circle?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeNumberField("centerX", value?.centerX ?: 0.0)
        gen?.writeNumberField("centerY", value?.centerY ?: 0.0)
        gen?.writeNumberField("layoutX", value?.layoutX ?: 0.0)
        gen?.writeNumberField("layoutY", value?.layoutY ?: 0.0)
        gen?.writeNumberField("translateX", value?.translateX ?: 0.0)
        gen?.writeNumberField("translateY", value?.translateY ?: 0.0)
        gen?.writeNumberField("radius", value?.radius ?: 0.0)
        gen?.writeFieldName("fill")
        gen?.writeStartObject()
        val color: Color? = value?.fill as? Color
        if (color != null) {
            gen?.writeBooleanField("opaque", color.isOpaque)
            gen?.writeNumberField("hue", color.hue)
            gen?.writeNumberField("saturation", color.saturation)
            gen?.writeNumberField("brightness", color.brightness)
        }
        gen?.writeEndObject()
        gen?.writeEndObject()
    }
}

class CircleDeserializer : JsonDeserializer<Circle>() {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): Circle? {
        p?.let {
            val codec = it.codec
            val node: JsonNode = codec.readTree(p)

            val centerX = node.get("centerX").asDouble()
            val centerY = node.get("centerY").asDouble()
            val layoutX = node.get("layoutX").asDouble()
            val layoutY = node.get("layoutY").asDouble()
            val translateX = node.get("translateX").asDouble()
            val translateY = node.get("translateY").asDouble()
            val radius = node.get("radius").asDouble()

            val fillNode = node.get("fill")
            val opaque = fillNode.get("opaque").asBoolean()
            val hue = fillNode.get("hue").asDouble()
            val saturation = fillNode.get("saturation").asDouble()
            val brightness = fillNode.get("brightness").asDouble()

            val fill = Color.hsb(hue, saturation, brightness, if (opaque) 1.0 else 0.0)
            var shape = Circle(centerX, centerY, radius).apply {
                this.fill = fill
                this.layoutX = layoutX
                this.layoutY = layoutY
                this.translateX = translateX
                this.translateY = translateY
            }
            makeDraggable(shape)
            return shape
        }

        return null
    }
}

class PathSerializer : JsonSerializer<Path>() {
    override fun serialize(
        value: Path?,
        gen: JsonGenerator?,
        serializers: SerializerProvider?
    ) {
        gen?.writeStartObject()
        //gen?.writeStringField("color", value?.stroke.toString())
        //gen?.writeNumberField("width", value?.strokeWidth ?: 0.0)
        gen?.writeFieldName("elements")
        gen?.writeStartArray()

        value?.elements?.forEach { element ->
            gen?.writeStartObject()
            gen?.writeFieldName("type")
            gen?.writeString(element::class.java.simpleName)
            when (element) {
                is MoveTo -> {
                    gen?.writeNumberField("x", element.x)
                    gen?.writeNumberField("y", element.y)
                }
                is LineTo -> {
                    gen?.writeNumberField("x", element.x)
                    gen?.writeNumberField("y", element.y)
                }
            }
            gen?.writeEndObject()
        }
        gen?.writeEndArray()
        gen?.writeEndObject()
    }
}


class PathDeserializer : JsonDeserializer<Path>() {
    override fun deserialize(parser: JsonParser?, ctxt: DeserializationContext?): Path {
        val path = Path()
        path.strokeWidth = 2.0
        val root = parser?.codec?.readTree<JsonNode>(parser)
        //val color : Color = Color.web(root?.get("color")?.toString() ?: "black")
        //val width = root?.get("width").toString().toDouble()
        val elements = root?.get("elements")
        elements?.forEach { elementNode ->
            when (val type = elementNode.get("type").asText()) {
                "MoveTo" -> {
                    val x = elementNode.get("x").asDouble()
                    val y = elementNode.get("y").asDouble()
                    path.elements.add(MoveTo(x, y))
                }

                "LineTo" -> {
                    val x = elementNode.get("x").asDouble()
                    val y = elementNode.get("y").asDouble()
                    path.elements.add(LineTo(x, y))
                }
            }
        }
        //path.stroke = color

        println(path)
        return path
    }
}

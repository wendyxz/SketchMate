package wb.frontend

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle

class RectangleSerializer : JsonSerializer<Rectangle>() {

    override fun serialize(value: Rectangle?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeStartObject()
        gen?.writeNumberField("x", value?.x ?: 0.0)
        gen?.writeNumberField("y", value?.y ?: 0.0)
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
            val width = node.get("width").asDouble()
            val height = node.get("height").asDouble()

            val fillNode = node.get("fill")
            val opaque = fillNode.get("opaque").asBoolean()
            val hue = fillNode.get("hue").asDouble()
            val saturation = fillNode.get("saturation").asDouble()
            val brightness = fillNode.get("brightness").asDouble()

            val fill = Color.hsb(hue, saturation, brightness, if (opaque) 1.0 else 0.0)

            return Rectangle(x, y, width, height).apply {
                this.fill = fill
            }
        }

        return null
    }
}






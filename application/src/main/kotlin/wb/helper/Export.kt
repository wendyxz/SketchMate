package wb.helper

import com.itextpdf.text.Document
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfWriter
import javafx.embed.swing.SwingFXUtils
import javafx.stage.Stage
import java.io.File
import java.io.FileOutputStream
import javax.imageio.ImageIO


fun exportPNG(stage: Stage) {
    val image = stage.scene.snapshot(null)
    val bufferedImage = SwingFXUtils.fromFXImage(image, null)
    val file = File("whiteboard.png")
    ImageIO.write(bufferedImage, "png", file)
}

fun exportPDF(stage: Stage) {
    val file = File("whiteboard.pdf")
    val pageSize = Rectangle(stage.scene.width.toFloat(), stage.scene.height.toFloat())
    val document = Document(pageSize)
    val writer = PdfWriter.getInstance(document, FileOutputStream(file))
    document.open()
    document.newPage()
    val cb = writer.directContent
    val template = cb.createTemplate(stage.scene.width.toFloat(), stage.scene.height.toFloat())
    val graphics =
        template.createGraphics(stage.scene.width.toDouble().toFloat(), stage.scene.height.toDouble().toFloat())
    graphics.background = java.awt.Color.WHITE
    stage.scene.root.snapshot(null, null)?.let {
        val image = SwingFXUtils.fromFXImage(it, null)
        graphics.drawImage(image, 0, 0, null)
    }
    graphics.dispose()
    cb.addTemplate(template, 0f, 0f)
    document.close()
}
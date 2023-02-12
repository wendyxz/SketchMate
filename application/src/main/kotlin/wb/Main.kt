package wb

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.layout.StackPane
import javafx.stage.Stage

import wb.frontend.*
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane

enum class CursorType {
    cursor,
    textbox,
    pen,
    rectangle,
    circle,
    eraser
}
class Main : Application() {
    private var ct = CursorType.cursor
    private val canvas = ResizableCanvas()
    private val graphicsContext = canvas.graphicsContext2D
    override fun start(stage: Stage) {
        stage.title = "WhiteBoard"
        stage.minWidth = 320.0
        stage.minHeight = 240.0

        var root = BorderPane()
        val stack = StackPane()
        root.top = TopMenu()
        root.left = ToolMenu(::setCursorType)

        root.center = stack
        stack.children.add(canvas)
        stage.scene = Scene(root, 800.0, 600.0)
        canvas.resize(stage.width, stage.height)

        stage.show()
    }

    private fun setCursorType(ctype: CursorType) {
        ct = ctype
        when (ct) {
            CursorType.cursor -> println("cursor")
            CursorType.textbox -> println("text")
            CursorType.pen -> initDraw(graphicsContext, canvas)
            CursorType.rectangle -> println("rectangle")
            CursorType.circle -> println("circle")
            CursorType.eraser -> println("eraser")
        }
    }
    private fun initDraw(gc: GraphicsContext, cv: ResizableCanvas) {
        val canvasWidth = gc.canvas.width
        val canvasHeight = gc.canvas.height
        gc.fill = Color.LIGHTGRAY
        gc.stroke = Color.BLACK
        gc.lineWidth = 5.0
        gc.fill()
        gc.strokeRect(
            0.0,  //x of the upper left corner
            0.0,  //y of the upper left corner
            canvasWidth,  //width of the rectangle
            canvasHeight
        ) //height of the rectangle
        gc.fill = Color.RED
        gc.stroke = Color.BLUE
        gc.lineWidth = 1.0

        cv.addEventHandler(
            MouseEvent.MOUSE_PRESSED
        ) { event ->
            gc.beginPath()
            gc.moveTo(event.x, event.y)
            gc.stroke()
        }
        cv.addEventHandler(
            MouseEvent.MOUSE_DRAGGED
        ) { event ->
            gc.lineTo(event.x, event.y)
            gc.stroke()
        }
        cv.addEventHandler(
            MouseEvent.MOUSE_RELEASED
        ) { }
    }
}

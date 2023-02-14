package wb

import javafx.application.Application
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.transform.Scale
import javafx.stage.Stage
import wb.frontend.*


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
    private val rootcanvas = Pane()
    private val scale = Scale()
    private var root = BorderPane()

    override fun start(stage: Stage) {
        stage.title = "WhiteBoard"
        stage.minWidth = 480.0
        stage.minHeight = 320.0
        scale.pivotX = 0.0
        scale.pivotY = 0.0

        root.top = TopMenu()
        root.left = ToolMenu(::setCursorType)
        root.center = rootcanvas
        stage.scene = Scene(root, 800.0, 600.0)
        scale.xProperty().bind(stage.scene.widthProperty())
        scale.yProperty().bind(stage.scene.heightProperty())

        stage.show()
    }

    private fun setCursorType(ctype: CursorType) {
        ct = ctype
        when (ct) {
            CursorType.cursor -> println("cursor")
            CursorType.textbox -> println("text")
            CursorType.pen -> initDraw(rootcanvas)
            CursorType.rectangle -> println("rectangle")
            CursorType.circle -> println("circle")
            CursorType.eraser -> println("eraser")
        }
    }
    private fun initDraw(rc: Pane) {
        var path = Path()
        rc.addEventHandler(
            MouseEvent.MOUSE_PRESSED
        ) { event ->
            path = Path()
            var moveTo = MoveTo()
            path.stroke = Color.RED
            path.strokeWidth = 2.0
            moveTo.x = event.x
            moveTo.y = event.y
            path.elements.add(moveTo)
        }
        rc.addEventHandler(
            MouseEvent.MOUSE_DRAGGED
        ) { event ->
            val lineTo = LineTo()
            lineTo.x = event.x
            lineTo.y = event.y
            path.elements.add(lineTo)
        }
        rc.addEventHandler(
            MouseEvent.MOUSE_RELEASED
        ) {
            path.transforms.add(Scale(1.0/scale.x, 1.0/scale.y))
            path.transforms.add(scale)
            rc.children.add(path)
        }
    }
}

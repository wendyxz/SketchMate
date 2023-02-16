package wb.frontend

import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.transform.Scale
import kotlin.math.max

class PathTools(Canvas: Pane) {

    private var path = Path()
    private var penTools = PenTools()
    private var rootcanvas = Canvas
    private val scale = Scale()

    init {
        scale.pivotX = 0.0
        scale.pivotY = 0.0
    }

    fun getPenTools(): PenTools {
        return penTools
    }

    fun setScale(scene: Scene) {
        scale.xProperty().bind(scene.widthProperty())
        scale.yProperty().bind(scene.heightProperty())
    }

    fun initPath() {

        rootcanvas.addEventHandler(
            MouseEvent.MOUSE_PRESSED, startPath
        )
        rootcanvas.addEventHandler(
            MouseEvent.MOUSE_DRAGGED, pathProcess
        )
        rootcanvas.addEventHandler(
            MouseEvent.MOUSE_RELEASED, pathComplete
        )
    }

    fun cancelPath() {
        rootcanvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, startPath)
        rootcanvas.removeEventHandler(MouseEvent.MOUSE_DRAGGED, pathProcess)
        rootcanvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, pathComplete)
    }

    private val startPath = EventHandler<MouseEvent> { event ->
        path = Path()
        var moveTo = MoveTo()
        path.stroke = penTools.strokeColor
        path.strokeWidth = penTools.strokeWidth
        if (penTools.lineStyle == "dashed") {
            path.strokeDashArray.clear()
            path.strokeDashArray.addAll(20.0, 20.0)
        } else if (penTools.lineStyle == "dotted") {
            path.strokeDashArray.clear()
            path.strokeDashArray.addAll(5.0, 15.0)
        } else {
            path.strokeDashArray.clear()
        }
        moveTo.x = event.x
        moveTo.y = event.y
        path.elements.add(moveTo)
        rootcanvas.children.add(path)
    }

    private val pathProcess = EventHandler<MouseEvent> { event ->
        val lineTo = LineTo()
        lineTo.x = max(0.0,event.x)
        lineTo.y = max(0.0,event.y)
        path.elements.add(lineTo)
    }

    private val pathComplete = EventHandler<MouseEvent> {
        path.transforms.add(Scale(1.0 / scale.x, 1.0 / scale.y))
        path.transforms.add(scale)
    }

}
package wb

import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.LineTo
import javafx.scene.shape.MoveTo
import javafx.scene.shape.Path
import javafx.scene.transform.Scale
import javafx.stage.Stage
import wb.frontend.*
import kotlin.math.max



class Main : Application() {
    private val rootcanvas = Pane()
//    private val scale = Scale()
    private var root = BorderPane()
//    private var path = Path()
    private var backgroundFill = BackgroundFill(Color.WHITE, null, null)
    private var background = Background(backgroundFill)
    private var shapeTools = ShapeTools(rootcanvas)
//    private var penTools = PenTools()
    private var pathTools = PathTools(rootcanvas)

    override fun start(stage: Stage) {
        stage.title = "WhiteBoard"
        stage.minWidth = 480.0
        stage.minHeight = 320.0
//        scale.pivotX = 0.0
//        scale.pivotY = 0.0

        pathTools = PathTools(rootcanvas)
        root.top = TopMenu(::setBackgroundColour)
        root.left = ToolMenu(::setCursorType, pathTools.getPenTools())
        root.center = rootcanvas
        rootcanvas.background = background
        stage.scene = Scene(root, 800.0, 600.0)
        pathTools.setScale(stage.scene)


//        scale.xProperty().bind(stage.scene.widthProperty())
//        scale.yProperty().bind(stage.scene.heightProperty())
        shapeTools = ShapeTools(rootcanvas)
        stage.show()
    }

    private fun setCursorType(ctype: CursorType) {
        cursorType = ctype
        when (cursorType) {
            CursorType.cursor -> pathTools.cancelPath()
            CursorType.textbox -> println("text")
            CursorType.pen -> {
                pathTools.initPath()
            }
            CursorType.rectangle -> {
                pathTools.cancelPath()
                shapeTools.createRectangle()
            }
            CursorType.circle -> {
                pathTools.cancelPath()
                shapeTools.createCircle()
            };
        }
    }

//    private fun initPath(rc: Pane) {
//
//        rc.addEventHandler(
//            MouseEvent.MOUSE_PRESSED, startPath
//        )
//        rc.addEventHandler(
//            MouseEvent.MOUSE_DRAGGED, pathProcess
//        )
//        rc.addEventHandler(
//            MouseEvent.MOUSE_RELEASED, pathComplete
//        )
//    }
//
//    private fun cancelPath(rc: Pane) {
//        rc.removeEventHandler(MouseEvent.MOUSE_PRESSED, startPath)
//        rc.removeEventHandler(MouseEvent.MOUSE_DRAGGED, pathProcess)
//        rc.removeEventHandler(MouseEvent.MOUSE_RELEASED, pathComplete)
//    }

//    private val startPath = EventHandler<MouseEvent> { event ->
//        path = Path()
//        var moveTo = MoveTo()
//        path.stroke = penTools.strokeColor
//        path.strokeWidth = penTools.strokeWidth
//        if (penTools.lineStyle == "dashed") {
//            path.strokeDashArray.clear()
//            path.strokeDashArray.addAll(20.0, 20.0)
//        } else if (penTools.lineStyle == "dotted") {
//            path.strokeDashArray.clear()
//            path.strokeDashArray.addAll(5.0, 15.0)
//        } else {
//            path.strokeDashArray.clear()
//        }
//        moveTo.x = event.x
//        moveTo.y = event.y
//        path.elements.add(moveTo)
//        rootcanvas.children.add(path)
//    }
//
//    private val pathProcess = EventHandler<MouseEvent> { event ->
//        val lineTo = LineTo()
//        lineTo.x = max(0.0,event.x)
//        lineTo.y = max(0.0,event.y)
//        path.elements.add(lineTo)
//    }
//
//    private val pathComplete = EventHandler<MouseEvent> {
//        path.transforms.add(Scale(1.0 / scale.x, 1.0 / scale.y))
//        path.transforms.add(scale)
//    }


    private fun setBackgroundColour(color: Color) {
        println("HI")
        rootcanvas.background = Background(BackgroundFill(color, null, null))
    }
}


package wb.frontend

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.*
import javafx.scene.transform.Scale
import wb.helper.save

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

        if(penTools.eraser)
            path.strokeDashArray.clear()

        moveTo.x = max(penTools.strokeWidth * 0.5, event.x)
        moveTo.y = max(penTools.strokeWidth * 0.5, event.y)
        path.elements.add(moveTo)
        rootcanvas.children.add(path)
        DragResize.makeResizable(path, rootcanvas)
        // print(path)
    }

    private val pathProcess = EventHandler<MouseEvent> { event ->
        val lineTo = LineTo()
        lineTo.x = max(penTools.strokeWidth * 0.5, event.x)
        lineTo.y = max(penTools.strokeWidth * 0.5, event.y)
        path.elements.add(lineTo)
    }

    private val pathComplete = EventHandler<MouseEvent> {
        path.transforms.add(Scale(1.0 / scale.x, 1.0 / scale.y))
        path.transforms.add(scale)

        if(penTools.eraser) eraseFor()

        save()
    }

    private fun eraseFor() {
        val eraseList: MutableList<Node> = mutableListOf()
        for(eac in rootcanvas.children)
            if(shouldErase(eac)) eraseList.add(eac)

        for(eac in eraseList) rootcanvas.children.remove(eac)
    }

    private fun shouldErase(node: Node) = when(node) {
        is Path -> erasePath(node)
        is Shape -> eraseShape(node)
        else -> false
    }

    class Point(val x: Double, val y: Double)
    class Line(val first: Point, val second: Point)

    private fun findLineList(node: Path): List<Line>{
        val returnList: MutableList<Line> = mutableListOf()
        for(index in 1 until node.elements.size){
            val previous = node.elements[index-1]
            val current = node.elements[index]

            val previousX = if (previous is MoveTo) previous.x else (previous as LineTo).x
            val previousY = if (previous is MoveTo) previous.y else (previous as LineTo).y
            val currentX = if (current is MoveTo) current.x else (current as LineTo).x
            val currentY = if (current is MoveTo) current.y else (current as LineTo).y

            val previousPoint = Point(previousX, previousY)
            val currentPoint = Point(currentX, currentY)

            returnList.add(Line(previousPoint, currentPoint))
        }
        return returnList
    }

    private fun isCounterClockWise(A: Point, B: Point, C: Point): Boolean{
        return (C.y-A.y)*(B.x-A.x) > (B.y-A.y)*(C.x-A.x)
    }

    // DOES NOT HANDLE COLLINEAR CASE!!!
    private fun lineIntersect(first: Line, second: Line): Boolean{
        return (
                (isCounterClockWise(first.first, second.first, second.second) != isCounterClockWise(first.second, second.first, second.second))
                &&
                (isCounterClockWise(first.first, first.second, second.first) != isCounterClockWise(first.first, first.second, second.second))
            )
    }

    private fun erasePath(node: Path): Boolean{
        val nodeLine = findLineList(node)
        val pathLine = findLineList(path)

        for(eacNodeLine in nodeLine)
            for(eacPathLine in pathLine)
                if(lineIntersect(eacNodeLine, eacPathLine)) return true

        return false
    }

    private fun eraseShape(shape: Shape): Boolean{
//        println("start")
//        println(shape)
//        println("layout ${shape.layoutX} ${shape.layoutY}")
//        println("translate ${shape.translateX} ${shape.translateY}")
//        println("finish")

        if(shape is Rectangle){
            val smallerX = shape.x + shape.layoutX
            val greaterX = smallerX + shape.width
            val smallerY = shape.y + shape.layoutY
            val greaterY = smallerY + shape.height

            val lineList: MutableList<Line> = mutableListOf()
            lineList.add(Line(Point(smallerX, smallerY), Point(smallerX, greaterY)))
            lineList.add(Line(Point(smallerX, smallerY), Point(greaterX, smallerY)))
            lineList.add(Line(Point(greaterX, greaterY), Point(greaterX, smallerY)))
            lineList.add(Line(Point(greaterX, greaterY), Point(smallerX, greaterY)))

            val pathLine = findLineList(path)
            for(eacLine in lineList)
                for(eacPathLine in pathLine)
                    if(lineIntersect(eacLine, eacPathLine)) return true

            return false
        }
        else if(shape is Circle){
            val pathLine = findLineList(path)

            val center = Point(shape.centerX + shape.layoutX, shape.centerY + shape.layoutY)
            val radius = shape.radius

            for(eacPathLine in pathLine){
                if(insideCircle(eacPathLine.first, center, radius)) return true;
                if(insideCircle(eacPathLine.second, center, radius)) return true;
            }

            return false
        }

        return false
    }

    private fun insideCircle(point: Point, center: Point, radius: Double): Boolean {
        return (point.x-center.x)*(point.x-center.x)+(point.y-center.y)*(point.y-center.y) <= radius*radius
    }
}
package wb.frontend

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import kotlin.math.max

/**
 * ************* How to use ************************
 *
 * Rectangle rectangle = new Rectangle(50, 50);
 * rectangle.setFill(Color.BLACK);
 * DragResizeMod.makeResizable(rectangle, null);
 *
 * Pane root = new Pane();
 * root.getChildren().add(rectangle);
 *
 * primaryStage.setScene(new Scene(root, 300, 275));
 * primaryStage.show();
 *
 * ************* OnDragResizeEventListener **********
 *
 * You need to override OnDragResizeEventListener and
 * 1) preform out of main field bounds check
 * 2) make changes to the node
 * (this class will not change anything in node coordinates)
 *
 * There is defaultListener and it works only with Canvas nad Rectangle
 */
interface OnDragResizeEventListener {
    fun onDrag(node: Node, x: Double, y: Double, h: Double, w: Double)
    fun onResize(node: Node, x: Double, y: Double, h: Double, w: Double)
}
class OnDragResizeEL: OnDragResizeEventListener {
    override fun onDrag(node: Node, x: Double, y: Double, h: Double, w: Double) {

        setNodeSize(node, x, y, h, w)
    }

    override fun onResize(node: Node, x: Double, y: Double, h: Double, w: Double) {

        setNodeSize(node, x, y, h, w)
    }

    private fun setNodeSize(node: Node, x: Double, y: Double, h: Double, w: Double) {
        node.layoutX = x
        node.layoutY = y
        // TODO find generic way to set width and height of any node
        // here we cant set height and width to node directly.
        // or i just cant find how to do it,
        // so you have to wright resize code anyway for your Nodes...
        //something like this
        if (node is Canvas) {
            node.width = w
            node.height = h
        } else if (node is Rectangle) {
            node.width = w
            node.height = h
        } else if (node is Circle) {
            node.radius = max(w/2, h/2)
        }
    }
}

class DragResize private constructor(private val node: Node, listener: OnDragResizeEventListener?) {

    enum class S {
        DEFAULT, DRAG, NW_RESIZE, SW_RESIZE, NE_RESIZE, SE_RESIZE, E_RESIZE, W_RESIZE, N_RESIZE, S_RESIZE
    }
    private var clickX = 0.0
    private var clickY = 0.0
    private var offsetX = 0.0
    private var offsetY = 0.0
    private var nodeX = 0.0
    private var nodeY = 0.0
    private var nodeH = 0.0
    private var nodeW = 0.0
    private var startX = 0.0
    private var startY = 0.0
    private var startH = 0.0
    private var startW = 0.0
    private var state = S.DEFAULT
    private val listener: OnDragResizeEventListener? = defaultListener

    init {
        //if (listener != null) this.listener = listener
    }

    protected fun mouseReleased(event: MouseEvent) {
        wb.save()
        if (state == S.DRAG) {
            node.layoutX = max(-node.layoutBounds.minX, event.sceneX - offsetX)
            node.layoutY = max(-node.layoutBounds.minY, event.sceneY - offsetY)

            node.translateX = 0.0
            node.translateY = 0.0
        }
        node.cursor = Cursor.DEFAULT
        state = S.DEFAULT
    }

    protected fun mouseOver(event: MouseEvent) {
        val state = currentMouseState(event)
        val cursor = getCursorForState(state)
        node.cursor = cursor
    }

    private fun currentMouseState(event: MouseEvent): S {
        var state = S.DEFAULT
        val left = isLeftResizeZone(event)
        val right = isRightResizeZone(event)
        val top = isTopResizeZone(event)
        val bottom = isBottomResizeZone(event)
        if (left && top) state = S.NW_RESIZE else if (left && bottom) state = S.SW_RESIZE else if (right && top) state =
            S.NE_RESIZE else if (right && bottom) state = S.SE_RESIZE else if (right) state =
            S.E_RESIZE else if (left) state = S.W_RESIZE else if (top) state = S.N_RESIZE else if (bottom) state =
            S.S_RESIZE else if (isInDragZone(event)) state = S.DRAG
        return state
    }

    protected fun mouseDragged(event: MouseEvent) {
        if (cursor == CursorType.eraser){
            canvas?.children?.remove(node)
            return
        }
        if (listener != null) {

            val mouseX = parentX(event.x)
            val mouseY = parentY(event.y)
            if (state == S.DRAG) {
                node.translateX = event.sceneX - clickX
                node.translateY = event.sceneY - clickY
//                node.translateX = max(-node.layoutBounds.minX, event.sceneX - clickX)
//                node.translateY = max(-node.layoutBounds.minY,event.sceneY - clickY)
                //listener.onDrag(node, mouseX - clickX, mouseY - clickY, nodeH, nodeW)
            } else if (state != S.DEFAULT) {
                //resizing
                var newX = startX
                var newY = startY
                var newH = startH
                var newW = startW

                // Right Resize
                if (state == S.E_RESIZE || state == S.NE_RESIZE || state == S.SE_RESIZE) {
                    newW = startW + event.sceneX - clickX
                }
                // Left Resize
                if (state == S.W_RESIZE || state == S.NW_RESIZE || state == S.SW_RESIZE) {
                    newX = startX + event.sceneX - clickX
                    newW = startW + clickX - event.sceneX
                }

                // Bottom Resize
                if (state == S.S_RESIZE || state == S.SE_RESIZE || state == S.SW_RESIZE) {
                    newH = startH + event.sceneY - clickY

                }
                // Top Resize
                if (state == S.N_RESIZE || state == S.NW_RESIZE || state == S.NE_RESIZE) {
                    newY = startY + event.sceneY - clickY
                    newH = startH + clickY - event.sceneY
                }

                //min valid rect Size Check
                if (newW < MIN_W) {
                    if (state == S.W_RESIZE || state == S.NW_RESIZE || state == S.SW_RESIZE) newX = newX - MIN_W + newW
                    newW = MIN_W
                }
                if (newH < MIN_H) {
                    if (state == S.N_RESIZE || state == S.NW_RESIZE || state == S.NE_RESIZE) newY = newY + newH - MIN_H
                    newH = MIN_H
                }
                if (node is Circle) {listener.onResize(node, startX, startY, newH, newW)}
                else {listener.onResize(node, newX, newY, newH, newW)}
            }
        }
    }

    protected fun mousePressed(event: MouseEvent) {
        cursor = CursorType.cursor
//        print(event.x)
        nodeX = nodeX()
        nodeY = nodeY()
        nodeH = nodeH()
        nodeW = nodeW()
        state = if (isInResizeZone(event)) {
            //setNewInitialEventCoordinates(event)
            clickX = event.sceneX
            clickY = event.sceneY
            startX = node.layoutX
            startY = node.layoutY
            startW = nodeW()
            startH = nodeH()
//            offsetX = event.sceneX - node.layoutX
//            offsetY = event.sceneY - node.layoutY
            currentMouseState(event)
        } else if (isInDragZone(event)) {
            clickX = event.sceneX
            clickY = event.sceneY
            offsetX = event.sceneX - node.layoutX
            offsetY = event.sceneY - node.layoutY
            //setNewInitialEventCoordinates(event)
            S.DRAG
        } else {
            S.DEFAULT
        }
    }


    private fun isInResizeZone(event: MouseEvent): Boolean {
        return (isLeftResizeZone(event) || isRightResizeZone(event)
                || isBottomResizeZone(event) || isTopResizeZone(event))
    }

    private fun isInDragZone(event: MouseEvent): Boolean {
//        val xPos = parentX(event.x)
//        val yPos = parentY(event.y)
//        val nodeX = nodeX() + MARGIN
//        val nodeY = nodeY() + MARGIN
//        val nodeX0 = nodeX() + nodeW() - MARGIN
//        val nodeY0 = nodeY() + nodeH() - MARGIN
        //return xPos > nodeX && xPos < nodeX0 && yPos > nodeY && yPos < nodeY0
        return true
    }

    private fun isLeftResizeZone(event: MouseEvent): Boolean {
        if (node is Circle) { return intersect(-nodeW()/2, event.x)}
        return intersect(0.0, event.x)
    }

    private fun isRightResizeZone(event: MouseEvent): Boolean {
        if (node is Circle) { return intersect(nodeW()/2, event.x)}
        return intersect(nodeW(), event.x)
    }

    private fun isTopResizeZone(event: MouseEvent): Boolean {
        if (node is Circle) { return intersect(-nodeH()/2, event.y)}
        return intersect(0.0, event.y)
    }

    private fun isBottomResizeZone(event: MouseEvent): Boolean {
        if (node is Circle) { return intersect(nodeH()/2, event.y)}
        return intersect(nodeH(), event.y)
    }

    private fun intersect(side: Double, point: Double): Boolean {
        return side + MARGIN > point && side - MARGIN < point
    }

    private fun parentX(localX: Double): Double {
        return nodeX() + localX
    }

    private fun parentY(localY: Double): Double {
        return nodeY() + localY
    }

    private fun nodeX(): Double {
       // return node.boundsInParent.minX
        return node.layoutX
    }

    private fun nodeY(): Double {
        //return node.boundsInParent.minY
        return node.layoutY
    }

    private fun nodeW(): Double {
        return node.boundsInParent.width
    }

    private fun nodeH(): Double {
        return node.boundsInParent.height
    }

    companion object {
        private val defaultListener: OnDragResizeEventListener = OnDragResizeEL()
        private var canvas: Pane? = null
        private const val MARGIN = 8
        private const val MIN_W = 30.0
        private const val MIN_H = 20.0
        @JvmOverloads
        fun makeResizable(node: Node, canva: Pane) {
            canvas = canva
            val resizer = DragResize(node, null)
            node.onMousePressed = EventHandler { event ->
                if (cursor != CursorType.pen && cursor != CursorType.eraser) {
                resizer.mousePressed(event)}}
            node.onMouseDragged = EventHandler { event -> if (cursor != CursorType.pen) resizer.mouseDragged(event) }
            node.onMouseDragged = EventHandler { event -> if (cursor != CursorType.pen) resizer.mouseDragged(event) }
            node.onMouseMoved = EventHandler { event -> if (cursor != CursorType.pen) resizer.mouseOver(event) }
            node.onMouseReleased = EventHandler { event -> if (cursor != CursorType.pen) resizer.mouseReleased(event) }
        }

        private fun getCursorForState(state: S): Cursor {
            if (cursor == CursorType.pen) {return Cursor.DEFAULT}
            return when (state) {
                S.NW_RESIZE -> Cursor.NW_RESIZE
                S.SW_RESIZE -> Cursor.SW_RESIZE
                S.NE_RESIZE -> Cursor.NE_RESIZE
                S.SE_RESIZE -> Cursor.SE_RESIZE
                S.E_RESIZE -> Cursor.E_RESIZE
                S.W_RESIZE -> Cursor.W_RESIZE
                S.N_RESIZE -> Cursor.N_RESIZE
                S.S_RESIZE -> Cursor.S_RESIZE
                else -> Cursor.DEFAULT
            }
        }
    }
}
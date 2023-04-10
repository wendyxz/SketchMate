package wb.frontend

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Pane
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Polygon
import javafx.scene.shape.Rectangle
import wb.frontend.Tools.CursorType
import wb.frontend.Tools.cursor
import wb.helper.save
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
    private var minX = 0.0
    private var maxX = 0.0
    private var minY = 0.0

    private var state = S.DEFAULT
    private val listener: OnDragResizeEventListener? = defaultListener

    protected fun mouseReleased(event: MouseEvent) {
        if (state == S.DRAG) {
            if (node is Polygon) {
                for (i in 0..5 step 2) {
                    node.points[i] += node.translateX
                }
                for (i in 1..5 step 2) {
                    node.points[i] += node.translateY
                }
            } else {
                node.layoutX = max(-node.layoutBounds.minX, event.sceneX - offsetX)
                node.layoutY = max(-node.layoutBounds.minY, event.sceneY - offsetY)
            }

            node.translateX = 0.0
            node.translateY = 0.0
        }
        node.cursor = Cursor.DEFAULT
        state = S.DEFAULT
        save()
    }

    protected fun mouseOver(event: MouseEvent) {
        val state = currentMouseState(event)
        var cursor =  Cursor.DEFAULT
        if (node !is Path) {
            cursor = getCursorForState(state)
        }
        node.cursor = cursor
    }

    private fun currentMouseState(event: MouseEvent): S {
        var state = S.DEFAULT
        if (node is Polygon) {
            val x = event.x
            val y = event.y
            if (node.points[0]+node.layoutX - MARGIN <= x && x <= node.points[0]+node.layoutX + MARGIN) {
                state = S.W_RESIZE
            } else if (node.points[2]+node.layoutX + MARGIN >= x && x >= node.points[2]+node.layoutX - MARGIN) {
                state = S.E_RESIZE
            } else if (node.points[5]+node.layoutY- MARGIN <= y && y <= node.points[5]+node.layoutY + MARGIN) {
                state = S.N_RESIZE
            }
        } else {
            val left = isLeftResizeZone(event)
            val right = isRightResizeZone(event)
            val top = isTopResizeZone(event)
            val bottom = isBottomResizeZone(event)
            if (left && top) state = S.NW_RESIZE else if (left && bottom) state =
                S.SW_RESIZE else if (right && top) state =
                S.NE_RESIZE else if (right && bottom) state = S.SE_RESIZE else if (right) state =
                S.E_RESIZE else if (left) state = S.W_RESIZE else if (top) state = S.N_RESIZE else if (bottom) state =
                S.S_RESIZE else if (isInDragZone(event)) state = S.DRAG
        }
        return state
    }

    protected fun mouseDragged(event: MouseEvent) {
        if (cursor == CursorType.eraser){
            canvas?.children?.remove(node)
            return
        }
        if (listener != null) {
            if (state == S.DRAG) {
                node.translateX = event.sceneX - clickX
                node.translateY = event.sceneY - clickY
            } else if (state != S.DEFAULT) {
                // triangle
                if (node is Polygon) {
                    when (state) {
                        S.N_RESIZE -> node.points[5] = minY + event.sceneY - clickY
                        S.W_RESIZE -> node.points[0] = minX - clickX + event.sceneX
                        S.E_RESIZE -> node.points[2] = maxX + event.sceneX - clickX
                    }
                    return
                }

                //resizing
                var newX = startX
                var newY = startY
                var newH = startH
                var newW = startW

                // Right Resize
                if (state == S.E_RESIZE || state == S.NE_RESIZE || state == S.SE_RESIZE) {
                    if (node is Circle) newW = startW - (clickX - event.sceneX)*2
                    else newW = startW + event.sceneX - clickX
                }
                // Left Resize
                if (state == S.W_RESIZE || state == S.NW_RESIZE || state == S.SW_RESIZE) {
                    if (node is Circle) newW = startW - (-clickX + event.sceneX)*2
                    else {
                        newX = startX + event.sceneX - clickX
                        newW = startW + clickX - event.sceneX
                    }
                }

                // Bottom Resize
                if (state == S.S_RESIZE || state == S.SE_RESIZE || state == S.SW_RESIZE) {
                    if (node is Circle) newW = startW - (clickY - event.sceneY)*2
                    else newH = startH + event.sceneY - clickY
                }
                // Top Resize
                if (state == S.N_RESIZE || state == S.NW_RESIZE || state == S.NE_RESIZE) {
                    if (node is Circle) newW = startW - (-clickY + event.sceneY)*2
                    else {
                        newY = startY + event.sceneY - clickY
                        newH = startH + clickY - event.sceneY
                    }
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
                if (node is Circle) {listener.onResize(node, startX, startY, newW, newW)}
                else {listener.onResize(node, newX, newY, newH, newW)}
            }
        }
    }

    protected fun mousePressed(event: MouseEvent) {
        cursor = CursorType.cursor

        nodeX = nodeX()
        nodeY = nodeY()
        nodeH = nodeH()
        nodeW = nodeW()
        state = if (isInResizeZone(event)) {
            clickX = event.sceneX
            clickY = event.sceneY
            startX = node.layoutX
            startY = node.layoutY
            startW = nodeW()
            startH = nodeH()
            currentMouseState(event)
        } else if (isInDragZone(event)) {
            clickX = event.sceneX
            clickY = event.sceneY
            offsetX = event.sceneX - node.layoutX
            offsetY = event.sceneY - node.layoutY

            S.DRAG
        } else {
            S.DEFAULT
        }
    }


    private fun isInResizeZone(event: MouseEvent): Boolean {
        if (node is Polygon) {
            return isTriangleResizeZone(node, event)
        }
        return (isLeftResizeZone(event) || isRightResizeZone(event)
                || isBottomResizeZone(event) || isTopResizeZone(event))
    }

    private fun isInDragZone(event: MouseEvent): Boolean {
        return true
    }

    private fun isTriangleResizeZone(node: Polygon, event: MouseEvent) : Boolean {
        minX = node.points[0]
        maxX = node.points[2]
        minY = node.points[5]
        val x = event.x
        val y = event.y
        return  node.points[0] - MARGIN <= x && x <= node.points[0] + MARGIN ||
                node.points[2] + MARGIN >= x && x >= node.points[2] - MARGIN ||
                node.points[5] - MARGIN <= y && y <= node.points[5] + MARGIN
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

    private fun nodeX(): Double {
        return node.layoutX
    }

    private fun nodeY(): Double {
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
        fun makeResizable(node: Node) {
            val resizer = DragResize(node, null)
            node.onMousePressed = EventHandler { event ->
                if (cursor != CursorType.pen && cursor != CursorType.eraser) {
                resizer.mousePressed(event)}}
            node.onMouseDragged = EventHandler {
                    event -> if (cursor != CursorType.pen && cursor != CursorType.eraser) resizer.mouseDragged(event)
            }
            node.onMouseMoved = EventHandler {
                    event -> if (cursor != CursorType.pen && cursor != CursorType.eraser) resizer.mouseOver(event)
            }
            node.onMouseReleased = EventHandler {
                    event -> if (cursor != CursorType.pen && cursor != CursorType.eraser) resizer.mouseReleased(event)
            }
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
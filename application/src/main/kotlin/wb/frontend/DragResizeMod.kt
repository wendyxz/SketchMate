package wb.frontend

import javafx.event.EventHandler
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.canvas.Canvas
import javafx.scene.input.MouseEvent
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle

class DragResizeMod private constructor(node: Node, listener: OnDragResizeEventListener?) {
    interface OnDragResizeEventListener {
        fun onDrag(node: Node?, x: Double, y: Double, h: Double, w: Double)
        fun onResize(node: Node?, x: Double, y: Double, h: Double, w: Double)
    }

    enum class S {
        DEFAULT, DRAG, NW_RESIZE, SW_RESIZE, NE_RESIZE, SE_RESIZE, E_RESIZE, W_RESIZE, N_RESIZE, S_RESIZE
    }

    private var clickX = 0.0
    private var clickY = 0.0
    private var nodeX = 0.0
    private var nodeY = 0.0
    private var nodeH = 0.0
    private var nodeW = 0.0
    private var state = S.DEFAULT
    private val node: Node
    private var listener: OnDragResizeEventListener? = defaultListener

    init {
        this.node = node
        if (listener != null) this.listener = listener
    }

    protected fun mouseReleased(event: MouseEvent?) {
        if (event != null) {
            if (event.isSecondaryButtonDown) {
                node.cursor = Cursor.DEFAULT
                state = S.DEFAULT
            }
        }
    }

    protected fun mouseOver(event: MouseEvent) {
        if (event.isSecondaryButtonDown) {
            val state = currentMouseState(event)
            val cursor: Cursor = getCursorForState(state)
            node.cursor = cursor
        }
    }

    private fun currentMouseState(event: MouseEvent): S {
        if (event.isSecondaryButtonDown) {
            var state = S.DEFAULT
            val left = isLeftResizeZone(event)
            val right = isRightResizeZone(event)
            val top = isTopResizeZone(event)
            val bottom = isBottomResizeZone(event)
            if (left && top) state = S.NW_RESIZE else if (left && bottom) state =
                S.SW_RESIZE else if (right && top) state =
                S.NE_RESIZE else if (right && bottom) state = S.SE_RESIZE else if (right) state =
                S.E_RESIZE else if (left) state = S.W_RESIZE else if (top) state = S.N_RESIZE else if (bottom) state =
                S.S_RESIZE else if (isInDragZone(event)) state = S.DRAG
            return state
        }
        return S.DEFAULT
    }

    protected fun mouseDragged(event: MouseEvent) {
        if (event.isSecondaryButtonDown) {
            if (listener != null) {
                val mouseX = parentX(event.getX())
                val mouseY = parentY(event.getY())
                if (state == S.DRAG) {
                    listener!!.onDrag(node, mouseX - clickX, mouseY - clickY, nodeH, nodeW)
                } else if (state != S.DEFAULT) {
                    //resizing
                    var newX = nodeX
                    var newY = nodeY
                    var newH = nodeH
                    var newW = nodeW

                    // Right Resize
                    if (state == S.E_RESIZE || state == S.NE_RESIZE || state == S.SE_RESIZE) {
                        newW = mouseX - nodeX
                    }
                    // Left Resize
                    if (state == S.W_RESIZE || state == S.NW_RESIZE || state == S.SW_RESIZE) {
                        newX = mouseX
                        newW = nodeW + nodeX - newX
                    }

                    // Bottom Resize
                    if (state == S.S_RESIZE || state == S.SE_RESIZE || state == S.SW_RESIZE) {
                        newH = mouseY - nodeY
                    }
                    // Top Resize
                    if (state == S.N_RESIZE || state == S.NW_RESIZE || state == S.NE_RESIZE) {
                        newY = mouseY
                        newH = nodeH + nodeY - newY
                    }

                    //min valid rect Size Check
                    if (newW < MIN_W) {
                        if (state == S.W_RESIZE || state == S.NW_RESIZE || state == S.SW_RESIZE) newX =
                            newX - MIN_W + newW
                        newW = MIN_W
                    }
                    if (newH < MIN_H) {
                        if (state == S.N_RESIZE || state == S.NW_RESIZE || state == S.NE_RESIZE) newY =
                            newY + newH - MIN_H
                        newH = MIN_H
                    }
                    listener!!.onResize(node, newX, newY, newH, newW)
                }
            }
        }
    }

    protected fun mousePressed(event: MouseEvent) {
        if (event.isSecondaryButtonDown) {
            state = if (isInResizeZone(event)) {
                setNewInitialEventCoordinates(event)
                currentMouseState(event)
            } else if (isInDragZone(event)) {
                setNewInitialEventCoordinates(event)
                S.DRAG
            } else {
                S.DEFAULT
            }
        }
    }

    private fun setNewInitialEventCoordinates(event: MouseEvent) {
        if (event.isSecondaryButtonDown) {
            nodeX = nodeX()
            nodeY = nodeY()
            nodeH = nodeH()
            nodeW = nodeW()
            clickX = event.getX()
            clickY = event.getY()
        }
    }

    private fun isInResizeZone(event: MouseEvent): Boolean {
        if (event.isSecondaryButtonDown) {
            return (isLeftResizeZone(event) || isRightResizeZone(event)
                    || isBottomResizeZone(event) || isTopResizeZone(event))
        }
        return false
    }

    private fun isInDragZone(event: MouseEvent): Boolean {
        if (event.isSecondaryButtonDown) {
            val xPos = parentX(event.getX())
            val yPos = parentY(event.getY())
            val nodeX = nodeX() + MARGIN
            val nodeY = nodeY() + MARGIN
            val nodeX0 = nodeX() + nodeW() - MARGIN
            val nodeY0 = nodeY() + nodeH() - MARGIN
            return xPos > nodeX && xPos < nodeX0 && yPos > nodeY && yPos < nodeY0
        }
        return false
    }

    private fun isLeftResizeZone(event: MouseEvent): Boolean {
        if (event.isSecondaryButtonDown) {
            return intersect(0.0, event.getX())
        }
        return false
    }

    private fun isRightResizeZone(event: MouseEvent): Boolean {
        if (event.isSecondaryButtonDown) {
            return intersect(nodeW(), event.getX())
        }
        return false
    }

    private fun isTopResizeZone(event: MouseEvent): Boolean {
        if (event.isSecondaryButtonDown) {
            return intersect(0.0, event.getY())
        }
        return false
    }

    private fun isBottomResizeZone(event: MouseEvent): Boolean {
        if (event.isSecondaryButtonDown) {
            return intersect(nodeH(), event.getY())
        }
        return false
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
        return node.getBoundsInParent().getMinX()
    }

    private fun nodeY(): Double {
        return node.getBoundsInParent().getMinY()
    }

    private fun nodeW(): Double {
        return node.getBoundsInParent().getWidth()
    }

    private fun nodeH(): Double {
        return node.getBoundsInParent().getHeight()
    }

    companion object {
        private val defaultListener: OnDragResizeEventListener = object : OnDragResizeEventListener {
            override fun onDrag(node: Node?, x: Double, y: Double, h: Double, w: Double) {
                /*
            // TODO find generic way to get parent width and height of any node
            // can perform out of bounds check here if you know your parent size
            if (x > width - w ) x = width - w;
            if (y > height - h) y = height - h;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            */
                if (node != null) {
                    setNodeSize(node, x, y, h, w)
                }
            }

            override fun onResize(node: Node?, x: Double, y: Double, h: Double, w: Double) {
                /*
            // TODO find generic way to get parent width and height of any node
            // can perform out of bounds check here if you know your parent size
            if (w > width - x) w = width - x;
            if (h > height - y) h = height - y;
            if (x < 0) x = 0;
            if (y < 0) y = 0;
            */
                if (node != null) {
                    setNodeSize(node, x, y, h, w)
                }
            }

            private fun setNodeSize(node: Node, x: Double, y: Double, h: Double, w: Double) {
//                node.setLayoutX(x)
//                node.setLayoutY(y)
                // TODO find generic way to set width and height of any node
                // here we cant set height and width to node directly.
                // or i just cant find how to do it,
                // so you have to wright resize code anyway for your Nodes...
                //something like this
                if (node is Canvas) {
                    (node as Canvas).width = w
                    (node as Canvas).height = h
                } else if (node is Rectangle) {
                    (node as Rectangle).width = w
                    (node as Rectangle).height = h
                } else if (node is VBox) {
                    (node as VBox).minWidth = w
                    (node as VBox).maxWidth = w
                    (node as VBox).minHeight = h
                    (node as VBox).maxHeight = h
                }
            }
        }
        private const val MARGIN = 40
        private const val MIN_W = 30.0
        private const val MIN_H = 20.0
        fun makeResizable(node: Node) {
            makeResizable(node, null)
        }

        fun makeResizable(node: Node, listener: OnDragResizeEventListener?) {
            val resizer = DragResizeMod(node, listener)
            node.setOnMousePressed(object : EventHandler<MouseEvent?> {
                override fun handle(event: MouseEvent?) {
                    if (event != null && event.isSecondaryButtonDown) {
                        resizer.mousePressed(event)
                    }
                }
            })
            node.setOnMouseDragged(object : EventHandler<MouseEvent?> {
                override fun handle(event: MouseEvent?) {
                    if (event != null && event.isSecondaryButtonDown) {
                        resizer.mouseDragged(event)
                    }
                }
            })
            node.setOnMouseMoved(object : EventHandler<MouseEvent?> {
                override fun handle(event: MouseEvent?) {
                    if (event != null && event.isSecondaryButtonDown) {
                        resizer.mouseOver(event)
                    }
                }
            })
            node.setOnMouseReleased(object : EventHandler<MouseEvent?> {
                override fun handle(event: MouseEvent?) {
                    if (event != null) {
                        if (event.isSecondaryButtonDown) {
                            resizer.mouseReleased(event)
                        }
                    }

                }
            })
        }

        private fun getCursorForState(state: S): Cursor {
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
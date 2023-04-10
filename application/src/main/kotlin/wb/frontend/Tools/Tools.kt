package wb.frontend

import javafx.scene.Node
import javafx.scene.input.MouseEvent
import wb.frontend.Tools.CursorType
import wb.frontend.Tools.cursor


private var cursorAnchorX = 0.0
private var cursorAnchorY = 0.0
private var mouseOffsetX = 0.0
private var mouseOffsetY = 0.0
private fun onPressedEvent(node: Node, event: MouseEvent) {
    if (cursor == CursorType.pen) return
    if (event.isSecondaryButtonDown) return
    cursorAnchorX = event.sceneX
    cursorAnchorY = event.sceneY
    mouseOffsetX = event.sceneX-node.layoutX
    mouseOffsetY = event.sceneY-node.layoutY
}
private fun onDraggedEvent(node: Node, event: MouseEvent) {
    if (cursor == CursorType.pen) return
    if (event.isSecondaryButtonDown) return
    node.translateX = event.getSceneX()-cursorAnchorX
    node.translateY = event.getSceneY()-cursorAnchorY
}
private fun onReleasedEvent(node: Node, event: MouseEvent) {
    if (cursor == CursorType.pen) return
    if (event.isSecondaryButtonDown) return
    node.layoutX = event.getSceneX() - mouseOffsetX
    node.layoutY = event.getSceneY() - mouseOffsetY
    node.translateX = 0.0
    node.translateY = 0.0
}
fun makeDraggable(handleObject: Node, moveObject : Node = handleObject) {
    handleObject.setOnMousePressed { event -> onPressedEvent(moveObject, event) }
    handleObject.setOnMouseDragged { event -> onDraggedEvent(moveObject, event) }
    handleObject.setOnMouseReleased { event -> onReleasedEvent(moveObject, event) }
}


package wb.frontend

import javafx.scene.control.Alert

// Create GENERAL FORM!!!! TODO!
fun showWarnDialog(title: String, content: String?) {
    var displayContent = content
    if (displayContent == null) {
        displayContent = "Unspecified"
    }
    val alert = Alert(Alert.AlertType.INFORMATION)
//        val X = this.stage.x + this.stage.width / 2
//        val Y = this.stage.y + this.stage.height / 2
//        alert.x = X
//        alert.y = Y
    alert.x = 400.0
    alert.y = 400.0

    alert.title = title
    alert.contentText = displayContent

    alert.showAndWait()
}

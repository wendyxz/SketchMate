package wb.helper

import javafx.scene.control.Alert

fun showWarnDialog(title: String, content: String?) {
    var displayContent = content
    if (displayContent == null) {
        displayContent = "Unspecified"
    }
    val alert = Alert(Alert.AlertType.INFORMATION)

    alert.x = 400.0
    alert.y = 400.0

    alert.title = title
    alert.contentText = displayContent

    alert.showAndWait()
}

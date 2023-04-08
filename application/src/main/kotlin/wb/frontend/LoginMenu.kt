package wb.frontend

import javafx.geometry.HPos
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.GridPane
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Callback
import javafx.beans.property.SimpleStringProperty

class LoginMenu(root: BorderPane, stage: Stage, width: Double, height: Double) {
    private val title = Text("User Login / Sign up")
    private val loginButton = Button("Log in")
    private val signUpButton = Button("Sign up")
    private val usernameLabel = Label("Username:")
    private val username = TextField()
    private val passwordLabel = Label("Password:")
    private val password = PasswordField()
    private val grid = GridPane()

    init {
        val co1 = ColumnConstraints()
        val co2 = ColumnConstraints()
        co1.percentWidth = 15.0
        co1.halignment = HPos.CENTER
        co2.percentWidth = 15.0
        co2.halignment = HPos.CENTER
        grid.columnConstraints.addAll(co1, co2)
        grid.alignment = Pos.CENTER
        grid.vgap = 15.0
        grid.hgap = -5.0
        grid.add(title, 0, 0, 2, 1)
        grid.add(usernameLabel, 0, 1)
        grid.add(username, 1, 1)
        grid.add(passwordLabel, 0, 2)
        grid.add(password, 1, 2)
        grid.add(loginButton, 0, 3)
        grid.add(signUpButton, 1, 3)
        stage.scene = Scene(grid, width, height)
        stage.show()

        loginButton.setOnMouseClicked {
            if (username.text == "") {
                showWarnDialog("Unspecified Username!", "Please enter username!")
            } else if (password.text == "") {
                showWarnDialog("Unspecified Password!", "Please enter password!")
            }
            try {
                // todo: add some output to this
                val str = wb.backend.login(username.text, password.text)
                when (str) {
                    "Success" -> {
                        showWarnDialog("Success!", "Log in success!")
                        stage.scene = Scene(root, width, height)
                        addKeyListener(stage)
                        wb.backend.username = username.text
                        wb.backend.password = password.text
                        updateTitle(stage)
                    }

                    "Wrong password" -> {
                        showWarnDialog("Wrong Password!", "Please check your password and try again!")
                    }

                    else -> {
                        // this should be not finding such user case
                        showWarnDialog("User not found!", "Please check your username and try again!")
                    }
                }

            } catch (e: Exception) {
                e.stackTrace.forEach { println(it) }
                showWarnDialog("Error", e.toString())
            }
        }

        signUpButton.setOnMouseClicked {
            val dialog: Dialog<VerifyCredential> = Dialog()
            dialog.title = "Sign up for an account"
            dialog.headerText = "Please enter your user credential to sign up for an account:"
            dialog.isResizable = false

            val label1 = Label("username: ")
            val label2 = Label("password: ")
            val label3 = Label("Verify password: ")
            val username = TextField()
            val password = PasswordField()
            val passwordRepeat = PasswordField()

            //val buttonTypeOk = Button("Sign Up")
            val buttonTypeOk = ButtonType("Okay", ButtonBar.ButtonData.OK_DONE)

            val grid = GridPane()
            grid.add(label1, 1, 1)
            grid.add(username, 2, 1)
            grid.add(label2, 1, 2)
            grid.add(password, 2, 2)
            grid.add(label3, 1, 3)
            grid.add(passwordRepeat, 2, 3)
            dialog.dialogPane.content = grid

            dialog.dialogPane.buttonTypes.add(buttonTypeOk)

            // set dialog pos
//            val X = this.viewModel.model.stage.x + this.viewModel.model.stage.width / 2
//            val Y = this.viewModel.model.stage.y + this.viewModel.model.stage.height / 2
//            dialog.x = X
//            dialog.y = Y
            dialog.x = 400.0
            dialog.y = 400.0

            dialog.resultConverter = Callback<ButtonType?, VerifyCredential?> {
                if (it == buttonTypeOk) VerifyCredential(username.text, password.text, passwordRepeat.text) else null
            }

            // 'x' functionality.
            dialog.setOnCloseRequest {
                dialog.hide()
            }

            val result = dialog.showAndWait()
            //println("${result.get().username} ${result.get().password} ${result.get().verifyPassword}")

            if (result.isPresent) {
                // now we check if two password is the same
                if (result.get().password != result.get().verifyPassword) {
                    showWarnDialog("Password incorrect!", "Password doesn't match, please try again!")
                } else if (result.get().username == "") {
                    showWarnDialog("Unspecified Username!", "Please enter username!")
                } else if (result.get().password == "") {
                    showWarnDialog("Unspecified Password!", "Please enter password!")
                } else {
                    try {
                        // todo: add some output to this
                        println(wb.backend.createUser(result.get().username, result.get().password))
                        showWarnDialog("Success", "Successfully signed up, please log in!")
                    } catch (e: Exception) {
                        showWarnDialog("Error", e.toString())
                    }
                }
            }
        }
    }

    fun showWarnDialog(title: String, content: String?) {
        var displayContent = content
        if (displayContent == null) {
            displayContent = "Unspecified"
        }
        val alert = Alert(Alert.AlertType.INFORMATION)
//        val X = stage.x + stage.width / 2
//        val Y = stage.y + stage.height / 2
//        alert.x = X
//        alert.y = Y
        alert.x = 400.0
        alert.y = 400.0
        alert.title = title
        alert.contentText = displayContent

        alert.showAndWait()
    }

    private fun updateTitle(stage: Stage) {
        println(wb.backend.username)
        stage.titleProperty().bind(
            SimpleStringProperty(
                "WhiteBoard     - ${
                    if (wb.backend.username != "")
                        "Logged In: ${wb.backend.username}"
                    else
                        "Not Logged In"
                }"
            )
        )
    }

}
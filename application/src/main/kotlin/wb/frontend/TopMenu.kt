package wb.frontend

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Callback

//this is from the register dialog box
class Credential(val username: String, val password: String)
class VerifyCredential(val username: String, val password: String, val verifyPassword: String)

class TopMenu(
    setBackgroundColour: (color: Color) -> Unit,
    save: (filename: String) -> Unit, load: (filename: String) -> Unit, stage: Stage
) : MenuBar() {

//    var rightLabel = Label("Logged in: ${wb.backend.username}")

    // Menu choices
    private val fileMenu = Menu("File")
    private val editMenu = Menu("Edit")
    private val helpMenu = Menu("Help")
    private val accountMenu = Menu("Account")
    private val themeMenu = Menu("Theme")

    // File sub-menu
    private val fileNew = MenuItem("New File")
    private val fileOpen = MenuItem("Open File")
    private val fileSave = MenuItem("Save")
    private val fileLoad = MenuItem("Load")
    private val fileExport = MenuItem("Export as PNG")
    private val fileExPDF = MenuItem("Export as PDF")
    private val fileQuit = MenuItem("Quit")

    // Edit sub-menu
    private val editUndo = MenuItem("Undo")
    private val editRedo = MenuItem("Redo")
    private val editCut = MenuItem("Cut Item")
    private val editCopy = MenuItem("Copy Item")
    private val editPaste = MenuItem("Paste Item")

    // Help sub-menu
    private val helpAbout = MenuItem("About")

    // Account sub-menu
    private val accountLogOut = MenuItem("Log Out")
    private val accountChangeP = MenuItem("Change Password")

    // Theme sub-menu
    private val lightTheme = MenuItem("light")
    private val darkTheme = MenuItem("dark")

    init {
        fileMenu.items.addAll(fileNew, fileOpen, fileSave, fileLoad, fileExport, fileExPDF, fileQuit)
        editMenu.items.addAll(editUndo, editRedo, editCut, editCopy, editPaste)
        helpMenu.items.addAll((helpAbout))
        accountMenu.items.addAll(accountLogOut, accountChangeP)
        themeMenu.items.addAll(lightTheme, darkTheme)
        darkTheme.setOnAction { setBackgroundColour(Color.BLACK) }
        lightTheme.setOnAction { setBackgroundColour(Color.WHITE) }

        registerControllers(stage)
        fileControllers(save, load)

        menus.addAll(fileMenu, editMenu, helpMenu, accountMenu, themeMenu)
    }

    private fun fileControllers(
        save: (filename: String) -> Unit,
        load: (filename: String) -> Unit
    ) {
        fileNew.setOnAction {
            val inputDialog = TextInputDialog()
            inputDialog.headerText = "Enter file name:"
            val result = inputDialog.showAndWait()
            result.ifPresent { fileName ->
                println("New file name: $fileName")
            }
        }

        fileSave.setOnAction {
            val choiceDialog = ChoiceDialog("local", "local", "remote")
            choiceDialog.headerText = "Select save location:"
            val result = choiceDialog.showAndWait()
            result.ifPresent { location ->
                if (location == "local") {
                    fileSave.setOnAction { save("data.json") }
                }
                println("Save location: $location")
            }
        }

        fileLoad.setOnAction {
            val choices = listOf("local", "name1", "name2", "name3")
            val choiceDialog = ChoiceDialog(choices[0], choices)
            choiceDialog.headerText = "Select file to load:"
            val result = choiceDialog.showAndWait()
            result.ifPresent { selection ->
                if (selection == "local") {
                    fileLoad.setOnAction { load("data.json") }
                }
                println("Load selection: $selection")
            }
        }
    }

    private fun registerControllers(stage: Stage) {

        accountChangeP.setOnAction {

            val dialog: Dialog<VerifyCredential> = Dialog()
            dialog.title = "Change Password"
            dialog.headerText = "Please enter your new Password:"
            dialog.isResizable = false

            val label1 = Label("password: ")
            val label2 = Label("repeat password: ")
            val password = PasswordField()
            val verifyPassword = PasswordField()

            //val buttonTypeOk = Button("Sign Up")
            val buttonTypeOk = ButtonType("Sign In", ButtonBar.ButtonData.OK_DONE)

            val grid = GridPane()
            grid.add(label1, 1, 1)
            grid.add(password, 2, 1)
            grid.add(label2, 1, 2)
            grid.add(verifyPassword, 2, 2)
            dialog.dialogPane.content = grid

            dialog.dialogPane.buttonTypes.add(buttonTypeOk)

            // set dialog pos
//            val X = this.stage.x + this.viewModel.model.stage.width / 2
//            val Y = this.viewModel.model.stage.y + this.viewModel.model.stage.height / 2
//            dialog.x = X
//            dialog.y = Y
            dialog.x = 400.0
            dialog.y = 400.0

            dialog.resultConverter = Callback<ButtonType?, VerifyCredential?> {
                if (it == buttonTypeOk) VerifyCredential("", password.text, verifyPassword.text) else null
            }

            // 'x' functionality.
            dialog.setOnCloseRequest {
                dialog.hide()
            }

            val result = dialog.showAndWait()
            println("${result.get().username} ${result.get().password}")

            if (result.isPresent) {
                // now we check if two password is the same
                if (result.get().password != result.get().verifyPassword) {
                    showWarnDialog("Password incorrect!", "Password doesn't match, please try again!")
                } else if (result.get().password == "") {
                    showWarnDialog("Unspecified Password!", "Please enter password!")
                } else {
                    try {
                        // todo: add some output to this
                        println(wb.backend.updateUser(wb.backend.username, result.get().password))

                        wb.backend.password = result.get().password

                        showWarnDialog("Success", "Password successfully changed!")
                    } catch (e: Exception) {
                        showWarnDialog("Error", e.toString())
                    }
                }
            }

        }

        accountLogOut.setOnAction {

            showWarnDialog("message", wb.backend.logout())
            wb.backend.username = ""
            wb.backend.userId = ""
            wb.backend.password = ""
            wb.backend.cookieValue = ""
            updateTitle(stage)
        }

    }

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
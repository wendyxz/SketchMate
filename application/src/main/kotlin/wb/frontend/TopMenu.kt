package wb.frontend

import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.util.Callback

//this is from the register dialog box
class Credential(val username: String, val password: String)
class VerifyCredential(val username: String, val password: String, val verifyPassword: String)

class TopMenu(setBackgroundColour: (color: Color) -> Unit,
    save: (filename: String) -> Unit, load: (filename: String) -> Unit) : MenuBar() {
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
    private val accountLogIn = MenuItem("Log In")
    private val accountLogOut = MenuItem("Log Out")
    private val accountCreate = MenuItem("Create Account")
    private val accountChangeP = MenuItem("Change Password")

    // Theme sub-menu
    private val lightTheme = MenuItem("light")
    private val darkTheme = MenuItem("dark")

    init {
        fileMenu.items.addAll(fileNew, fileOpen, fileSave, fileLoad, fileExport, fileExPDF, fileQuit)
        editMenu.items.addAll(editUndo, editRedo, editCut, editCopy, editPaste)
        helpMenu.items.addAll((helpAbout))
        accountMenu.items.addAll(accountLogIn, accountLogOut, accountCreate, accountChangeP)

        themeMenu.items.addAll(lightTheme, darkTheme)
        darkTheme.setOnAction { setBackgroundColour(Color.BLACK) }
        lightTheme.setOnAction { setBackgroundColour(Color.WHITE) }

        registerControllers()

        fileSave.setOnAction { save("data.json") }
        fileLoad.setOnAction { load("data.json") }

        menus.addAll(fileMenu, editMenu, helpMenu, accountMenu, themeMenu)
    }

    private fun registerControllers() {

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

        accountCreate.setOnAction {

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
                    } catch (e: Exception) {
                        showWarnDialog("Error", e.toString())
                    }
                }
            }
        }

        accountLogIn.setOnAction {

            val dialog: Dialog<Credential> = Dialog()
            dialog.title = "Log In"
            dialog.headerText = "Please enter your user credential to log in:"
            dialog.isResizable = false

            val label1 = Label("username: ")
            val label2 = Label("password: ")
            val username = TextField()
            val password = PasswordField()

            //val buttonTypeOk = Button("Sign Up")
            val buttonTypeOk = ButtonType("Sign In", ButtonBar.ButtonData.OK_DONE)

            val grid = GridPane()
            grid.add(label1, 1, 1)
            grid.add(username, 2, 1)
            grid.add(label2, 1, 2)
            grid.add(password, 2, 2)
            dialog.dialogPane.content = grid

            dialog.dialogPane.buttonTypes.add(buttonTypeOk)

            // set dialog pos
//            val X = this.viewModel.model.stage.x + this.viewModel.model.stage.width / 2
//            val Y = this.viewModel.model.stage.y + this.viewModel.model.stage.height / 2
//            dialog.x = X
//            dialog.y = Y
            dialog.x = 400.0
            dialog.y = 400.0

            dialog.resultConverter = Callback<ButtonType?, Credential?> {
                if (it == buttonTypeOk) Credential(username.text, password.text) else null
            }

            // 'x' functionality.
            dialog.setOnCloseRequest {
                dialog.hide()
            }

            val result = dialog.showAndWait()
            //println("${result.get().username} ${result.get().password}")

            if (result.isPresent) {
                if (result.get().username == "") {
                    showWarnDialog("Unspecified Username!", "Please enter username!")
                } else if (result.get().password == "") {
                    showWarnDialog("Unspecified Password!", "Please enter password!")
                }

                try {
                    // todo: add some output to this
                    val str = wb.backend.login(result.get().username, result.get().password)
                    when (str) {
                        "Success" -> {
//                            val userPrefs = Preferences.userNodeForPackage(javaClass)
                            showWarnDialog("Success!", "Log in success!")
//                            val oldUsername = userPrefs.get("username", "")
//                            todo.common.username = result.get().username
//                            todo.common.password = result.get().password
//                            userPrefs.put("username", todo.common.username)
//                            userPrefs.put("password", todo.common.password)
//                            val res = todo.common.getUserEntries()
//                            if (res != "") {
//                                println("get entries success")
//                                if (oldUsername != todo.common.username){
//                                    viewModel.model.loadData(res)
//                                }
//                                else {
//                                    viewModel.model.syncData(res)
//                                }
//                            }
//                            updateView()
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
        }

        accountLogOut.setOnAction {

            showWarnDialog("message", wb.backend.logout())
            wb.backend.username = ""
            wb.backend.userId = ""
            wb.backend.password = ""
            wb.backend.cookieValue = ""
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

}
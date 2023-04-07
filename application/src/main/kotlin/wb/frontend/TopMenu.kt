package wb.frontend

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Callback
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.timerTask

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
    private val fileNew = MenuItem("New Board")
    private val fileOpen = MenuItem("Open Board")
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
        fileControllers(save, load, stage)
        export(stage)

        menus.addAll(fileMenu, editMenu, helpMenu, accountMenu, themeMenu)

        val autoSync: Timer = Timer()
        autoSync.scheduleAtFixedRate(timerTask() {
            Platform.runLater {
                load("data.json")
            }
        }, 100, 100)
    }

    private fun export(stage: Stage) {
        fileExport.setOnAction {
//            val snapshotParams = SnapshotParameters()
//            val dpi = 300.0
//            val width = dpi * scene.width / 72
//            val height = dpi * scene.height / 72
            val image = stage.scene.snapshot(null)
//            val image = WritableImage(width.toInt(), height.toInt())
//            stage.scene.snapshot(image)
            val bufferedImage = SwingFXUtils.fromFXImage(image, null)
//            val finalImage = scaleImage(bufferedImage, scene.width, scene.height)
//            val newBufferedImage = BufferedImage(bufferedImage.width, bufferedImage.height, BufferedImage.TYPE_INT_ARGB)
//            val graphics = bufferedImage.createGraphics()
//            graphics.setRenderingHint(
//                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON
//            )
//            graphics.drawImage(bufferedImage, 0, 0, null)
//            graphics.dispose()
            val file = File("whiteboard.png")
            ImageIO.write(bufferedImage, "png", file)
        }
    }

    private fun scaleImage(image: BufferedImage, width: Double, height: Double): BufferedImage {
        val scaledImage = BufferedImage(width.toInt(), height.toInt(), BufferedImage.TYPE_INT_ARGB)
        val g = scaledImage.createGraphics()
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC)
        g.drawImage(image, 0, 0, width.toInt(), height.toInt(), null)
        g.dispose()
        return scaledImage
    }

    private fun fileControllers(
        save: (filename: String) -> Unit,
        load: (filename: String) -> Unit,
        stage: Stage
    ) {
        fileNew.setOnAction {
            val inputDialog = TextInputDialog()
            inputDialog.headerText = "Enter New Board name:"
            val result = inputDialog.showAndWait()
            result.ifPresent { fileName ->
                println("New file name: $fileName")
                try {
                    println(wb.backend.createBoard(fileName, ""))
                    println(wb.backend.Blogin(fileName, ""))
                    wb.backend.boardname = fileName
                    updateTitle(stage)
                    wb.rootcanvas.children.clear()
                } catch (e: Exception) {
                    showWarnDialog("Error", e.toString())
                }
            }
        }

        fileSave.setOnAction {
            val choiceDialog = ChoiceDialog("local", "local", "remote ${wb.backend.boardname}")
            choiceDialog.headerText = "Select save location:"
            val result = choiceDialog.showAndWait()
            result.ifPresent { location ->
                if (location == "local") {
                    save("data.json")
                } else {
                    save("remote")
                }
            }
        }

        fileLoad.setOnAction {
            val choices = listOf("local", "remote ${wb.backend.boardname}")
            val choiceDialog = ChoiceDialog(choices[0], choices)
            choiceDialog.headerText = "Select file to load:"
            val result = choiceDialog.showAndWait()
            result.ifPresent { selection ->
                if (selection == "local") {
                    load("data.json")
                } else {
                    load("remote")
                }
                println("Load selection: $selection")
            }
        }

        fileOpen.setOnAction {
            var listOfBoards = wb.backend.getBoards()
            val optionsList = if (listOfBoards.isNullOrEmpty()) {
                listOf("N/A")
            } else {
                listOfBoards.map { it.second }
            }
            val choiceDialog = ChoiceDialog<String>(optionsList.firstOrNull(), optionsList)
            choiceDialog.headerText = "Select which remote board:"
            choiceDialog.contentText = "Board options:"
            val result = choiceDialog.showAndWait()
            result.ifPresent { location ->
                val selectedPair = listOfBoards.find { it.second == location }
                if (selectedPair != null) {
                    wb.backend.boardname = location
                    wb.backend.boardId = selectedPair.first
                    println(wb.backend.Blogin(location, ""))
                    wb.rootcanvas.children.clear()
                    load("remote")
                    updateTitle(stage)
                }
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

    private fun updateTitle(stage: Stage) {
        stage.titleProperty().bind(
            SimpleStringProperty(
                "WhiteBoard    - ${
                    if (wb.backend.boardname != "")
                        "Remote Board: ${wb.backend.boardname}"
                    else
                        "Local Board"
                }     - ${
                    if (wb.backend.username != "")
                        "Logged In: ${wb.backend.username}"
                    else
                        "Not Logged In"
                }"
            )
        )
    }
}
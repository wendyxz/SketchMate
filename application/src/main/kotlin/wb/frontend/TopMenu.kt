package wb.frontend

import com.itextpdf.text.Document
import com.itextpdf.text.Rectangle
import com.itextpdf.text.pdf.PdfWriter
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.embed.swing.SwingFXUtils
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.layout.GridPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.util.Callback
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import wb.backend.createBoard
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.util.*
import javax.imageio.ImageIO
import kotlin.concurrent.timerTask


fun exportPNG(stage: Stage) {
    val image = stage.scene.snapshot(null)
    val bufferedImage = SwingFXUtils.fromFXImage(image, null)
    val file = File("whiteboard.png")
    ImageIO.write(bufferedImage, "png", file)
}

fun exportPDF(stage: Stage) {
        val file = File("whiteboard.pdf")
        val pageSize = Rectangle(stage.scene.width.toFloat(), stage.scene.height.toFloat())
        val document = Document(pageSize)
        val writer = PdfWriter.getInstance(document, FileOutputStream(file))
        document.open()
        document.newPage()
        val cb = writer.directContent
        val template = cb.createTemplate(stage.scene.width.toFloat(), stage.scene.height.toFloat())
        val graphics = template.createGraphics(stage.scene.width.toDouble().toFloat(), stage.scene.height.toDouble().toFloat())
        graphics.background = java.awt.Color.WHITE
        stage.scene.root.snapshot(null, null)?.let {
            val image = SwingFXUtils.fromFXImage(it, null)
            graphics.drawImage(image, 0, 0, null)
        }
        graphics.dispose()
        cb.addTemplate(template, 0f, 0f)
        document.close()
}

//this is from the register dialog box
class Credential(val username: String, val password: String)
class VerifyCredential(val username: String, val password: String, val verifyPassword: String)

class TopMenu(stage: Stage) : MenuBar() {

//    var rightLabel = Label("Logged in: ${wb.backend.username}")

    // Menu choices
    private val fileMenu = Menu("File")
    private val editMenu = Menu("Edit")
    private val helpMenu = Menu("Help")
    private val accountMenu = Menu("Account")
    private val themeMenu = Menu("Theme")

    // File sub-menu
    private val fileNew = MenuItem("New Remote Board")
    private val fileOpen = MenuItem("Open Remote Board")
    private val fileLocal = MenuItem("Return to Local Board")
    private val fileSave = MenuItem("Save")
    private val fileLoad = MenuItem("Load")
    private val fileExPNG = MenuItem("Export as PNG")
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
        fileMenu.items.addAll(fileNew, fileOpen, fileSave, fileLoad, fileExPNG, fileExPDF, fileQuit, fileLocal)
        editMenu.items.addAll(editUndo, editRedo, editCut, editCopy, editPaste)
        helpMenu.items.addAll((helpAbout))
        accountMenu.items.addAll(accountLogOut, accountChangeP)
        themeMenu.items.addAll(lightTheme, darkTheme)
        darkTheme.setOnAction { setBackgroundColour(Color.BLACK) }
        lightTheme.setOnAction { setBackgroundColour(Color.WHITE) }

        registerControllers(stage)
        fileControllers(stage)
        fileExPNG.setOnAction { exportPNG(stage) }
        fileExPDF.setOnAction { exportPDF(stage) }
//        exportPNG(stage)
//        exportPDF(stage)

        menus.addAll(fileMenu, editMenu, helpMenu, accountMenu, themeMenu)

        val autoSync: Timer = Timer()
        autoSync.scheduleAtFixedRate(timerTask() {
            Platform.runLater {
                load()
            }
        }, 100, 100)
    }


    private fun fileControllers(
        stage: Stage
    ) {
        fileNew.setOnAction {
            val inputDialog = TextInputDialog()
            inputDialog.headerText = "Enter New Board name:"
            val result = inputDialog.showAndWait()
            result.ifPresent { fileName ->
                println("New board name: $fileName")
                try {
                    wb.rootcanvas.children.clear()
                    save()
                    var jsonname = "${wb.backend.username}_${wb.backend.boardname}_data.json"
                    val file = File(jsonname)
                    val reader = BufferedReader(FileReader(file))
                    var data = reader.readText()
//                    data = Json.encodeToString(data).replace("\\", "")
//                    data = Json.encodeToString(data).replace("\"", "\\\"")
                    data = Json.encodeToString(data).replace("\\", "").replace("\"", "\\\"")
                    println("!!!!!!!!!!!!!!!!!!!!!!!!!")
                    println("!!!!!!!!!!!!!!!!!!!!!!!!!")
                    println(data)
                    reader.close()
                    println(wb.backend.createBoard(fileName, data))
                    println(wb.backend.Blogin(fileName, ""))
                    wb.backend.boardname = fileName
                    updateTitle(stage)
                } catch (e: Exception) {
                    showWarnDialog("Error", e.toString())
                }
            }
        }

        fileLocal.setOnAction {
            showWarnDialog("message", wb.backend.Blogout())
            wb.backend.boardname = ""
            wb.backend.boardname = ""
            wb.backend.json = ""
            wb.backend.cookieValueB = ""
            updateTitle(stage)
        }

        fileSave.setOnAction {
            val choiceDialog = ChoiceDialog("local", "local", "remote ${wb.backend.boardname}")
            choiceDialog.headerText = "Select save location:"
            val result = choiceDialog.showAndWait()
            result.ifPresent { location ->
                if (location == "local") {
                    save()
                } else {
                    save()
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
                    load()
                } else {
                    load()
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
                    load()
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
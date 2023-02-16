package wb.frontend

import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.paint.Color


class TopMenu(setBackgroundColour: (color: Color) -> Unit) : MenuBar() {
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
        fileMenu.items.addAll(fileNew, fileOpen, fileSave, fileExport, fileExPDF, fileQuit)
        editMenu.items.addAll(editUndo, editRedo, editCut, editCopy, editPaste)
        helpMenu.items.addAll((helpAbout))
        accountMenu.items.addAll(accountLogIn, accountLogOut, accountCreate, accountChangeP)

        themeMenu.items.addAll(lightTheme, darkTheme)
        darkTheme.setOnAction {setBackgroundColour(Color.BLACK) }
        lightTheme.setOnAction {setBackgroundColour(Color.WHITE) }

        menus.addAll(fileMenu, editMenu, helpMenu, accountMenu, themeMenu)
    }

}
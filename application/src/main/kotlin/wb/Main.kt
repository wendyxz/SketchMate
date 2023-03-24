package wb

import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import javafx.application.Application
import javafx.geometry.*
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.PasswordField
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Path
import javafx.scene.shape.Rectangle
import javafx.scene.text.Text
import javafx.stage.Stage
import kotlinx.serialization.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import wb.frontend.*
import java.io.*
import java.util.*
import kotlin.concurrent.timerTask

@Serializable
class TypeWrapper(val type: String, val string: String)

var root = BorderPane()
val rootcanvas = Pane()
var autoLoad = true
var counterForTextBox = -1

// Serializer/Deserializer
val objectMapper = jacksonObjectMapper().registerModule(SimpleModule()
    .addSerializer(Rectangle::class.java, RectangleSerializer())
    .addDeserializer(Rectangle::class.java, RectangleDeserializer())
    .addSerializer(Circle::class.java, CircleSerializer())
    .addDeserializer(Circle::class.java, CircleDeserializer())
    .addSerializer(Path::class.java, PathSerializer())
    .addDeserializer(Path::class.java, PathDeserializer())
    .addSerializer(VBox::class.java, TextSerializer())
    .addDeserializer(VBox::class.java, TextDeserializer()))

fun save(filename: String) {
    // we need to write smth like:
    // val data = serializeCanvas(rootcanvas)
    val elements = mutableListOf<String>()
    for (element in rootcanvas.children) {
        // println(element)
        when (element) {
            is Rectangle -> elements.add(Json.encodeToString(
                TypeWrapper("Rectangle", objectMapper.writeValueAsString(element))))
            is Circle -> elements.add(Json.encodeToString(
                TypeWrapper("Circle", objectMapper.writeValueAsString(element))))
            is Path -> elements.add(Json.encodeToString(
                TypeWrapper("Path", objectMapper.writeValueAsString(element))))
            is VBox ->
                    elements.add(Json.encodeToString(
                        TypeWrapper("VBox", objectMapper.writeValueAsString(element))))
            else -> print("not defined")
        }
    }

    val file = File(filename)
    val writer = BufferedWriter(FileWriter(file))
    writer.write(objectMapper.writeValueAsString(elements))
    writer.close()
    // print("done")
    autoLoad = true
}
fun load(filename: String) {
    if(!autoLoad) return

    val file = File(filename)
    val reader = BufferedReader(FileReader(file))
    val data = reader.readText()
    reader.close()
    // print("reader closed")
    // we need smth like:
    // unserializeCanvas(data, rootcanvas)
    // to replace the bottom section

    counterForTextBox = (counterForTextBox+1)%10
    if(counterForTextBox == 0)
        rootcanvas.children.removeAll(rootcanvas.children)
    else
        rootcanvas.children.removeAll(rootcanvas.children.filter { it !is VBox })

    var elements = Json.decodeFromString<List<String>?>(data)
    if (elements != null) {
        for (wrapper in elements) {
            var element = Json.decodeFromString<TypeWrapper>(wrapper)
            when (element.type) {
                "Rectangle" -> rootcanvas.children.add(objectMapper.readValue(element.string, Rectangle::class.java))
                "Circle" -> rootcanvas.children.add(objectMapper.readValue(element.string, Circle::class.java))
                "Path" -> root.children.add(objectMapper.readValue(element.string, Path::class.java))
                "VBox" -> {
                    if(counterForTextBox == 0)
                        rootcanvas.children.add(objectMapper.readValue(element.string, VBox::class.java))
                }
                else -> print("otherwise")
            }
        }
    }


    // println("decoded")
}

class Main : Application() {
    private var backgroundFill = BackgroundFill(Color.WHITE, null, null)
    private var background = Background(backgroundFill)
    private var shapeTools = ShapeTools(rootcanvas)
    private var textTools = TextTools(rootcanvas)
    private var pathTools = PathTools(rootcanvas)
    
    override fun start(stage: Stage) {
        stage.title = "WhiteBoard"
        stage.minWidth = 480.0
        stage.minHeight = 320.0
        val loginButton = Button("Log in")



        loginPage(stage, loginButton)
        loginButton.setOnMouseClicked{
            pathTools = PathTools(rootcanvas)
            root.center = rootcanvas
            root.top = TopMenu(::setBackgroundColour, ::save, ::load)
            root.left = ToolMenu(::setCursorType, pathTools.getPenTools())
            rootcanvas.background = background
            stage.scene = Scene(root, 800.0, 600.0)
            pathTools.setScale(stage.scene)
            shapeTools = ShapeTools(rootcanvas)
            shapeTools.setScale(stage.scene)
            textTools.setScale(stage.scene)
        }
    }

    private fun loginPage(stage: Stage, loginButton: Button) {
        var login = VBox()
        val title = Text("User Login / Sign up")
        val registerButton = Button("Sign up")
        login.alignment = Pos.CENTER
        login.spacing = 20.0
        val username = HBox()
        username.alignment = Pos.CENTER
        username.spacing = 10.0
        val usernameLabel = Label("Username:")
        val usernameField = TextField()
        username.getChildren().addAll(usernameLabel, usernameField)
        val password = HBox()
        password.spacing = 12.0
        password.alignment = Pos.CENTER
        val passwordLabel = Label("Password:")
        val passwordField = PasswordField()
        password.getChildren().addAll(passwordLabel, passwordField)
        val buttons = HBox()
        buttons.spacing = 30.0
        buttons.alignment = Pos.CENTER
        buttons.getChildren().addAll(loginButton, registerButton)
        login.getChildren().addAll(title, username, password, buttons)
        stage.scene = Scene(login, 800.0, 600.0)
        stage.show()
    }

    private fun setCursorType(ctype: CursorType) {
        cursorType = ctype
        when (cursorType) {
            CursorType.cursor -> pathTools.cancelPath()
            CursorType.textbox -> textTools.createTextBox()
            CursorType.pen -> pathTools.initPath()
            CursorType.rectangle -> {
                pathTools.cancelPath()
                shapeTools.createRectangle()
            }

            CursorType.circle -> {
                pathTools.cancelPath()
                shapeTools.createCircle()
            }

            CursorType.eraser -> {
                pathTools.initPath()
                //pathTools.cancelPath()
                // shapeTools.createRectangle()
            };
        }
    }

    private fun setBackgroundColour(color: Color) {
        rootcanvas.background = Background(BackgroundFill(color, null, null))
    }
}


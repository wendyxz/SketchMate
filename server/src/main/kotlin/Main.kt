import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

data class User(val id: Int, val username: String, val password: String)

class Database {
    private val connection: Connection

    init {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite:users.db")
    }

    fun registerUser(username: String, password: String): Boolean {
        return try {
            val statement = connection.createStatement()
            val query = "INSERT INTO users (username, password) VALUES ('$username', '$password')"
            statement.executeUpdate(query)
            true
        } catch (e: ClassNotFoundException) {
            false
        } catch (e: SQLException) {
            false
        }
        return false
    }

    fun getUser(username: String): User? {
        val statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?")
        statement.setString(1, username)
        val resultSet = statement.executeQuery()
        if (resultSet.next()) {
            return User(
                resultSet.getInt("id"),
                resultSet.getString("username"),
                resultSet.getString("password")
            )
        }
        return null
    }

    fun close() {
        try {
            connection.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }
}

class UserSession(var user: User? = null)

fun main() {
    val db = Database()
    val session = UserSession()

//    var input = -1
    while (true) {
        // Display menu options
        println("1. Register")
        println("2. Login")
        println("3. Logoff")
        println("4. Exit")

        // Prompt user for input
        println("Enter option number: ")
        val input = readLine()?.toIntOrNull() ?: continue

        when (input) {
            1 -> {
                // Register
                print("Enter username: ")
                val username = readLine() ?: continue
                print("Enter password: ")
                val password = readLine() ?: continue
                val success = db.registerUser(username, password)
                if (success) {
                    println("User registered successfully")
                } else {
                    println("User registration failed")
                }
            }

            2 -> {
                // Login
                print("Enter username: ")
                val username = readLine() ?: continue
                print("Enter password: ")
                val password = readLine() ?: continue
                val user = db.getUser(username)
                if (user != null && user.password == password) {
                    session.user = user
                    println("User logged in: ${user.username}")
                } else {
                    println("Invalid username or password")
                }
            }

            3 -> {
                // Logoff
                session.user = null
                println("User logged off")
            }

            4 -> {
                // Exit
                db.close()
                return
            }

            else -> {
                // Invalid option
                println("Invalid option")
            }
        }
    }


    db.close()
}

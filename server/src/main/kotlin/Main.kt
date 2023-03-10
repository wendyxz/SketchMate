//import org.jetbrains.exposed.sql.*
//import org.jetbrains.exposed.sql.transactions.transaction
//
//object Users : Table() {
//    val id = varchar("id", 10) // Column<String>
//    val name = varchar("name", length = 50) // Column<String>
//    val password = varchar("password", length = 50) // Column<String>
//
//    override val primaryKey = PrimaryKey(id, name = "PK_User_ID") // name is optional here
//}
//
//
//fun main() {
//
//    // this connection string will create on disk as `test.h2.db`
//    Database.connect("jdbc:h2:user", driver = "org.h2.Driver", user = "root", password = "")
//
//    transaction {
//        addLogger(StdOutSqlLogger)
//
//        SchemaUtils.drop (Users)
//
//        SchemaUtils.create (Users)
//
//
//        Users.insert {
//            it[id] = "andrey"
//            it[name] = "Andrey"
//            it[password] = "password"
//        }
//
//        Users.insert {
//            it[id] = "sergey"
//            it[name] = "Sergey"
//            it[password] = "password"
//        }
//
//        Users.insert {
//            it[id] = "eugene"
//            it[name] = "Eugene"
//            it[password] = "password"
//        }
//
//        Users.insert {
//            it[id] = "alex"
//            it[name] = "Alex"
//            it[password] = "password"
//        }
//
//        Users.insert {
//            it[id] = "smth"
//            it[name] = "Something"
//            it[password] = "password"
//        }
//
//        Users.update({ Users.id eq "alex"}) {
//            it[name] = "Alexey"
//        }
//
//        println("All Users:")
//
//        for (user in Users.selectAll()) {
//            println("${user[Users.id]}: ${user[Users.name]}, ${user[Users.name]}")
//        }
//
//    }
//}

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

    // Logoff
    val username = "example_user"
    val password = "password123"
    val success = db.registerUser(username, password)
    if (success) {
        println("User registered successfully")
    } else {
        println("User registration failed")
    }

    // Login
    val user = db.getUser(username)
    if (user != null && user.password == password) {
        session.user = user
        println("User logged in: ${user.username}")
    } else {
        println("Invalid username or password")
    }

    // Logoff
    session.user = null
    println("User logged off")

    db.close()
}


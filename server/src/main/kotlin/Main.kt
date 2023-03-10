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

data class User(val id: Int, val username: String, val password: String)

class Database {
    private val connection: Connection

    init {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite:users.db")
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

    fun addUser(id: String, username: String, password: String) {
        val statement = connection.prepareStatement("INSERT INTO users(username, password) VALUES (?, ?)")
//        statement.setString(1, id)
        statement.setString(1, username)
        statement.setString(2, password)
        statement.executeUpdate()
    }

    fun close() {
        connection.close()
    }
}

class UserSession(var user: User? = null)

fun main() {
    val db = Database()
    val session = UserSession()

    // Login
    val username = "john"
    val password = "password"
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


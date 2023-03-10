import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table() {
    val id = varchar("id", 10) // Column<String>
    val name = varchar("name", length = 50) // Column<String>
    val password = varchar("password", length = 50) // Column<String>

    override val primaryKey = PrimaryKey(id, name = "PK_User_ID") // name is optional here
}


fun main() {

    // this connection string will create on disk as `test.h2.db`
    Database.connect("jdbc:h2:user", driver = "org.h2.Driver", user = "root", password = "")

    transaction {
        addLogger(StdOutSqlLogger)

        SchemaUtils.drop (Users)

        SchemaUtils.create (Users)


        Users.insert {
            it[id] = "andrey"
            it[name] = "Andrey"
            it[password] = "password"
        }

        Users.insert {
            it[id] = "sergey"
            it[name] = "Sergey"
            it[password] = "password"
        }

        Users.insert {
            it[id] = "eugene"
            it[name] = "Eugene"
            it[password] = "password"
        }

        Users.insert {
            it[id] = "alex"
            it[name] = "Alex"
            it[password] = "password"
        }

        Users.insert {
            it[id] = "smth"
            it[name] = "Something"
            it[password] = "password"
        }

        Users.update({ Users.id eq "alex"}) {
            it[name] = "Alexey"
        }

        println("All Users:")

        for (user in Users.selectAll()) {
            println("${user[Users.id]}: ${user[Users.name]}, ${user[Users.name]}")
        }

    }
}

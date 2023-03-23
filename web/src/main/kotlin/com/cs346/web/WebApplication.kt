package com.cs346.web

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import java.util.*
import org.springframework.jdbc.core.query
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository


@SpringBootApplication
class WebApplication(@Autowired val jdbcTemplate: JdbcTemplate): CommandLineRunner{
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger
                = getLogger(ServerApplication::class.java)
    }
    override fun run(vararg args:String?) {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id String primary key AUTOINCREMENT，name VARCHAR(20), password VARCHAR(30);")
        println("Yes")
    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}

@RestController
class MessageController(val service: MessageService) {
    @GetMapping("/")
    fun index(): List<Message> = service.findMessages()

    @GetMapping("/{id}")
    fun index(@PathVariable id: String): List<Message> =
        service.findMessageById(id)

    @PostMapping("/")
    fun post(@RequestBody message: Message) {
        service.save(message)
    }
}



@Service
class MessageService(val db: MessageRepository) {
    fun findMessages(): List<Message> = db.findAll().toList()

    fun findMessageById(id: String): List<Message> = db.findById(id).toList()

    fun save(message: Message) {
        db.save(message)
    }

    fun <T : Any> Optional<out T>.toList(): List<T> =
        if (isPresent) listOf(get()) else emptyList()
}



@Table("MESSAGES")
data class Message(@Id var id: String?, val text: String)


@Repository
interface MessageRepository : CrudRepository<Message, String>
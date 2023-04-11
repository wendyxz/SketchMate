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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.slf4j.LoggerFactory.getLogger


@SpringBootApplication
class WebApplication(@Autowired val jdbcTemplate: JdbcTemplate) : CommandLineRunner {
    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        private val logger = getLogger(WebApplication::class.java)
    }

    override fun run(vararg args: String?) {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS users (id text PRIMARY KEY, name VARCHAR(20), password VARCHAR(30));")
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS boards (id text PRIMARY KEY, name VARCHAR(20), json TEXT);")
    }
}

fun main(args: Array<String>) {
    runApplication<WebApplication>(*args)
}

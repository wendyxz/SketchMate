package com.cs346.web.user

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.sql.Array
import java.sql.Connection
import java.util.*

@Repository
class UserDAOImpl(val jdbcTemplate: JdbcTemplate) : UserDAO {

    override fun getAllUsers(): List<User>? {
        var rowMapper: RowMapper<User> = RowMapper<User> { resultSet: ResultSet, _: Int ->
            User(resultSet.getString("id"), resultSet.getString("name"), null)
        }
        val sql = "SELECT * FROM users"
        var results = jdbcTemplate.query(sql, rowMapper)
        return results
    }

    override fun findUser(id: String): User? {
        var rowMapper: RowMapper<User> = RowMapper<User> { resultSet: ResultSet, _: Int ->
            User(resultSet.getString("id"), resultSet.getString("name"), null)
        }
        val sql = "SELECT id, name, password FROM users WHERE id=?"
        var result = jdbcTemplate?.queryForObject(sql, rowMapper, id)
        return result
    }

    override fun login(name: String): User? {
        var rowMapper: RowMapper<User> = RowMapper<User> { resultSet: ResultSet, _: Int ->
            User(resultSet.getString("id"), resultSet.getString("name"), resultSet.getString("password"))
        }
        val sql = "SELECT id, name, password FROM users WHERE name=? limit 1"
        var result = jdbcTemplate?.queryForObject(sql, rowMapper, name)
        return result
    }

    override fun createUser(user: User): Int? {
        val sql = "INSERT INTO users(id, name, password) VALUES(?,?,?)"
        return jdbcTemplate?.update(sql, UUID.randomUUID().toString(), user.name, user.password)
    }

    override fun updateUser(id: String, user: User): Int? {
        val sql = "UPDATE users SET password=? WHERE id=?"
        return jdbcTemplate?.update(sql, user.password, id)
    }

    override fun deleteUser(id: String): Int? {
        val sql = "DELETE FROM users WHERE id=?"
        return jdbcTemplate?.update(sql, id)
    }
}

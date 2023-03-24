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
            User(resultSet.getInt("id"), resultSet.getString("name"), null)
        }
        val sql = "SELECT * FROM users"
        var results = jdbcTemplate.query(sql, rowMapper)
        return results
    }

    override fun findUser(id: Int): User? {
        var rowMapper: RowMapper<User> = RowMapper<User> { resultSet: ResultSet, _: Int ->
            User(resultSet.getInt("id"), resultSet.getString("name"), null)
        }
        val sql = "SELECT id, name, password FROM users WHERE id=?"
        var result = jdbcTemplate?.queryForObject(sql, rowMapper, id)
        return result
    }

    override fun login(name: String): User? {
        var rowMapper: RowMapper<User> = RowMapper<User> { resultSet: ResultSet, _: Int ->
            User(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("password"))
        }
        val sql = "SELECT id, name, password FROM users WHERE name=?"
        var result = jdbcTemplate?.queryForObject(sql, rowMapper, name)
        return result
    }

    override fun createUser(user: User): Int? {
        val sql = "INSERT INTO users(name,password) VALUES(?,?)"
        return jdbcTemplate?.update(sql, user.name, user.password)
    }

    override fun updateUser(id: Int, user: User): Int? {
        val sql = "UPDATE users SET name=?, password=? WHERE id=?"
        return jdbcTemplate?.update(sql, user.name, user.password, id)
    }

    override fun deleteUser(id: Int): Int? {
        val sql = "DELETE FROM users WHERE id=?"
        return jdbcTemplate?.update(sql, id)
    }
}

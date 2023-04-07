package com.cs346.web.board

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet
import java.util.*

@Repository
class BoardDAOImpl(val jdbcTemplate: JdbcTemplate) : BoardDAO {

//    override fun getAllBoards(): List<Board>? {
//        var rowMapper: RowMapper<Board> = RowMapper<Board> { resultSet: ResultSet, _: Int ->
//            Board(
//                resultSet.getString("id"),
//                resultSet.getString("name"), resultSet.getString("json")
//            )
//        }
//        val sql = "SELECT name FROM boards"
//        var results = jdbcTemplate.query(sql, rowMapper)
//        return results
//    }

    override fun getAllBoards(): List<Pair<String, String>>? {
        val sql = "SELECT id, name FROM boards"
        return jdbcTemplate?.query(sql) { rs, _ ->
            rs.getString("id") to rs.getString("name")
        }
    }

    override fun findBoard(id: String): String? {
//        var rowMapper: RowMapper<String> = RowMapper<String> { resultSet: ResultSet, _: Int ->
//            String
//        }
        val sql = "SELECT json FROM boards WHERE id=?"
        return jdbcTemplate?.queryForObject(sql, String::class.java, id)
    }

    override fun login(name: String): Board? {
        var rowMapper: RowMapper<Board> = RowMapper<Board> { resultSet: ResultSet, _: Int ->
            Board(resultSet.getString("id"), resultSet.getString("name"),
                resultSet.getString("json"))
        }
        val sql = "SELECT id, name, json FROM boards WHERE name=? limit 1"
        var result = jdbcTemplate?.queryForObject(sql, rowMapper, name)
        return result
    }

    override fun createBoard(board: Board): Int? {
        val sql = "INSERT INTO boards(id, name, json) VALUES(?,?,?)"
        return jdbcTemplate?.update(sql, UUID.randomUUID().toString(), board.name, board.json)
    }

    override fun updateBoard(id: String, board: LoginDTO): Int? {
        val sql = "UPDATE boards SET json=? WHERE id=?"
        return jdbcTemplate?.update(sql, board.json, id)
    }

    override fun deleteBoard(id: String): Int? {
        val sql = "DELETE FROM boards WHERE id=?"
        return jdbcTemplate?.update(sql, id)
    }
}

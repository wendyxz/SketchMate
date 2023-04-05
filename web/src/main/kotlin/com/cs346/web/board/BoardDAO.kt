package com.cs346.web.board

interface BoardDAO {

    fun getAllBoards(): List<Board>?

    fun findBoard(id: String): String?

    fun login(name: String): Board?

    fun createBoard(book: Board): Int?

    fun updateBoard(id: String, book: Board): Int?

    fun deleteBoard(id: String): Int?
}

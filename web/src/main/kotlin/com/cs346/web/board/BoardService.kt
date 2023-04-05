package com.cs346.web.board

interface BoardService {

    fun getAllBoards(): List<Board>?

    fun findBoard(id: String): Board?

    fun login(name: String): Board?

    fun createBoard(board: Board): Int?

    fun updateBoard(id: String, board: Board): Int?

    fun deleteBoard(id: String): Int?
}

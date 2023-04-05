package com.cs346.web.board

import org.springframework.stereotype.Service

@Service
class BoardServiceImpl(val BoardDao: BoardDAO) : BoardService {
    override fun getAllBoards(): List<Board>? {
        return BoardDao.getAllBoards()
    }

    override fun findBoard(id: String): String? {
        return BoardDao.findBoard(id)
    }

    override fun login(name: String): Board? {
        return BoardDao.login(name)
    }

    override fun createBoard(board: Board): Int? {
        return BoardDao.createBoard(board)
    }

    override fun updateBoard(id: String, board: Board): Int? {
        return BoardDao.updateBoard(id, board)
    }

    override fun deleteBoard(id: String): Int? {
        return BoardDao.deleteBoard(id)
    }
}

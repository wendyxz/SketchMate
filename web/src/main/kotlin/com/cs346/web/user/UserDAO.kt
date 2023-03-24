package com.cs346.web.user

interface UserDAO {

    fun getAllUsers(): List<User>?

    fun findUser(id: Int): User?

    fun login(name: String): User?

    fun createUser(book: User): Int?

    fun updateUser(id: Int, book: User): Int?

    fun deleteUser(id: Int): Int?
}

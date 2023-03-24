package com.cs346.web.user

interface UserDAO {

    fun getAllUsers(): List<User>?

    fun findUser(id: String): User?

    fun login(name: String): User?

    fun createUser(book: User): Int?

    fun updateUser(id: String, book: User): Int?

    fun deleteUser(id: String): Int?
}

package com.cs346.web.user

interface UserService {

    fun getAllUsers(): List<User>?

    fun findUser(id: String): User?

    fun login(name: String): User?

    fun createUser(User: User): Int?

    fun updateUser(id: String, user: User): Int?

    fun deleteUser(id: String): Int?
}

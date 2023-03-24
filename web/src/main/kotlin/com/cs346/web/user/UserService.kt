package com.cs346.user

interface UserService {

    fun getAllUsers(): List<User>?

    fun findUser(id: Int): User?

    fun login(name: String): User?

    fun createUser(User: User): Int?

    fun updateUser(id: Int, user: User): Int?

    fun deleteUser(id: Int): Int?
}

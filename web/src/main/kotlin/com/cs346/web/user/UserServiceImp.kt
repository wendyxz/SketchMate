package com.cs346.web.user

import org.springframework.stereotype.Service

@Service
class UserServiceImpl(val UserDao: UserDAO) : UserService {
    override fun getAllUsers(): List<User>? {
        return UserDao.getAllUsers()
    }

    override fun findUser(id: Int): User? {
        return UserDao.findUser(id)
    }

    override fun login(name: String): User? {
        return UserDao.login(name)
    }

    override fun createUser(user: User): Int? {
        return UserDao.createUser(user)
    }

    override fun updateUser(id: Int, User: User): Int? {
        return UserDao.updateUser(id, User)
    }

    override fun deleteUser(id: Int): Int? {
        return UserDao.deleteUser(id)
    }
}

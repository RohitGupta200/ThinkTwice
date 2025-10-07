package com.app.thinktwice.database.repository

import com.app.thinktwice.database.User
import com.app.thinktwice.database.dao.UserDao
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun createUser(username: String, email: String, firstName: String, lastName: String): Result<Long> {
        return try {
            // Check if username already exists
            val existingUser = userDao.getByUsername(username)
            if (existingUser != null) {
                Result.failure(Exception("Username already exists"))
            } else {
                // Check if email already exists
                val existingEmail = userDao.getByEmail(email)
                if (existingEmail != null) {
                    Result.failure(Exception("Email already exists"))
                } else {
                    val userId = userDao.insert(username, email, firstName, lastName)
                    Result.success(userId)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserById(id: Long): User? {
        return userDao.getById(id)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getByUsername(username)
    }

    suspend fun getUserByEmail(email: String): User? {
        return userDao.getByEmail(email)
    }

    fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllFlow()
    }

    suspend fun updateUser(id: Long, username: String, email: String, firstName: String, lastName: String): Result<Unit> {
        return try {
            // Check if the new username is already taken by another user
            val existingUser = userDao.getByUsername(username)
            if (existingUser != null && existingUser.id != id) {
                Result.failure(Exception("Username already exists"))
            } else {
                // Check if the new email is already taken by another user
                val existingEmail = userDao.getByEmail(email)
                if (existingEmail != null && existingEmail.id != id) {
                    Result.failure(Exception("Email already exists"))
                } else {
                    userDao.update(id, username, email, firstName, lastName)
                    Result.success(Unit)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUser(id: Long): Result<Unit> {
        return try {
            userDao.delete(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserCount(): Long {
        return userDao.count()
    }

    suspend fun validateUser(username: String, email: String): List<String> {
        val errors = mutableListOf<String>()

        if (username.isBlank()) {
            errors.add("Username cannot be empty")
        }

        if (email.isBlank()) {
            errors.add("Email cannot be empty")
        } else if (!isValidEmail(email)) {
            errors.add("Invalid email format")
        }

        return errors
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$".toRegex()
        return emailRegex.matches(email)
    }
}
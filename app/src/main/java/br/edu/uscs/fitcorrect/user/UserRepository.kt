package br.edu.uscs.fitcorrect.user

import java.util.UUID

class UserRepository(private val userProfileDao: UserProfileDao) {
    suspend fun createUser(name: String, birthDate: String): UserProfile {
        val userId = generateUserId()
        val userProfile = UserProfile(id = userId, name = name, birthDate = birthDate)
        userProfileDao.insertUserProfile(userProfile)
        return userProfile
    }

    private fun generateUserId(): String {
        return UUID.randomUUID().toString()
    }

    suspend fun getUserById(userId: String): UserProfile? {
        return userProfileDao.getUserById(userId)
    }

    suspend fun getAllUsers(): List<UserProfile> {
        return userProfileDao.getAllUsers()
    }
}
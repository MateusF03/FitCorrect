package br.edu.uscs.fitcorrect.user

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM UserProfile WHERE id = :id")
    suspend fun getUserById(id: String): UserProfile?

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)

    @Delete
    suspend fun deleteUserProfile(userProfile: UserProfile)

    @Query("SELECT * FROM UserProfile")
    suspend fun getAllUsers(): List<UserProfile>
}
package br.edu.uscs.fitcorrect.users

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM usuarios WHERE login = :login")
    suspend fun getUserByLogin(login: String): User?
}
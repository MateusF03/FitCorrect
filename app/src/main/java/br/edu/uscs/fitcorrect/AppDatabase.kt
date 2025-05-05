package br.edu.uscs.fitcorrect

import androidx.room.Database
import androidx.room.RoomDatabase
import br.edu.uscs.fitcorrect.user.UserProfile
import br.edu.uscs.fitcorrect.user.UserProfileDao

@Database(entities = [UserProfile::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
}
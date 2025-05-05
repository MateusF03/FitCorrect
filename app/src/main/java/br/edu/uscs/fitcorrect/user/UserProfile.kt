package br.edu.uscs.fitcorrect.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProfile(
    @PrimaryKey val id: String,
    val name: String,
    val birthDate: String,
)


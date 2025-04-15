package br.edu.uscs.fitcorrect.users

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "usuarios")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val login: String,
    val senha: String
) {
    init {
        require(login.isNotBlank()) { "Login não pode ser vazio!" }
        require(senha.length >= 8) { "Senha deve ter no mínimo 8 caracteres!" }
    }
}
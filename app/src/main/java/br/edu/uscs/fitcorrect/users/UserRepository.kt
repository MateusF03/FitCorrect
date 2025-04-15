package br.edu.uscs.fitcorrect.users

import at.favre.lib.crypto.bcrypt.BCrypt
import br.edu.uscs.fitcorrect.users.UserDao
import br.edu.uscs.fitcorrect.users.User


class UserRepository(private val userDao: UserDao) {

    suspend fun registerUser(login: String, plainPassword: String) {
        val hashedPassword = BCrypt.withDefaults().hashToString(12, plainPassword.toCharArray())
        val user = User(login = login, senha = hashedPassword)
        userDao.insertUser(user)
    }

    suspend fun loginUser(login: String, plainPassword: String): Boolean {
        val user = userDao.getUserByLogin(login) ?: return false
        val result = BCrypt.verifyer().verify(plainPassword.toCharArray(), user.senha)
        return result.verified
    }
}
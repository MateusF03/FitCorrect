package br.edu.uscs.fitcorrect.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun registerUser(login: String, senha: String) {
        viewModelScope.launch(Dispatchers.IO) {
            userRepository.registerUser(login, senha)
        }
    }

    fun loginUser(login: String, senha: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = userRepository.loginUser(login, senha)
            onResult(success)
        }
    }
}
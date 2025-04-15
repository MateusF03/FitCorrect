package br.edu.uscs.fitcorrect.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.uscs.fitcorrect.users.AppDatabase
import br.edu.uscs.fitcorrect.users.UserRepository
import br.edu.uscs.fitcorrect.users.UserViewModel
import br.edu.uscs.fitcorrect.users.UserViewModelFactory

@Composable
fun LoginScreen(
    viewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(
            UserRepository(
                AppDatabase.getDatabase(LocalContext.current).userDao()
            )
        )
    )
) {
    val context = LocalContext.current
    var login by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("FitCorrect", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Login") },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = senha,
            onValueChange = { senha = it },
            label = { Text("Senha") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = {
            if (login.isBlank() || senha.isBlank()) {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.loginUser(login, senha) { success ->
                    if (success) {
                        Toast.makeText(context, "Login realizado!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }) {
            Text("Entrar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(onClick = {
            if (login.isBlank() || senha.isBlank()) {
                Toast.makeText(context, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.registerUser(login, senha)
                Toast.makeText(context, "Cadastro realizado!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("Cadastrar")
        }
    }
}
package br.edu.uscs.fitcorrect

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.registerForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.uscs.fitcorrect.ui.ExerciseAssistantScreen
import br.edu.uscs.fitcorrect.ui.LoginScreen
import br.edu.uscs.fitcorrect.ui.theme.FitCorrectTheme
import br.edu.uscs.fitcorrect.users.AppDatabase
import br.edu.uscs.fitcorrect.users.UserRepository
import br.edu.uscs.fitcorrect.users.UserViewModel
import br.edu.uscs.fitcorrect.users.UserViewModelFactory

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        // Permissão da câmera tratada aqui se necessário
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            FitCorrectTheme {
                val database = AppDatabase.getDatabase(this)
                val repository = UserRepository(database.userDao())
                val userViewModel: UserViewModel = viewModel(
                    factory = UserViewModelFactory(repository)
                )

                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    ExerciseAssistantScreen()
                } else {
                    LoginScreen(
                        userViewModel = userViewModel,
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
        }
    }
}
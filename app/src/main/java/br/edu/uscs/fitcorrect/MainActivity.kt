package br.edu.uscs.fitcorrect

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.edu.uscs.fitcorrect.ui.theme.FitCorrectTheme
import br.edu.uscs.fitcorrect.utils.CurrentUserManager

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)

        setContent {
            FitCorrectTheme {
                val navController = rememberNavController()
                val context = LocalContext.current

                // Launch once when the app starts
                LaunchedEffect(Unit) {
                    val userId = CurrentUserManager.getCurrentUserId(context)
                    if (userId != null) {
                        navController.navigate("exercise") {
                            popUpTo(0) // Remove splash or default
                        }
                    } else {
                        navController.navigate("profile_selector") {
                            popUpTo(0)
                        }
                    }
                }

                NavHost(
                    navController = navController,
                    startDestination = "splash" // Temporary placeholder
                ) {
                    composable("splash") {
                        // Show nothing or a loading screen while we check userId
                        Box(modifier = Modifier.fillMaxSize())
                    }
                    composable("profile_selector") {
                        ProfileSelectorScreen(navController)
                    }
                    composable("profile") {
                        ProfileScreen(navController)
                    }
                    composable("exercise") {
                        ExerciseValidationScreen(navController)
                    }
                    composable("session") {
                        ExerciseSessionScreen(navController = navController)
                    }
                    composable("results") {
                        SessionResultScreen(navController)
                    }
                }
            }
        }
    }
}

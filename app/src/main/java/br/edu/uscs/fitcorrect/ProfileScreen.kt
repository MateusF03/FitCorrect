package br.edu.uscs.fitcorrect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.room.Room
import br.edu.uscs.fitcorrect.user.UserProfile
import br.edu.uscs.fitcorrect.user.UserRepository
import br.edu.uscs.fitcorrect.utils.CurrentUserManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ProfileScreen(navController: NavController, userRepository: UserRepository = getUserRepo()) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("User Profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = birthDate,
            onValueChange = { birthDate = it },
            label = { Text("Birth Date (yyyy-mm-dd)") }
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            // Save user in coroutine
            CoroutineScope(Dispatchers.IO).launch {
                val user = userRepository.createUser(name, birthDate)
                withContext(Dispatchers.Main) {
                    message = "User ${user.name} created!"
                }
            }
        }) {
            Text("Save Profile")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(message)

        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = { navController.navigate("exercise") }) {
            Text("Back to Exercises")
        }
    }
}

@Composable
fun ProfileSelectorScreen(
    navController: NavController,
    userRepository: UserRepository = getUserRepo(),
    onUserSelected: (UserProfile) -> Unit = {}
) {
    val context = LocalContext.current
    var users by remember { mutableStateOf<List<UserProfile>>(emptyList()) }

    LaunchedEffect(Unit) {
        users = withContext(Dispatchers.IO) {
            userRepository.getAllUsers()
        }
    }

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Select a User", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))
        users.forEach { user ->
            Button(
                onClick = {
                    CurrentUserManager.saveCurrentUserId(context, user.id)
                    onUserSelected(user)
                    navController.navigate("exercise") {
                        popUpTo("profile_selector") { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text("${user.name} (Born ${user.birthDate})")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { navController.navigate("profile") }) {
            Text("Create New Profile")
        }
    }
}

@Composable
fun getUserRepo(): UserRepository {
    val context = LocalContext.current
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java, "fitcorrect-db"
    ).build()
    return UserRepository(db.userProfileDao())
}

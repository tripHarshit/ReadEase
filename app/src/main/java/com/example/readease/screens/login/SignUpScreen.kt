package com.example.readease.screens.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.readease.components.LoadingScreen
import com.example.readease.navigation.ReadEaseScreens

@Composable
fun SignUpScreen(navController: NavController, viewModel: LoginScreenViewModel = viewModel()) {
    val signUpState by viewModel.signUpState.collectAsState()

    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val emailError = rememberSaveable { mutableStateOf(false) }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordError = rememberSaveable { mutableStateOf(false) }

    when (signUpState) {
        is State.Idle, is State.Error -> {
            SignUpForm(
                email = email,
                password = password,
                passwordVisibility = passwordVisibility,
                emailError = emailError,
                passwordError = passwordError,
                navController = navController,
                viewModel = viewModel
            )
            if (signUpState is State.Error) {
                val errorMessage = (signUpState as State.Error).message
                Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
            }
        }

        is State.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingScreen()
            }
        }

        is State.Success -> {
            LaunchedEffect(Unit) {
                navController.navigate(ReadEaseScreens.ReaderHomeScreen.name) {
                    popUpTo(ReadEaseScreens.SignUpScreen.name) { inclusive = true }
                }
            }
        }
    }
}


@Composable
fun SignUpForm(
    email: MutableState<String>,
    password: MutableState<String>,
    passwordVisibility: MutableState<Boolean>,
    emailError: MutableState<Boolean>,
    passwordError: MutableState<Boolean>,
    navController: NavController,
    viewModel: LoginScreenViewModel
) {
    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceDim) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            Text(text = "Welcome to",
                color = MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp
                )
            AppTitle()
            Spacer(modifier = Modifier.height(100.dp))
            EmailField(email, emailError)
            PasswordField(password, passwordVisibility, passwordError)
            SignUpButton(email, password,emailError, passwordError, viewModel)
            LoginText(navController)
        }
    }
}

@Composable
fun SignUpButton(
    email: MutableState<String>,
    password: MutableState<String>,
    emailError: MutableState<Boolean>,
    passwordError: MutableState<Boolean>,
    viewModel: LoginScreenViewModel,
) {
    Button(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(50.dp)
            .alpha(.6f),
        onClick = {
            emailError.value = email.value.trim().isEmpty()
            passwordError.value = password.value.trim().isEmpty() || password.value.length < 6

            if (!emailError.value && !passwordError.value) {
                viewModel.signUpWithEmailAndPassword(email.value, password.value)
            }
        },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
        elevation = ButtonDefaults.elevatedButtonElevation(10.dp)
    ) {

            Text(
                text = "Create Account",
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 20.sp
            )
    }

}

@Composable
fun LoginText(navController: NavController) {
    Text(
        text = "Already have an account? Login",
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(16.dp)
            .clickable { navController.navigate(ReadEaseScreens.LoginScreen.name) }
    )
}

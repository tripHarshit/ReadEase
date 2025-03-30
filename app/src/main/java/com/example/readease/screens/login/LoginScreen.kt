package com.example.readease.screens.login

import android.graphics.Paint.Style
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.readease.R
import com.example.readease.components.LoadingScreen
import com.example.readease.navigation.ReadEaseScreens

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginScreenViewModel = viewModel()) {

    val loginState by viewModel.loginState.collectAsState()

    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val emailError = rememberSaveable { mutableStateOf(false) }
    val passwordError = rememberSaveable { mutableStateOf(false) }

    when (loginState) {
        is State.Idle, is State.Error -> {
            LoginForm(
                email = email,
                password = password,
                passwordVisibility = passwordVisibility,
                emailError = emailError,
                passwordError = passwordError,
                navController = navController,
                viewModel = viewModel
            )
            if (loginState is State.Error) {
                val errorMessage = (loginState as State.Error).message
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
                    popUpTo(ReadEaseScreens.LoginScreen.name) { inclusive = true }
                }
            }
        }
    }

}

@Composable
fun LoginForm(
    email: MutableState<String>,
    password: MutableState<String>,
    passwordVisibility: MutableState<Boolean>,
    emailError: MutableState<Boolean>,
    passwordError: MutableState<Boolean>,
    navController: NavController,
    viewModel: LoginScreenViewModel
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceDim
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(44.dp))
            AppTitle()
            Spacer(modifier = Modifier.height(100.dp))
            EmailField(email, emailError)
            PasswordField(password, passwordVisibility, passwordError)
            LoginButton(email, password, emailError, passwordError, viewModel)
            SignupText(navController)
        }
    }
}

@Composable
fun AppTitle() {
    Text(
        text = "ReadEase",
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(0.dp),
        style = MaterialTheme.typography.displayLarge
    )
}

@Composable
fun EmailField(email: MutableState<String>, emailError: MutableState<Boolean>) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = email.value,
            onValueChange = {
                email.value = it
                emailError.value = it.isEmpty()
            },
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = if (emailError.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (emailError.value) MaterialTheme.colorScheme.error.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            shape = RoundedCornerShape(20.dp),
            label = { Text(text = "Email Address") },
            singleLine = true,
            maxLines = 1,
            isError = emailError.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        if (emailError.value) {
            Text(
                text = "Email cannot be empty",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 24.dp, top = 2.dp)
            )
        }
    }
}

@Composable
fun PasswordField(password: MutableState<String>, passwordVisibility: MutableState<Boolean>, passwordError: MutableState<Boolean>) {
    val keyboardController = LocalSoftwareKeyboardController.current
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = password.value,
            onValueChange = {
                password.value = it
                passwordError.value = it.isEmpty() || it.length < 6
            },
            colors = OutlinedTextFieldDefaults.colors(
                cursorColor = MaterialTheme.colorScheme.primary,
                focusedBorderColor = if (passwordError.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (passwordError.value) MaterialTheme.colorScheme.error.copy(alpha = 0.6f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                errorBorderColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            label = { Text(text = "Password") },
            singleLine = true,
            visualTransformation = if (passwordVisibility.value) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                Icon(
                    painter = painterResource(
                        id = if (passwordVisibility.value) R.drawable.baseline_visibility_24
                        else R.drawable.baseline_visibility_off_24
                    ),
                    contentDescription = "Toggle Password Visibility",
                    modifier = Modifier.clickable {
                        passwordVisibility.value = !passwordVisibility.value
                    }
                )
            },
            maxLines = 1,
            isError = passwordError.value,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )
        if (passwordError.value) {
            Text(
                text = "Password must be at least 6 characters",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 24.dp, top = 2.dp)
            )
        }
    }
}


@Composable
fun LoginButton(
    email: MutableState<String>,
    password: MutableState<String>,
    emailError: MutableState<Boolean>,
    passwordError: MutableState<Boolean>,
    viewModel: LoginScreenViewModel
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
                viewModel.signInWithEmailAndPassword(email.value, password.value)
            }
        },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.tertiary),
        elevation = ButtonDefaults.elevatedButtonElevation(10.dp)
    ) {
        Text(
            text = "Login",
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier,
            fontSize = 20.sp,
        )
    }
}

@Composable
fun SignupText(navController: NavController) {
    Text(
        text = "Not a member? Signup",
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .padding(16.dp)
            .clickable { navController.navigate(ReadEaseScreens.SignUpScreen.name) }
    )
}

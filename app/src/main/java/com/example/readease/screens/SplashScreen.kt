package com.example.readease.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.readease.navigation.ReadEaseScreens
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreen(navController: NavController = NavController(LocalContext.current)) {
    val scale = remember { Animatable(0.5f) }
    val alpha = remember { Animatable(0f) }
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(1f, animationSpec = tween(1000, easing = EaseInOut))
        alpha.animateTo(1f, animationSpec = tween(1200))
        isVisible = true
        delay(2000L)

        val destination = if (FirebaseAuth.getInstance().currentUser?.email.isNullOrEmpty()) {
            ReadEaseScreens.LoginScreen.name
        } else {
            ReadEaseScreens.ReaderHomeScreen.name
        }
        navController.navigate(destination) {
            popUpTo(ReadEaseScreens.SplashScreen.name) { inclusive = true }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceDim
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .size(320.dp)
                    .scale(scale.value)
                    .alpha(alpha.value),
                shape = RoundedCornerShape(36),
                elevation = CardDefaults.cardElevation(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    AnimatedVisibility(visible = isVisible) {
                        Text(
                            text = "ReadEase",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    AnimatedVisibility(visible = isVisible) {
                        Text(
                            text = "\"Read. With. Ease\"",
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

package com.example.readease.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.readease.model.MUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class State {
    object Idle : State()
    object Success : State()
    data class Error(val message: String) : State()
    object Loading : State()
}

class LoginScreenViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<State>(State.Idle)
    val loginState: StateFlow<State> = _loginState.asStateFlow()

    private val _signUpState = MutableStateFlow<State>(State.Idle)
    val signUpState: StateFlow<State> = _signUpState.asStateFlow()

    private val auth: FirebaseAuth = Firebase.auth

    /**
     * Sign in existing user with email and password
     */
    fun signInWithEmailAndPassword(email: String, password: String) {
        _loginState.value = State.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _loginState.value = State.Success
                } else {
                    _loginState.value = State.Error(task.exception?.message ?: "Login failed")
                }
            }
            .addOnFailureListener { exception ->
                _loginState.value = State.Error(exception.message ?: "Login failed")
            }
    }

    /**
     * Register a new user with email and password
     */
    fun signUpWithEmailAndPassword(email: String, password: String) {
        _signUpState.value = State.Loading

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _signUpState.value = State.Success
                    val displayName = task.result?.user?.email?.split("@")?.get(0)
                    createUser(displayName)
                } else {
                    val errorMessage = task.exception?.message ?: " Sign-up failed"
                    _signUpState.value = State.Error(errorMessage)

                }
            }
            .addOnFailureListener { exception ->
                val errorMessage = exception.message ?: " Sign-up failed"
                _signUpState.value = State.Error(errorMessage)
            }
    }

    private fun createUser(displayName: String?) {
        val userId = auth.currentUser?.uid
        val user = MUser(id = null,
            userId = userId.toString(),
            displayName = displayName.toString(),
            avatarUrl = "").toMap()


        FirebaseFirestore.getInstance().collection("Users").add(user)
    }


    /**
     * Sign out the current user
     */
    fun signOut() {
        auth.signOut()
        _loginState.value = State.Idle
        _signUpState.value = State.Idle
    }
}

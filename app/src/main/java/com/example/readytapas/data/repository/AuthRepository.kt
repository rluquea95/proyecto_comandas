package com.example.readytapas.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

//Clase encargada de loguear y cerrar sesión
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    //Obtiene el usuario logueado (null en caso de que no haya)
    val currentUser get() = firebaseAuth.currentUser

    //Inicia sesión en Firebase con las credenciales del usuario
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Reestablece la contraseña del usuario
    suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    //Llama a la función predefinida de cerrar sesión de Firebase
    fun logout() {
        firebaseAuth.signOut()
    }
}

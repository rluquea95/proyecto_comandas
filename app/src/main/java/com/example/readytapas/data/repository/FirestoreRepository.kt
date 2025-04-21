package com.example.readytapas.data.repository

import com.example.readytapas.data.model.Producto
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun collection(name: String): CollectionReference {
        return firestore.collection(name)
    }

    suspend fun getCarta(): List<Producto> {
        return try {
            firestore.collection("Carta")
                .get()
                .await()
                .mapNotNull { it.toObject(Producto::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
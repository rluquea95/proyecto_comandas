package com.example.readytapas.data.repository

import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.Pedido
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

    suspend fun getMesas(): List<Mesa> {
        return try {
            firestore.collection("Mesas")
                .get()
                .await()
                .mapNotNull { it.toObject(Mesa::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun crearPedido(pedido: Pedido) {
        try {
            firestore.collection("Pedidos")
                .add(pedido)
                .await()
        } catch (e: Exception) {
            // Podrías manejar el error si quieres
        }
    }

    suspend fun actualizarMesa(mesa: Mesa) {
        try {
            firestore.collection("Mesas")
                .whereEqualTo("name", mesa.name.name) // name es un enum, hay que usar name
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference
                ?.set(mesa)
                ?.await()
        } catch (e: Exception) {
            // Podrías manejar el error si quieres
        }
    }
}
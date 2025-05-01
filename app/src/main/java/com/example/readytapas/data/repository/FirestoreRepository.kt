package com.example.readytapas.data.repository

import android.util.Log
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

    suspend fun getCarta(): Result<List<Producto>> {
        return try {
            val productos = firestore.collection("Carta")
                .get()
                .await()
                .mapNotNull { it.toObject(Producto::class.java) }
            Result.success(productos)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error al obtener la carta", e)
            Result.failure(e)
        }
    }

    suspend fun getMesas(): Result<List<Mesa>> {
        return try {
            val mesas = firestore.collection("Mesas")
                .get()
                .await()
                .mapNotNull { it.toObject(Mesa::class.java) }
            Result.success(mesas)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error al obtener las mesas", e)
            Result.failure(e)
        }
    }

    suspend fun crearPedido(pedido: Pedido): Result<Unit> {
        return try {
            firestore.collection("Pedidos")
                .add(pedido)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error al crear pedido", e)
            Result.failure(e)
        }
    }

    suspend fun actualizarMesa(mesa: Mesa): Result<Unit> {
        return try {
            firestore.collection("Mesas")
                .whereEqualTo("name", mesa.name.name)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.reference
                ?.set(mesa)
                ?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error al actualizar mesa", e)
            Result.failure(e)
        }
    }
}
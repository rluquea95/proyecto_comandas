package com.example.readytapas.data.repository

import android.util.Log
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
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

    suspend fun getPedidos(): Result<List<Pedido>> {
        return try {
            val pedidos = firestore.collection("Pedidos")
                .get()
                .await()
                .mapNotNull { it.toObject(Pedido::class.java) }
            Result.success(pedidos)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error al obtener los pedidos", e)
            Result.failure(e)
        }
    }

    //Actualiza el pedido por mesa
    private suspend fun actualizarPedidoPorMesa(pedido: Pedido): Result<Unit> {
        return try {
            val documento = firestore.collection("Pedidos")
                .whereEqualTo("mesa", pedido.mesa.name)
                .whereIn("state", listOf("ENCURSO", "LISTO"))
                .get().await()
                .documents.firstOrNull()
            if (documento != null) {
                documento.reference.set(pedido).await()
                Result.success(Unit)
            } else {
                Result.failure(Exception("No se encontró el pedido para la mesa ${pedido.mesa.name}"))
            }
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error al actualizar pedido", e)
            Result.failure(e)
        }
    }

    //Actualiza el estado del pedido
    suspend fun actualizarEstadoPedido(pedido: Pedido): Result<Unit> {
        return try {
            if (pedido.state == EstadoPedido.CERRADO) {
                return Result.success(Unit)
            }

            val todasUnidades = pedido.carta.flatMap { it.unidades }
            val estaListo = todasUnidades.all { it.preparado && it.entregado }

            val estadoAEscribir = if (estaListo) EstadoPedido.LISTO else pedido.state
            val pedidoParaEscribir = pedido.copy(state = estadoAEscribir)

            actualizarPedidoPorMesa(pedidoParaEscribir)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error al actualizar el estado del Pedido", e)
            Result.failure(e)
        }
    }

    //Cerrar Pedido y liberar Mesa
    suspend fun cerrarPedidoYLiberarMesa(pedido: Pedido): Result<Unit> {
        return try {
            // 1. Actualiza el pedido
            actualizarPedidoPorMesa(pedido).getOrThrow()

            // 2. Libera la mesa
            val mesaLiberada = Mesa(name = pedido.mesa, occupied = false)
            actualizarMesa(mesaLiberada).getOrThrow()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreRepository", "Error en cerrarPedidoYLiberarMesa", e)
            Result.failure(e)
        }
    }
}
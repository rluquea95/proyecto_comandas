package com.example.readytapas.data.repository

import android.util.Log
import com.example.readytapas.data.model.EstadoPedido
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // =====================
    // Lecturas y CRUD puros
    // =====================

    suspend fun getCarta(): Result<List<Producto>> = runCatching {
        firestore.collection("Carta")
            .get().await()
            .mapNotNull { it.toObject(Producto::class.java) }
    }.onFailure { Log.e("FirestoreRepository", "Error al obtener la carta", it) }

    suspend fun getMesas(): Result<List<Mesa>> = runCatching {
        firestore.collection("Mesas")
            .get().await()
            .mapNotNull { it.toObject(Mesa::class.java) }
    }.onFailure { Log.e("FirestoreRepository", "Error al obtener las mesas", it) }

    suspend fun getPedidos(): Result<List<Pedido>> = runCatching {
        firestore.collection("Pedidos")
            .get().await()
            .mapNotNull { it.toObject(Pedido::class.java) }
    }.onFailure { Log.e("FirestoreRepository", "Error al obtener Pedidos", it) }

    suspend fun crearPedido(pedido: Pedido): Result<Unit> = runCatching {
        firestore.collection("Pedidos").add(pedido).await()
        Unit
    }.onFailure { Log.e("FirestoreRepository", "Error al crear Pedido", it) }

    suspend fun actualizarMesa(mesa: Mesa): Result<Unit> = runCatching {
        val doc = firestore.collection("Mesas")
            .whereEqualTo("name", mesa.name.name)
            .get().await()
            .documents.firstOrNull()
            ?: throw IllegalStateException("Mesa ${mesa.name} no encontrada")
        doc.reference.set(mesa).await()
        Unit
    }.onFailure { Log.e("FirestoreRepository", "Error al actualizar Mesa", it) }


    // ==========================================
    // Operaciones atómicas con control de concurrencia
    // ==========================================

    suspend fun actualizarEstadoPedido(pedido: Pedido): Result<Unit> = runCatching {
        // 1) Si ya está cerrado, no hacemos nada
        if (pedido.state == EstadoPedido.CERRADO) return@runCatching Unit

        // 2) Recorremos todas las unidades para ver si ya TODO está preparado y entregado
        val todasUnidades = pedido.carta.flatMap { it.unidades }
        val estaListo     = todasUnidades.all { it.preparado && it.entregado }

        // 3) Creamos un nuevo Pedido con el state adecuado
        val estadoAEscribir     = if (estaListo) EstadoPedido.LISTO else EstadoPedido.ENCURSO
        val pedidoParaEscribir  = pedido.copy(state = estadoAEscribir)

        // 4) Transacción atómica que actualiza documento
        val pedidoRef = firestore.collection("Pedidos")
            .whereEqualTo("mesa", pedido.mesa.name)
            .whereIn("state", listOf(EstadoPedido.ENCURSO, EstadoPedido.LISTO))
            .get()
            .await()
            .documents
            .firstOrNull()
            ?.reference
            ?: throw IllegalStateException("Pedido no encontrado para mesa ${pedido.mesa.name}")

        firestore.runTransaction<Void> { tx ->
            tx.set(pedidoRef, pedidoParaEscribir)
            null
        }.await()

        Unit
    }.onFailure { Log.e("FirestoreRepository", "Error al actualizar estado de Pedido", it) }


    suspend fun tomarPedidoConControl(pedido: Pedido): Result<Unit> = runCatching {
        val mesaRef = firestore.collection("Mesas")
            .whereEqualTo("name", pedido.mesa.name)
            .get().await()
            .documents.firstOrNull()?.reference
            ?: throw IllegalStateException("Mesa ${pedido.mesa.name} no encontrada")

        firestore.runTransaction<Void> { tx ->
            val actual = tx.get(mesaRef).toObject(Mesa::class.java)
                ?: throw IllegalStateException("Datos de mesa inválidos")
            if (actual.occupied) throw IllegalStateException("Mesa ocupada")
            val pedidoRef = firestore.collection("Pedidos").document()
            tx.set(pedidoRef, pedido)
            tx.update(mesaRef, "occupied", true)
            null
        }.await()
        Unit
    }.onFailure { Log.e("FirestoreRepository", "Error en tomarPedidoConControl", it) }

    suspend fun cerrarPedidoYLiberarMesa(pedido: Pedido): Result<Unit> = runCatching {
        val pedidoRef = firestore.collection("Pedidos")
            .whereEqualTo("mesa", pedido.mesa.name)
            .whereIn("state", listOf(EstadoPedido.ENCURSO, EstadoPedido.LISTO))
            .get().await()
            .documents.firstOrNull()?.reference
            ?: throw IllegalStateException("Pedido no encontrado para mesa ${pedido.mesa.name}")
        val mesaRef = firestore.collection("Mesas")
            .whereEqualTo("name", pedido.mesa.name)
            .get().await()
            .documents.firstOrNull()?.reference
            ?: throw IllegalStateException("Mesa ${pedido.mesa.name} no encontrada")

        firestore.runTransaction<Void> { tx ->
            tx.set(pedidoRef, pedido)
            tx.update(mesaRef, "occupied", false)
            null
        }.await()
        Unit
    }.onFailure { Log.e("FirestoreRepository", "Error en cerrarPedidoYLiberarMesa", it) }

    suspend fun eliminarPedidoYLiberarMesa(pedido: Pedido): Result<Unit> = runCatching {
        val pedidoRef = firestore.collection("Pedidos")
            .whereEqualTo("mesa", pedido.mesa.name)
            .whereIn("state", listOf(EstadoPedido.ENCURSO, EstadoPedido.LISTO))
            .get().await()
            .documents.firstOrNull()?.reference
            ?: throw IllegalStateException("Pedido no encontrado para mesa ${pedido.mesa.name}")
        val mesaRef = firestore.collection("Mesas")
            .whereEqualTo("name", pedido.mesa.name)
            .get().await()
            .documents.firstOrNull()?.reference
            ?: throw IllegalStateException("Mesa ${pedido.mesa.name} no encontrada")

        firestore.runTransaction<Void> { tx ->
            tx.delete(pedidoRef)
            tx.update(mesaRef, "occupied", false)
            null
        }.await()
        Unit
    }.onFailure { Log.e("FirestoreRepository", "Error en eliminarPedidoYLiberarMesa", it) }

    // ==========================================
    // Snapshot Listener para cambios en tiempo real
    // ==========================================

    fun observePedidos(): Flow<List<Pedido>> = callbackFlow {
        val listener = firestore.collection("Pedidos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val pedidos =
                    snapshot?.documents?.mapNotNull { it.toObject(Pedido::class.java) }.orEmpty()
                trySend(pedidos).isSuccess
            }

        awaitClose { listener.remove() }
    }

    /* Controlar la edición de Pedido por usuario*/
    /* Intenta «bloquear» el pedido para edición. Devuelve true si el bloqueo tuvo éxito. */
    suspend fun lockPedido(mesaName: String, uid: String): Boolean = runCatching {
        // 1) Localiza el documento
        val ref = firestore.collection("Pedidos")
            .whereEqualTo("mesa", mesaName)
            .whereIn("state", listOf(EstadoPedido.ENCURSO, EstadoPedido.LISTO))
            .get().await()
            .documents.firstOrNull()?.reference
            ?: throw IllegalStateException("Pedido no encontrado para mesa $mesaName")

        // 2) Transacción: solo bloquea si no está ya bloqueado (o está bloqueado por ti)
        firestore.runTransaction<Void> { tx ->
            val snap = tx.get(ref)
            val bloqueado = snap.getString("lockedBy")
            if (bloqueado == null || bloqueado == uid) {
                tx.update(ref, "lockedBy", uid)
                null
            } else {
                throw IllegalStateException("lockedBy:$bloqueado")
            }
        }
            .await()
        true

    }.getOrDefault(false)


    /* Libera el bloqueo */
    suspend fun unlockPedido(mesaName: String, uid: String): Result<Unit> = runCatching {
        val ref = firestore.collection("Pedidos")
            .whereEqualTo("mesa", mesaName)
            .whereIn("state", listOf(EstadoPedido.ENCURSO, EstadoPedido.LISTO))
            .get().await()
            .documents.firstOrNull()?.reference
            ?: throw IllegalStateException("Pedido no encontrado para mesa $mesaName")

        firestore.runTransaction<Void> { tx ->
            val snap = tx.get(ref)
            if (snap.getString("lockedBy") == uid) {
                tx.update(ref, "lockedBy", null)
            }
            null
        }
            .await()    // Ignoramos el Void! que devuelve
        Unit        // devolvemos Unit

    }.onFailure { Log.e("FirestoreRepository", "fallo al desbloquear Pedido", it) }

    // Devuelve un Flow que emite el Pedido cada vez que cambia en Firestore
    fun observePedido(mesaName: String): Flow<Pedido> = callbackFlow {
        // filtramos por la mesa activa y por estados ENC/ LISTO
        val query = firestore.collection("Pedidos")
            .whereEqualTo("mesa", mesaName)
            .whereIn("state", listOf(EstadoPedido.ENCURSO, EstadoPedido.LISTO))
            .limit(1)

        val registration = query.addSnapshotListener { snap, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            val pedido = snap
                ?.documents
                ?.firstOrNull()
                ?.toObject(Pedido::class.java)
            if (pedido != null) trySend(pedido).isSuccess
        }

        awaitClose { registration.remove() }
    }
}

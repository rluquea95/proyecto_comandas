package com.example.readytapas.data.model

import com.google.firebase.Timestamp

data class Pedido(
    val mesa: Int = 0,
    val carta: List<ProductoPedido> = emptyList(),
    val state: EstadoPedido = EstadoPedido.PREPARACION,
    val time: Timestamp = Timestamp.now(),
    val total: Double = 0.0
)

enum class EstadoPedido {
    PREPARACION,
    LISTO,
    CERRADO
}

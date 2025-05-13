package com.example.readytapas.data.model

import com.google.firebase.Timestamp

data class Pedido(
    val mesa: NumeroMesa = NumeroMesa.BARRA_1,
    val carta: List<ProductoPedido> = emptyList(),
    val state: EstadoPedido = EstadoPedido.ENCURSO,
    val time: Timestamp = Timestamp.now(),
    val total: Double = 0.0
)

enum class EstadoPedido {
    ENCURSO,
    LISTO,
    CERRADO
}

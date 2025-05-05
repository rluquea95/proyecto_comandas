package com.example.readytapas.data.model

data class ProductoPedido(
    val producto: Producto = Producto(),
    var unidades: List<EstadoUnidad> = listOf(EstadoUnidad())
)

data class EstadoUnidad(
    var preparado: Boolean = false,
    var entregado: Boolean = false
)

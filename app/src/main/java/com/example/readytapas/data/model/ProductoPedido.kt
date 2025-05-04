package com.example.readytapas.data.model

data class ProductoPedido(
    val producto: Producto = Producto(),
    var cantidad: Int = 1,
    var preparado: Boolean = false,
    var entregado: Boolean = false
)

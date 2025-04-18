package com.example.readytapas.data.model

data class Producto(
    val name: String = "",
    val description: String = "",
    val category: String = "",  // "plato", "tapa", "bebida"
    val price: Double = 0.0,
    val imageUrl: String = ""
)

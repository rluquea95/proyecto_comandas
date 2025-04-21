package com.example.readytapas.data.model

data class Producto(
    val name: String = "",
    val description: String = "",
    val category: CategoryProducto = CategoryProducto.PLATO,
    val price: Double = 0.0,
    val imageUrl: String = ""
)
enum class CategoryProducto{
    PLATO,
    TAPA,
    BEBIDA
}

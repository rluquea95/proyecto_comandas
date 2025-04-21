package com.example.readytapas.data.model

data class Mesa(
    val name: NumeroMesa = NumeroMesa.BARRA,
    val isOccupied: Boolean = false,
    val isReserved: Boolean = false,
)
enum class NumeroMesa{
    MESA_0,
    MESA_1,
    MESA_2,
    MESA_3,
    MESA_4,
    MESA_5,
    MESA_6,
    MESA_7,
    MESA_8,
    MESA_9,
    MESA_10,
    BARRA
}

package com.example.readytapas.data.model

data class Mesa(
    val name: NumeroMesa = NumeroMesa.BARRA,
    val occupied: Boolean = false,
    val reserved: Boolean = false,
)
enum class NumeroMesa{
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
    MESA_11,
    MESA_12,
    BARRA
}

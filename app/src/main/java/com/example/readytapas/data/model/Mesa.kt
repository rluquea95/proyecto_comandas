package com.example.readytapas.data.model

data class Mesa(
    val name: NumeroMesa = NumeroMesa.BARRA_1,
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
    BARRA_1,
    BARRA_2,
    BARRA_3,
    BARRA_4,
    BARRA_5,
    BARRA_6,
    BARRA_7,
    BARRA_8,
    BARRA_9,
    BARRA_10,
    BARRA_11,
    BARRA_12,
}

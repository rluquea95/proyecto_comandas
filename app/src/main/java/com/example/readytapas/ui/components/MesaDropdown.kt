package com.example.readytapas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.MarronOscuro


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesaDropdown(
    mesas: List<Mesa>,
    mesaSeleccionada: Mesa?,
    onMesaSeleccionada: (Mesa) -> Unit,
    enabled: Boolean = true
) {
    var expanded by remember { mutableStateOf(false) }
    // Calcula el índice del primer elemento de barra (BARRA_*)
    val firstBarIndex = mesas.indexOfFirst { it.name.name.startsWith("BARRA_") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = mesaSeleccionada?.name?.name ?: "Selecciona una mesa",
            onValueChange = {},
            label = { Text("Mesa", style = MaterialTheme.typography.bodyMedium) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BeigeClaro,
                unfocusedContainerColor = BeigeClaro,
                disabledContainerColor = BeigeClaro,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = MarronOscuro,
                unfocusedLabelColor = MarronOscuro
            ),
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            mesas.forEachIndexed { index, mesa ->
                // Dibuja la línea divisoria justo antes de la primera barra
                if (index == firstBarIndex) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }

                DropdownMenuItem(
                    text = {
                        Text(
                            text = mesa.name.name,
                            color = MarronOscuro,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    },
                    enabled = enabled,
                    onClick = {
                        onMesaSeleccionada(mesa)
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BeigeClaro),
                    colors = MenuDefaults.itemColors(
                        textColor         = MarronOscuro,
                        disabledTextColor = Color.Gray
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MesaDropdownPreview() {
    val mesasMock = listOf(
        Mesa(name = NumeroMesa.MESA_1, occupied = false),
        Mesa(name = NumeroMesa.MESA_2, occupied = false),
        Mesa(name = NumeroMesa.BARRA_2, occupied = false)
    )

    var mesaSeleccionada by remember { mutableStateOf<Mesa?>(null) }

    MesaDropdown(
        mesas = mesasMock,
        mesaSeleccionada = mesaSeleccionada,
        onMesaSeleccionada = { mesaSeleccionada = it }
    )
}

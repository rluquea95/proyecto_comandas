package com.example.readytapas.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.GrisMedio
import com.example.readytapas.ui.theme.MarronMedioAcentoOpacidad
import com.example.readytapas.ui.theme.MarronOscuro
import java.util.Locale

@Composable
fun ProductoPedidoDialogItem(
    producto: Producto,
    onAgregar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAgregar() },
            //.padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MarronMedioAcentoOpacidad),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = loadLocalDrawableStrict(producto.imageUrl),
                contentDescription = "Imagen de ${producto.name}",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = producto.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MarronOscuro
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BlancoHueso)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (producto.price == producto.price.toInt().toDouble()) {
                            "${producto.price.toInt()} €"
                        } else {
                            "${String.format(Locale.getDefault(), "%.2f", producto.price)} €"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = GrisMedio,
                        textAlign = TextAlign.Right,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductoPedidoDialogItemPreview() {
    ProductoPedidoDialogItem(
        producto = Producto(
            name = "Tapa de sobrasada",
            description = "Untable de sobrasada sobre pan rústico",
            category = CategoryProducto.TAPA,
            price = 1.5,
            imageUrl = "tapa_sobrasada"
        ),
        onAgregar = {}
    )
}


package com.example.readytapas.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.readytapas.R
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.GrisMedio
import com.example.readytapas.ui.theme.MarronMedioAcentoOpacidad
import com.example.readytapas.ui.theme.MarronOscuro
import java.util.Locale


@Composable
fun ProductoCard(
    producto: Producto,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MarronMedioAcentoOpacidad),
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min) // Establece la altura mínima
            .padding(8.dp)
        ) {
            Column(modifier = Modifier.wrapContentWidth()) {
                Image(
                    painter = loadLocalDrawableStrict(producto.imageUrl),
                    contentDescription = producto.name,
                    modifier = Modifier
                        .size(84.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onImageClick() }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
            // Nombre y descripción centrados verticalmente
            Column(modifier = Modifier.wrapContentWidth()) {
                Text(
                    producto.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MarronOscuro,
                    modifier = Modifier.width(160.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    producto.description,
                    color = MarronOscuro,
                    maxLines = 4,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(160.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Precio centrado verticalmente
            Column(modifier = Modifier
                .fillMaxHeight()
                .wrapContentWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MarronMedioAcentoOpacidad),
                    contentAlignment = Alignment.Center,
                ){
                    Text(
                        producto.category.name,
                        color = BlancoHueso,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        textAlign = TextAlign.Center
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(bottom = 4.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BlancoHueso),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        // Formatear el precio con dos decimales si es necesario
                        if (producto.price == producto.price.toInt().toDouble()) {
                            "${producto.price.toInt()} €"
                        } else {
                            // Formatear el precio con dos decimales
                            "${String.format(Locale.getDefault(), "%.2f", producto.price)} €"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = GrisMedio,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun ImagenProductoDialog(
    producto: Producto,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(BeigeClaro)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = loadLocalDrawableStrict(producto.imageUrl),
                    contentDescription = producto.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(20.dp))
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    producto.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MarronOscuro
                )
                Text(
                    producto.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp, start = 10.dp, end = 10.dp),
                    textAlign = TextAlign.Center,
                    color = GrisMedio
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MarronOscuro,
                        contentColor = BlancoHueso,
                    )
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun loadLocalDrawableStrict(name: String): Painter {
    val context = LocalContext.current

    val resId = remember(name) {
        // Busca la imagen en res/drawable y almacena su ID
        context.resources.getIdentifier(name, "drawable", context.packageName)
    }
    return if (resId != 0) {
        painterResource(id = resId)
    } else {
        Log.e("LoadLocalDrawable", "Imagen '$name' no encontrada en res/drawable")
        painterResource(id = R.drawable.placeholder_carta) // Imagen de error
    }
}

@Preview(showBackground = true)
@Composable
fun ProductoCardPreview() {
    val productoMock = Producto(
        name = "Cuña de tortilla de patatas",
        description = "Porción de tortilla de patatas",
        category = CategoryProducto.TAPA,
        price = 21.50,
        imageUrl = "plato_tortilla"
    )

    ProductoCard(
        producto = productoMock,
        onImageClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ImagenProductoDialogPreview() {
    val productoMock = Producto(
        name = "Caña de Cerveza",
        description = "Bien fresquita",
        category = CategoryProducto.BEBIDA,
        price = 2.0,
        imageUrl = "bebida_cerveza"
    )

    ImagenProductoDialog(
        producto = productoMock,
        onDismiss = {}
    )
}

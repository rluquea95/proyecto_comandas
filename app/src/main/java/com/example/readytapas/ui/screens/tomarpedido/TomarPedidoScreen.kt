package com.example.readytapas.ui.screens.tomarpedido

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.CategoryProducto
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
import com.example.readytapas.data.model.Producto
import com.example.readytapas.data.model.ProductoPedido
import com.example.readytapas.ui.components.CategoriaChips
import com.example.readytapas.ui.components.SearchBar
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.components.loadLocalDrawableStrict
import com.example.readytapas.ui.theme.BarBeigeClaro
import com.example.readytapas.ui.theme.BarBlancoHueso
import com.example.readytapas.ui.theme.BarGrisMedio
import com.example.readytapas.ui.theme.BarMarronMedioAcento
import com.example.readytapas.ui.theme.BarMarronOscuro
import java.util.Locale

@Composable
fun TomarPedidoScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: TomarPedidoViewModel = hiltViewModel()
) {
    val mesas by viewModel.mesas.collectAsState()
    val mesaSeleccionada by viewModel.mesaSeleccionada.collectAsState()
    val productosPedidos by viewModel.productosPedidos.collectAsState()
    val productosFiltrados by viewModel.productosFiltrados.collectAsState()
    val categoriaSeleccionada by viewModel.categoriaSeleccionada.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val mostrarSnackbar by viewModel.mostrarSnackbar.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    var isDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Button(
                onClick = { viewModel.confirmarPedido() },
                enabled = mesaSeleccionada != null && productosPedidos.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BarMarronOscuro,
                    contentColor = BarBlancoHueso
                )
            ) {
                Text("Confirmar Pedido")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LaunchedEffect(mostrarSnackbar) {
                if (mostrarSnackbar) {
                    snackbarHostState.showSnackbar(
                        message = "Pedido enviado a cocina ✅",
                        duration = SnackbarDuration.Short
                    )
                    viewModel.resetearSnackbar()
                }
            }

            TopBarWithMenu(
                title = "Tomar Pedido",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )

            Spacer(modifier = Modifier.height(8.dp))

            MesaDropdown(
                mesas = mesas,
                mesaSeleccionada = mesaSeleccionada,
                onMesaSeleccionada = viewModel::seleccionarMesa
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { isDialogOpen = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BarMarronOscuro,
                    contentColor = BarBlancoHueso
                )
            ) {
                Text("Añadir Producto")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (productosPedidos.isNotEmpty()) {
                Text(
                    text = "Pedido actual:",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productosPedidos) { productoPedido ->
                        ProductoPedidoItem(
                            productoPedido = productoPedido,
                            onAumentar = { viewModel.aumentarCantidad(productoPedido) },
                            onDisminuir = { viewModel.disminuirCantidad(productoPedido) },
                            onEliminar = { viewModel.eliminarProducto(productoPedido) }
                        )
                    }
                }
            }
        }
    }

    if (isDialogOpen) {
        AñadirProductoDialog(
            productos = productosFiltrados,
            categoriaSeleccionada = categoriaSeleccionada,
            searchText = searchText,
            onSearchTextChange = viewModel::actualizarTextoBusqueda,
            onCategoriaSeleccionada = viewModel::seleccionarCategoria,
            onProductoSeleccionado = { producto ->
                viewModel.agregarProducto(producto)
            },
            onDismiss = { isDialogOpen = false }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesaDropdown(
    mesas: List<Mesa>,
    mesaSeleccionada: Mesa?,
    onMesaSeleccionada: (Mesa) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        TextField(
            readOnly = true,
            value = mesaSeleccionada?.name?.name?.replace('_', ' ') ?: "Selecciona una mesa",
            onValueChange = {},
            label = { Text("Mesa") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = BarBeigeClaro,
                unfocusedContainerColor = BarBeigeClaro,
                disabledContainerColor = BarBeigeClaro,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedLabelColor = BarMarronOscuro,
                unfocusedLabelColor = BarMarronOscuro
            ),
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
                if (mesa.name == NumeroMesa.BARRA && index != 0) {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        thickness = 1.dp,
                        color = Color.Gray
                    )
                }
                DropdownMenuItem(
                    text = { Text(mesa.name.name.replace('_', ' ')) },
                    onClick = {
                        onMesaSeleccionada(mesa)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ProductoPedidoDialogItem(
    producto: Producto,
    onAgregar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAgregar() }
            .padding(horizontal = 8.dp),
        colors = CardDefaults.cardColors(containerColor = BarMarronMedioAcento),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Image(
                painter = loadLocalDrawableStrict(producto.imageUrl),
                contentDescription = producto.name,
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
                    color = BarMarronOscuro
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .align(Alignment.End)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BarBlancoHueso)
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
                        color = BarGrisMedio,
                        textAlign = TextAlign.Right,
                    )
                }
            }
        }
    }
}

@Composable
fun ProductoPedidoItem(
    productoPedido: ProductoPedido,
    onAumentar: () -> Unit,
    onDisminuir: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BarBeigeClaro)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = onEliminar, modifier = Modifier.size(54.dp)) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color.Gray
                    )
                }
                Text(
                    text = productoPedido.producto.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BarMarronOscuro,
                    modifier = Modifier.width(170.dp)
                )
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(BarGrisMedio),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = productoPedido.cantidad.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BarBlancoHueso
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(onClick = onAumentar, modifier = Modifier.size(54.dp)) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Aumentar cantidad",
                        tint = Color.Gray
                    )
                }
                IconButton(onClick = onDisminuir, modifier = Modifier.size(54.dp)) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Disminuir cantidad",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun AñadirProductoDialog(
    productos: List<Producto>,
    categoriaSeleccionada: CategoryProducto?,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onCategoriaSeleccionada: (CategoryProducto?) -> Unit,
    onProductoSeleccionado: (Producto) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = BarBlancoHueso),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Seleccionar Producto",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = BarMarronOscuro
                )

                Spacer(modifier = Modifier.height(8.dp))

                SearchBar(
                    searchText = searchText,
                    onSearchTextChange = onSearchTextChange,
                    placeholder = "Buscar producto..."
                )

                Spacer(modifier = Modifier.height(8.dp))

                CategoriaChips(
                    categorias = listOf(null) + CategoryProducto.entries,
                    selectedCategoria = categoriaSeleccionada,
                    ordenarPorPrecio = null, // Aquí no quieres ordenación por precio
                    onCategoriaSeleccionada = onCategoriaSeleccionada
                )

                Spacer(modifier = Modifier.height(8.dp))

                val productosFiltrados = productos.filter {
                    (categoriaSeleccionada == null || it.category == categoriaSeleccionada) &&
                            it.name.contains(searchText, ignoreCase = true)
                }

                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoPedidoDialogItem(
                            producto = producto,
                            onAgregar = { onProductoSeleccionado(producto) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BarMarronOscuro,
                        contentColor = BarBlancoHueso
                    )
                ) {
                    Text("Cerrar")
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TomarPedidoScreenPreview() {
    val mesasMock = listOf(
        Mesa(name = NumeroMesa.MESA_1, occupied = false),
        Mesa(name = NumeroMesa.MESA_2, occupied = false),
        Mesa(name = NumeroMesa.BARRA, occupied = false)
    )

    val productosPedidosMock = listOf(
        ProductoPedido(
            producto = Producto(
                name = "Tortilla de Patatas",
                description = "Clásica tortilla española con cebolla",
                category = CategoryProducto.PLATO,
                price = 8.50,
                imageUrl = "plato_tortilla"
            ),
            cantidad = 2
        ),
        ProductoPedido(
            producto = Producto(
                name = "Caña de cerveza",
                description = "Cerveza bien fría",
                category = CategoryProducto.BEBIDA,
                price = 2.00,
                imageUrl = "bebida_cerveza"
            ),
            cantidad = 1
        )
    )

    Column(modifier = Modifier.padding(16.dp)) {
        MesaDropdown(
            mesas = mesasMock,
            mesaSeleccionada = mesasMock[0],
            onMesaSeleccionada = {}
        )
        Spacer(modifier = Modifier.height(16.dp))
        productosPedidosMock.forEach { pedido ->
            ProductoPedidoItem(
                productoPedido = pedido,
                onAumentar = {},
                onDisminuir = {},
                onEliminar = {}
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MesaDropdownPreview() {
    val mesasMock = listOf(
        Mesa(name = NumeroMesa.MESA_1, occupied = false),
        Mesa(name = NumeroMesa.MESA_2, occupied = false)
    )

    var mesaSeleccionada by remember { mutableStateOf<Mesa?>(null) }

    MesaDropdown(
        mesas = mesasMock,
        mesaSeleccionada = mesaSeleccionada,
        onMesaSeleccionada = { mesaSeleccionada = it }
    )
}

@Preview(showBackground = true)
@Composable
fun AñadirProductoDialogPreview() {
    val productosMock = listOf(
        Producto(
            name = "Tortilla",
            description = "Tortilla española",
            category = CategoryProducto.PLATO,
            price = 8.0,
            imageUrl = "plato_tortilla"
        ),
        Producto(
            name = "Cerveza",
            description = "Caña fría",
            category = CategoryProducto.BEBIDA,
            price = 2.0,
            imageUrl = "bebida_cerveza"
        )
    )

    AñadirProductoDialog(
        productos = productosMock,
        categoriaSeleccionada = null,
        searchText = "",
        onSearchTextChange = {},
        onCategoriaSeleccionada = {},
        onProductoSeleccionado = {},
        onDismiss = {}
    )
}





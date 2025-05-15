package com.example.readytapas.ui.screens.platoslistos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.*
import com.example.readytapas.ui.components.CustomSnackbarHost
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.MarronOscuro
import com.example.readytapas.ui.theme.MarronMedioAcento
import com.example.readytapas.ui.theme.MarronMedioAcentoOpacidad


@Composable
fun PlatosListosScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: PlatosListosViewModel = hiltViewModel()
) {
    // Ahora sí reconoce uiState y PlatosListosViewModel
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        snackbarHost = {
            CustomSnackbarHost(
                snackbarHostState = snackbarHostState,
                snackbarType = state.snackbarType,
                onDismiss = { viewModel.clearMessage() }
            )
        },
        topBar = {
            TopBarWithMenu(
                title = "Platos Listos",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            Button(
                onClick = viewModel::confirmEntregados,
                enabled = state.productosSeleccionados.any { it.value.isNotEmpty() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MarronOscuro,
                    contentColor = BlancoHueso
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar entregados", fontSize = 18.sp)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                val pedidosFiltrados = state.pedidos.filter { pedido ->
                    viewModel.getPlatosPendientesPorMesa(pedido.mesa.name).isNotEmpty()
                }

                items(
                    items = pedidosFiltrados,
                    key = { it.mesa.name }
                ) { pedido ->
                    val mesaName = pedido.mesa.name
                    val isExpanded = mesaName in state.pedidosExpandidos
                    val platosPendientes = viewModel.getPlatosPendientesPorMesa(mesaName)

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable { viewModel.toggleExpandido(mesaName) },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MarronMedioAcentoOpacidad),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = mesaName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MarronOscuro
                            )
                            if (isExpanded) {
                                Spacer(Modifier.height(8.dp))
                                if (platosPendientes.isEmpty()) {
                                    Text(
                                        "Todos los platos entregados.",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                        color = MarronOscuro
                                    )
                                } else {
                                    platosPendientes.forEach { (pp, idx) ->
                                        val clave = "${pp.producto.name}-$idx"
                                        val seleccionado =
                                            state.productosSeleccionados[mesaName]?.contains(clave) == true

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(
                                                    color = BeigeClaro,
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                        ) {
                                            Checkbox(
                                                checked = seleccionado,
                                                onCheckedChange = {
                                                    viewModel.toggleSeleccionUnidad(
                                                        mesaName,
                                                        clave
                                                    )
                                                },
                                                colors = CheckboxDefaults.colors(
                                                    checkedColor = MarronOscuro,
                                                    uncheckedColor = Color.Gray,
                                                    checkmarkColor = BlancoHueso
                                                )
                                            )
                                            Text(
                                                text = pp.producto.name,
                                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                                color = MarronOscuro
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

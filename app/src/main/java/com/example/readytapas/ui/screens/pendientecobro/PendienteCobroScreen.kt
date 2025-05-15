package com.example.readytapas.ui.screens.pendientecobro

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.readytapas.utils.PdfTicket
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.ui.components.CustomSnackbarHost
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.BeigeClaro
import com.example.readytapas.ui.theme.MarronOscuro
import com.example.readytapas.ui.theme.MarronMedioAcento

@Composable
fun PendienteCobroScreen(
    navController: NavController,
    onLogoutClick: () -> Unit,
    viewModel: PendienteCobroViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Aquí arranca el manejador de eventos de cobro y PDF
    GenerarPdfTicketHandler(viewModel)

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
                title = "Pendiente de Cobro",
                titleAlignment = TextAlign.Center,
                onLogoutClick = onLogoutClick,
                showBackButton = true,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(state.pedidos, key = { it.mesa.name }) { pedido ->
                val mesaName = pedido.mesa.name
                val isExpanded = mesaName in state.pedidosExpandidos
                val lineasAgrupadas = viewModel.getLineasAgrupadasPorMesa(pedido)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { viewModel.toggleExpandido(mesaName) },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BeigeClaro)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = mesaName,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MarronOscuro
                        )
                        if (isExpanded) {
                            Spacer(Modifier.height(8.dp))
                            lineasAgrupadas.forEach { (producto, cantidad) ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .background(BeigeClaro, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "$cantidad × ${producto.name}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MarronOscuro
                                    )
                                    Spacer(Modifier.weight(1f))
                                    Text(
                                        text = "€${"%.2f".format(producto.price * cantidad)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MarronOscuro
                                    )
                                }
                            }

                            Spacer(Modifier.height(16.dp))

                            // Botón para cobrar *este* pedido
                            Button(
                                onClick = { viewModel.cobrarPedido(pedido) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MarronOscuro,
                                    contentColor = BlancoHueso
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cobrar mesa", fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.example.readytapas.ui.screens.mainmenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.readytapas.R
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BlancoHueso
import com.example.readytapas.ui.theme.MarronOscuro


private val rowVerticalSpacing = 40.dp // Espacio vertical entre las filas
private val menuItemSpacing = 10.dp // Espacio horizontal entre los elementos del menú

@Composable
fun MainMenuScreen(
    onLogoutClick: () -> Unit,
    navController: NavController
) {
    //Con Scaffold podemos evitar que el contenido se solape con la barra superior (TopBarWithMenu)
    //Definimos el color de fondo de la pantalla
    Scaffold(
        topBar = {
            TopBarWithMenu(
                title = "Ready Tapas",
                onLogoutClick = onLogoutClick,
                showBackButton = false
            )
        },
        containerColor = BlancoHueso
    ) { innerPadding ->
        //Contenedor principal de los iconos del menú
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding), // Aplicar el padding definido en Scaffold
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(22.dp))

            // Fila 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center, //Los centrará horizontalmente
            ) {
                //Icono de tomar pedido
                MenuItem(
                    painter = painterResource(id = R.drawable.crear_pedido),
                    text = "Tomar pedido",
                    onClick = {
                        navController.navigate("tomarPedido")
                    }
                )

                Spacer(modifier = Modifier.width(menuItemSpacing))

                //Icono de Editar Pedido
                MenuItem(
                    painter = painterResource(id = R.drawable.editar_pedido),
                    text = "Editar Pedido",
                    onClick = {
                        navController.navigate("editarPedido")
                    }
                )
            }

            Spacer(modifier = Modifier.height(rowVerticalSpacing))

            // Fila 2
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                //Icono de en preparación
                MenuItem(
                    painter = painterResource(id = R.drawable.en_preparacion),
                    text = "En preparación",
                    onClick = {
                        navController.navigate("enPreparacion")
                    }
                )

                Spacer(modifier = Modifier.width(menuItemSpacing))


                //Icono de platos listos
                MenuItem(
                    painter = painterResource(id = R.drawable.platos_listos),
                    text = "Platos listos",
                    onClick = {
                        navController.navigate("platosListos")
                    }
                )
            }

            Spacer(modifier = Modifier.height(rowVerticalSpacing))

            // Fila 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                //Icono de carta
                MenuItem(
                    painter = painterResource(id = R.drawable.carta),
                    text = "Carta",
                    onClick = {
                        navController.navigate("carta")
                    }
                )

                Spacer(modifier = Modifier.width(menuItemSpacing))

                //Icono de pendiente de cobro
                MenuItem(
                    painter = painterResource(id = R.drawable.pendiente_cobro),
                    text = "Pendiente de cobro",
                    onClick = {
                        navController.navigate("pendienteCobro")
                    }
                )
            }

            Spacer(modifier = Modifier.height(rowVerticalSpacing))

            // Fila 4
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {

                //Icono de pedidos cerrados
                MenuItem(
                    painter = painterResource(id = R.drawable.historial_pedidos_cerrados),
                    text = "Historial Pedidos",
                    onClick = {
                        navController.navigate("historialPedidos")
                    }
                )

                Spacer(modifier = Modifier.width(menuItemSpacing))

                //Icono de reservas
                MenuItem(
                    painter = painterResource(id = R.drawable.reservas),
                    text = "Reserva",
                    onClick = {/* Enlazará a la pantalla Reservas */}
                )
            }

            Spacer(modifier = Modifier.height(rowVerticalSpacing))
        }
    }
}

// Composable para cada elemento del menú
@Composable
fun MenuItem(
    painter: Painter,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Image(
            painter = painter,
            contentDescription = text,
            modifier = Modifier.size(100.dp),
            colorFilter = ColorFilter.tint(MarronOscuro) //Aplica el color al icono
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text,
            modifier = Modifier.width(180.dp),
            fontSize = 22.sp,
            color = MarronOscuro,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview
@Composable
fun PreviewMainMenuScreen() {
    // Creamos un NavController simulado para la vista previa (Preview)
    val navController = rememberNavController()

    // Llamamos a MainMenuScreen con onLogoutClick vacío (para la vista previa)
    MainMenuScreen(
        onLogoutClick = { /* Acción de logout vacía para el preview */ },
        navController = navController
    )
}
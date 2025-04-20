package com.example.readytapas.ui.screens.mainmenu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.readytapas.R
import com.example.readytapas.ui.components.TopBarWithMenu
import com.example.readytapas.ui.theme.BarBeigeClaro
import com.example.readytapas.ui.theme.BarMarronOscuro

@Composable
fun MainMenuScreen(
    onLogoutClick: () -> Unit,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BarBeigeClaro)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TopBarWithMenu(onLogoutClick = onLogoutClick) // Pasamos la acción de logout
            // Se añade espacio entre la barra superior y el contenido
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Los botones de la pantalla principal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MenuItem(
                        painter = painterResource(id = R.drawable.tomar_notas),
                        text = "Tomar pedido",
                        onClick = { navController.navigate("tomarPedido") } // Navegar a la pantalla de tomar pedido
                    )
                    MenuItem(
                        painter = painterResource(id = R.drawable.en_preparacion),
                        text = "En preparación",
                        onClick = { navController.navigate("enPreparacion") } // Navegar a la pantalla "En preparación"
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MenuItem(
                        painter = painterResource(id = R.drawable.platos_listos),
                        text = "Platos listos",
                        onClick = { navController.navigate("platosListos") } // Navegar a la pantalla de platos listos
                    )
                    MenuItem(
                        painter = painterResource(id = R.drawable.pendiente_cobro),
                        text = "Pendiente de cobro",
                        onClick = { navController.navigate("pendienteCobro") } // Navegar a la pantalla de pendiente de cobro
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MenuItem(
                        painter = painterResource(id = R.drawable.carta),
                        text = "Carta",
                        onClick = { navController.navigate("carta") } // Navegar a la pantalla de la carta
                    )
                    MenuItem(
                        painter = painterResource(id = R.drawable.reservas),
                        text = "Reserva",
                        onClick = { navController.navigate("reservas") } // Navegar a la pantalla de pendiente de cobro
                    )
                }
            }
        }
    }
}

@Composable
fun MenuItem(
    painter: Painter,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(14.dp)
            .width(130.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = text,
            modifier = Modifier.size(80.dp),
            colorFilter = ColorFilter.tint(BarMarronOscuro)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, fontSize = 24.sp, color = BarMarronOscuro, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun PreviewMainMenuScreen(){
    // Creamos un NavController simulado para el Preview
    val navController = rememberNavController()

    // Llamamos a MainMenuScreen con un onLogoutClick vacío (para la vista previa)
    MainMenuScreen(
        onLogoutClick = { /* Acción de logout vacía para el preview */ },
        navController = navController
    )
}
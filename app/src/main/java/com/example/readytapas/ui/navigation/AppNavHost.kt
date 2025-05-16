package com.example.readytapas.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.readytapas.ui.screens.login.LoginScreen
import com.example.readytapas.ui.screens.mainmenu.MainMenuScreen
import androidx.compose.ui.Alignment
import com.example.readytapas.ui.screens.carta.CartaScreen
import com.example.readytapas.ui.screens.editarpedido.EditarPedidoScreen
import com.example.readytapas.ui.screens.enpreparacion.EnPreparacionScreen
import com.example.readytapas.ui.screens.pendientecobro.PendienteCobroScreen
import com.example.readytapas.ui.screens.platoslistos.PlatosListosScreen
import com.example.readytapas.ui.screens.tomarpedido.TomarPedidoScreen
import com.example.readytapas.ui.theme.MarronMedioAcento

@Composable
fun AppNavHost(navController: NavHostController, isLoggedIn: Boolean, onLogoutClick: () -> Unit) {
    NavHost(
        navController = navController,
        /*Se almacena la pantalla que se verá cuando la aplicación se inicia
          en caso de que el usuario tenga la sesión iniciada, cargará directamente
          la pantalla mainmenu*/
        startDestination = if (isLoggedIn) "mainmenu" else "login"
    ) {
        composable("login") {
            //Cuando el usuario inicia sesión correctamente, se cambia a la pantalla mainmenu
            LoginScreen(onLoginSuccess = {
                navController.navigate("mainmenu") {
                    /*Elimina la pantalla de login de la pila de navegación para que
                    el usuario no pueda volver pulsando retroceso*/
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("mainmenu") {
            MainMenuScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }

        composable("tomarPedido") {
            TomarPedidoScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }

        composable("editarPedido") {
            EditarPedidoScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }

        composable("carta"){
            CartaScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }

        composable("tomarPedido") {
            TomarPedidoScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }

        composable("enPreparacion") {
            EnPreparacionScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }

        composable("platosListos") {
            PlatosListosScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }

        composable("pendienteCobro") {
            PendienteCobroScreen(onLogoutClick = onLogoutClick, navController = navController) // Pasamos el navController
        }
    }
}

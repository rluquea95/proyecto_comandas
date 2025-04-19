package com.example.readytapas.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.readytapas.ui.screens.login.LoginScreen
import com.example.readytapas.ui.screens.mainmenu.MainMenuScreen

@Composable
fun AppNavHost(navController: NavHostController, isLoggedIn: Boolean) {
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
            MainMenuScreen()
        }
    }
}

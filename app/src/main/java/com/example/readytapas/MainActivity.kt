package com.example.readytapas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.readytapas.data.repository.AuthRepository
import com.example.readytapas.ui.navigation.AppNavHost
import com.example.readytapas.ui.theme.ReadyTapasTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/*
                //Aquí se instancia FirestoreUploader y se pasa context para poder acceder al JSON que contiene Productos
                val firestoreUploaderProducto = FirestoreUploaderProducto(this)

                //Llamamos a la función uploadJsonData para subir los datos a Firestore
                firestoreUploaderProducto.uploadJsonDataProducto()


                //Aquí se instancia FirestoreUploader y se pasa context para poder acceder al JSON que contiene Mesas
                val firestoreUploaderMesa = FirestoreUploaderMesa(this)

                //Llamamos a la función uploadJsonData para subir los datos a Firestore
                firestoreUploaderMesa.uploadJsonDataMesa()
            */

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReadyTapasTheme {

                val navController = rememberNavController()

                // Función de logout que cierra la sesión utilizando AuthRepository
                val onLogoutClick: () -> Unit = {
                    authRepository.logout() // Llamamos a la función logout() del AuthRepository
                    navController.navigate("login") {
                        popUpTo("mainmenu") {
                            inclusive = true
                        } // Elimina la pantalla principal de la pila de navegación
                    }
                }

                AppNavHost(
                    navController = navController,
                    isLoggedIn = authRepository.currentUser != null,
                    onLogoutClick = onLogoutClick // Pasamos la función de logout a AppNavHost
                )
            }
        }
    }
}
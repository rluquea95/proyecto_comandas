package com.example.readytapas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.readytapas.data.repository.AuthRepository
import com.example.readytapas.ui.navigation.AppNavHost
import com.example.readytapas.ui.theme.ReadyTapasTheme
import com.example.readytapas.uploaddata.FirestoreUploaderMesa
import com.example.readytapas.uploaddata.FirestoreUploaderProducto
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


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
                    authRepository.logout()
                }

                AppNavHost(
                    navController = navController,
                    authRepository = authRepository,
                    onLogoutClick = onLogoutClick // Pasamos la función de logout a AppNavHost
                )
            }
        }
    }
}
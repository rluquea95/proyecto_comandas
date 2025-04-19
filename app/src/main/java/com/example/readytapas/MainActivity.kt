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
import com.google.firebase.auth.FirebaseAuth

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


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Obtenemos la instancia de Firebase Authenticator
        val authRepository = AuthRepository()

        setContent {
            ReadyTapasTheme {
                val navController = rememberNavController()
                AppNavHost(navController = navController, isLoggedIn = authRepository.currentUser != null)
            }
        }
    }
}
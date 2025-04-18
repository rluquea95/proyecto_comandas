package com.example.readytapas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

/*
            //Aquí se instancia FirestoreUploader y se pasa context para poder acceder al JSON que contiene Productos
            val firestoreUploader = FirestoreUploaderProducto(this)

            //Llamamos a la función uploadJsonData para subir los datos a Firestore
            firestoreUploaderProducto.uploadJsonData()


            //Aquí se instancia FirestoreUploader y se pasa context para poder acceder al JSON que contiene Mesas
            val firestoreUploaderMesa = FirestoreUploaderMesa(this)

            //Llamamos a la función uploadJsonData para subir los datos a Firestore
            firestoreUploaderMesa.uploadJsonDataMesa()

         */



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

        }
    }
}
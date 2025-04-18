package com.example.readytapas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.readytapas.uploaddata.FirestoreUploaderMesa
import com.example.readytapas.uploaddata.FirestoreUploaderProducto

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {

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

        }
    }
}
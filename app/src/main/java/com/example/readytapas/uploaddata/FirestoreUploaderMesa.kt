package com.example.readytapas.uploaddata

import android.content.Context
import com.example.readytapas.R
import com.example.readytapas.data.model.Mesa
import com.example.readytapas.data.model.NumeroMesa
import com.google.firebase.firestore.FirebaseFirestore
import java.io.InputStreamReader
import org.json.JSONArray
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreUploaderMesa(private val context: Context) {

    // Usando Firebase.firestore para obtener la instancia de Firestore
    private var db: FirebaseFirestore = Firebase.firestore

    // Función para leer el archivo JSON y subir los datos a Firestore
    fun uploadJsonDataMesa() {
        try {
            // Lee el archivo JSON desde los recursos
            val fileInputStream = context.resources.openRawResource(R.raw.ready_tapas_mesa)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val jsonString = inputStreamReader.readText()

            // Convierte el JSON a un array o lista
            val jsonArray = JSONArray(jsonString)

            // Recorre el array y sube cada objeto a Firestore
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                // Crea el objeto Mesa usando la clase data Mesa
                val mesa = Mesa(
                    name = NumeroMesa.valueOf(jsonObject.getString("name")),
                    occupied = jsonObject.getBoolean("occupied"),
                    reserved = jsonObject.getBoolean("reserved")
                )

                // Guardamos la mesa en la colección "Mesas" usando name como ID del documento
                // Usamos mesa.name.name para que guarde el string "MESA_X" en vez de "NumeroMesa.MESA_X"
                db.collection("Mesas").document(mesa.name.name).set(mesa)
                    .addOnSuccessListener {
                        println("Mesa añadida con ID: ${mesa.name}")
                    }
                    .addOnFailureListener { e ->
                        println("Error al añadir mesa: $e")
                    }
            }

            inputStreamReader.close()
            fileInputStream.close()

        } catch (e: Exception) {
            println("Error al subir el archivo JSON: $e")
        }
    }
}

///--------------------------------------------------------------------------------------------------------
/// Código que permite en la clase MainActivity subir los datos de Productos y Mesas a Firestore
/// Previamente hay que cambiar las reglas en Firestore para permitir lectura/escritura sin autentificación
///--------------------------------------------------------------------------------------------------------
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
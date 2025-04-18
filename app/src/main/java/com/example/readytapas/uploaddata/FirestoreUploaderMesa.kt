package com.example.readytapas.uploaddata

import android.content.Context
import com.example.readytapas.R
import com.example.readytapas.data.model.Mesa
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
                    id = jsonObject.getInt("id"),
                    isOccupied = jsonObject.getBoolean("isOccupied"),
                    isReserved = jsonObject.getBoolean("isReserved")
                )

                // Guardamos la mesa en la colección "Mesas" usando el id como ID del documento
                db.collection("Mesas").document(mesa.id.toString()).set(mesa)
                    .addOnSuccessListener {
                        println("Mesa añadida con ID: ${mesa.id}")
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

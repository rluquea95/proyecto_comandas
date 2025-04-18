package com.example.readytapas.uploaddata

import android.content.Context
import com.example.readytapas.R
import com.example.readytapas.data.model.Producto
import com.google.firebase.firestore.FirebaseFirestore
import java.io.InputStreamReader
import org.json.JSONArray
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreUploaderProducto(private val context: Context) {

    // Usando Firebase.firestore para obtener la instancia de Firestore
    private var db: FirebaseFirestore = Firebase.firestore

    // Función para leer el archivo JSON y subir los datos a Firestore
    fun uploadJsonDataProducto() {
        try {
            // Lee el archivo JSON desde los recursos
            val fileInputStream = context.resources.openRawResource(R.raw.ready_tapas_carta)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val jsonString = inputStreamReader.readText()

            // Convierte el JSON a un array o lista
            val jsonArray = JSONArray(jsonString)

            // Recorre el array y sube cada objeto a Firestore
            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray.getJSONObject(i)

                // Crea el objeto Producto usando la clase data Producto
                val producto = Producto(
                    name = jsonObject.getString("name"),
                    category = jsonObject.getString("category"),
                    description = jsonObject.getString("description"),
                    price = jsonObject.getDouble("price"),
                    imageUrl = jsonObject.getString("imageUrl")
                )

                // Guardamos el producto en la colección "Carta" en Firestore
                db.collection("Carta").add(producto)
                    .addOnSuccessListener { documentReference ->
                        println("Producto añadido: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        println("Error al añadir producto: $e")
                    }
            }

            inputStreamReader.close()
            fileInputStream.close()

        } catch (e: Exception) {
            println("Error al subir el archivo JSON: $e")
        }
    }
}

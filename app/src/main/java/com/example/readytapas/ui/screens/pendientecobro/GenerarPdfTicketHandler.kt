package com.example.readytapas.ui.screens.pendientecobro

import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import com.example.readytapas.utils.PdfTicket
import kotlinx.coroutines.flow.Flow
import java.io.File

@Composable
fun GenerarPdfTicketHandler(
    pedidosFlow: Flow<Pedido>,
    getLineas: (Pedido) -> List<Pair<Producto, Int>>
) {
    val context = LocalContext.current
    // 1) Al entrar en esta pantalla, borramos cualquier PDF anterior
    LaunchedEffect(Unit) {
        File(context.cacheDir, "facturas")
            .takeIf { it.exists() }
            ?.listFiles()
            ?.forEach { it.delete() }
    }

    LaunchedEffect(Unit) {
        pedidosFlow.collect { pedido ->
            val lineas = getLineas(pedido)
            val pdfFile = PdfTicket.generateTicketPdf(context, pedido, lineas)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
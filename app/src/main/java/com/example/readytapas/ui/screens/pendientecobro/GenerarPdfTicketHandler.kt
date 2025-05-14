package com.example.readytapas.ui.screens.pendientecobro

import android.content.Intent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.readytapas.utils.PdfTicket

@Composable
fun GenerarPdfTicketHandler(
    viewModel: PendienteCobroViewModel,
    snackbarHostState: SnackbarHostState
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.cobradoEvent.collect { pedidoCobrado ->
            val lineas = viewModel.getLineasAgrupadasPorMesa(pedidoCobrado)
            val pdfFile = PdfTicket.generateTicketPdf(context, pedidoCobrado, lineas)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                pdfFile
            )
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(viewIntent)
            snackbarHostState.showSnackbar("Factura generada y abierta")
        }
    }
}
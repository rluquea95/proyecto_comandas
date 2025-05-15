package com.example.readytapas.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.example.readytapas.data.model.Pedido
import com.example.readytapas.data.model.Producto
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Locale

object PdfTicket {
    /**
     * Genera un PDF con la factura del pedido y lo guarda
     * en cacheDir, devolviendo el File resultante.
     */
    fun generateTicketPdf(
        context: Context,
        pedido: Pedido,
        lineas: List<Pair<Producto, Int>>
    ): File {
        val facturasDir = File(context.filesDir, "facturas")
        if (!facturasDir.exists()) facturasDir.mkdirs()

        //Formatear la fecha
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fechaStr = dateFormat.format(pedido.time.toDate())

        // 2) Nombre único para el PDF
        val filename = "factura_${pedido.mesa.name}_${System.currentTimeMillis()}.pdf"
        val file = File(facturasDir, filename)

        // Preparamos Paints
        val paintTitle = Paint().apply {
            typeface = Typeface.MONOSPACE
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        val paintBody = Paint().apply {
            typeface = Typeface.MONOSPACE
            textSize = 16f
            textAlign = Paint.Align.LEFT
        }
        val paintSep = Paint().apply {
            strokeWidth = 1f
            color = Color.DKGRAY
        }

        val paintTotal = Paint().apply {
            typeface = Typeface.MONOSPACE
            textSize = 20f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        // Medimos altura de una línea
        val fm = paintBody.fontMetrics
        val lineHeight = (fm.bottom - fm.top) * 1.2f  // factor de interlineado

        // Padding
        val paddingTop = 40f
        val paddingBottom = 40f
        val headerLines = 3
        val headerHeight = headerLines * lineHeight
        val footerHeight = 2 * lineHeight  // separador + “TOTAL”

        // Altura dinámica
        val contentHeight = headerHeight +
                lineas.size * lineHeight +
                footerHeight +
                paddingTop + paddingBottom
        val pageWidth = 450  // puedes ajustar
        val pageHeight = contentHeight.toInt()

        // Creamos el documento
        val pdf = PdfDocument()
        val info = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdf.startPage(info)
        val canvas = page.canvas

        // Header
        var y = paddingTop
        canvas.drawText("READY TAPAS", (pageWidth / 2).toFloat(), y, paintTitle)
        y += lineHeight * 1.5f

        // Línea con Mesa (izquierda) y Fecha (derecha)
        val mesaText = "Mesa: ${pedido.mesa.name}"
        val fechaText = "FECHA: $fechaStr"
        canvas.drawText(mesaText, 10f, y, paintBody)
        canvas.drawText(
            fechaText,
            pageWidth - 10f - paintBody.measureText(fechaText),
            y,
            paintBody
        )
        y += lineHeight
        canvas.drawLine(0f, y, pageWidth.toFloat(), y, paintSep)
        y += lineHeight

        // Body
        lineas.forEach { (producto, cantidad) ->
            val texto = "%2d x %-20s".format(cantidad, producto.name)
            canvas.drawText(texto, 10f, y, paintBody)
            val precio = "€${"%.2f".format(producto.price * cantidad)}"
            canvas.drawText(
                precio,
                pageWidth - 10f - paintBody.measureText(precio),
                y,
                paintBody
            )
            y += lineHeight
        }

        // Footer
        canvas.drawLine(0f, y, pageWidth.toFloat(), y, paintSep)
        y += lineHeight
        canvas.drawText(
            "TOTAL: €${"%.2f".format(pedido.total)}",
            (pageWidth/2).toFloat(),
            y,
            paintTotal
        )

        // Guardamos
        pdf.finishPage(page)
        pdf.writeTo(FileOutputStream(file))
        pdf.close()

        return file
    }
}
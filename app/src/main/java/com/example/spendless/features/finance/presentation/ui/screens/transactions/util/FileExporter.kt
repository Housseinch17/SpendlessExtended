package com.example.spendless.features.finance.presentation.ui.screens.transactions.util

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import com.example.spendless.features.finance.data.model.TransactionItem
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Document
import com.itextpdf.text.Element
import com.itextpdf.text.FontFactory
import com.itextpdf.text.Paragraph
import com.itextpdf.text.Phrase
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import java.io.OutputStream
import javax.inject.Inject

class FileExporter @Inject constructor(@ApplicationContext val context: Context) {
    @SuppressLint("NewApi")
    fun saveCsvToDownloads(fileName: String, csvData: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "text/csv")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                outputStream.write(csvData.toByteArray())
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)

            Timber.tag("MyTag").d("CSV saved to Downloads/$fileName")
        }
    }

    @SuppressLint("InlinedApi")
    fun savePdfToDownloads(fileName: String, transactionList: List<TransactionItem>) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val uri = resolver.insert(collection, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                writePdf(outputStream, transactionList)
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(it, contentValues, null, null)

            Timber.tag("MyTag").d("PDF saved to Downloads/$fileName")
        }
    }

    private fun writePdf(outputStream: OutputStream, transactionsList: List<TransactionItem>) {
        val document = Document()
        PdfWriter.getInstance(document, outputStream)
        document.open()

        val titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16f)
        val headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12f)
        val cellFont = FontFactory.getFont(FontFactory.HELVETICA, 11f)

        //Title
        val title = Paragraph("Transactions Report\n\n", titleFont)
        title.alignment = Element.ALIGN_CENTER
        document.add(title)

        //Table with 5 columns
        val table = PdfPTable(5)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(2f, 2f, 1.5f, 1f, 1.5f)) // column width ratio

        //Header row
        val headers = listOf("Title", "Category", "Price", "Type", "Date")
        headers.forEach {
            val cell = PdfPCell(Phrase(it, headerFont))
            cell.horizontalAlignment = Element.ALIGN_CENTER
            cell.backgroundColor = BaseColor.LIGHT_GRAY
            table.addCell(cell)
        }

        //Add data rows
        transactionsList.forEach { txn ->
            table.addCell(Phrase(txn.title, cellFont))
            table.addCell(Phrase(txn.category.categoryName.name, cellFont))
            table.addCell(Phrase(txn.price, cellFont))
            table.addCell(Phrase(if (txn.isExpense) "Expense" else "Income", cellFont))
            table.addCell(Phrase(txn.date, cellFont))
        }
        document.add(table)
        document.close()
    }
}

package com.browserhelper
import android.webkit.WebView
import org.json.JSONArray

object TableParser {
    fun exportToCsv(tableHtml: String): String {
        // very simple parser - for robust, parse in JS side
        return tableHtml // placeholder, real export happens in JS evaluated result
    }
}

object CsvExporter {
    fun fromJsonArray(rows: List<List<String>>): String {
        return rows.joinToString("\n") { r -> r.joinToString(",") { """+it.replace(""","""")+""" } }
    }
}

object XlsxExporter {
    fun createXlsx(rows: List<List<String>>, outPath: String) {
        val wb = org.apache.poi.xssf.usermodel.XSSFWorkbook()
        val sheet = wb.createSheet("Table")
        rows.forEachIndexed { i, row ->
            val r = sheet.createRow(i)
            row.forEachIndexed { j, cell -> r.createCell(j).setCellValue(cell) }
        }
        java.io.FileOutputStream(outPath).use { wb.write(it); wb.close() }
    }
}


package com.browserhelper
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
object ZipManager {
    fun zipFiles(files: List<File>, outFile: File){
        ZipOutputStream(BufferedOutputStream(FileOutputStream(outFile))).use { zos ->
            files.forEach { f ->
                FileInputStream(f).use { fis ->
                    zos.putNextEntry(ZipEntry(f.name))
                    fis.copyTo(zos)
                    zos.closeEntry()
                }
            }
        }
    }
}

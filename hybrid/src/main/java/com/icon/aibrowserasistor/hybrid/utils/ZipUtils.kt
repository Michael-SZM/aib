package com.icon.aibrowserasistor.hybrid.utils

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

/**
 * Zip 解压工具类
 */
object ZipUtils {
    fun unzip(zipFile: File, targetDir: File): Boolean {
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }
        return try {
            ZipInputStream(FileInputStream(zipFile)).use { zis ->
                var entry = zis.nextEntry
                while (entry != null) {
                    val file = File(targetDir, entry.name)
                    if (entry.isDirectory) {
                        file.mkdirs()
                    } else {
                        file.parentFile?.mkdirs()
                        BufferedOutputStream(FileOutputStream(file)).use { bos ->
                            val buffer = ByteArray(1024 * 4)
                            var len: Int
                            while (zis.read(buffer).also { len = it } != -1) {
                                bos.write(buffer, 0, len)
                            }
                        }
                    }
                    zis.closeEntry()
                    entry = zis.nextEntry
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

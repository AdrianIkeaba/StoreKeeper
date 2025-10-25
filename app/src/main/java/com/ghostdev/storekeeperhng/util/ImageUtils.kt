package com.ghostdev.storekeeperhng.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ImageUtils {
    fun compressAndSave(context: Context, input: ByteArray, fileName: String = "IMG_${System.currentTimeMillis()}.jpg", quality: Int = 80): String {
        val dir = File(context.filesDir, "images").apply { if (!exists()) mkdirs() }
        val file = File(dir, fileName)
        val bitmap = BitmapFactory.decodeByteArray(input, 0, input.size)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(50, 95), out)
        }
        return file.absolutePath
    }

    fun compressAndSave(context: Context, uri: Uri, fileName: String = "IMG_${System.currentTimeMillis()}.jpg", quality: Int = 80): String? {
        return try {
            val stream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = stream?.use { it.readBytes() } ?: return null
            compressAndSave(context, bytes, fileName, quality)
        } catch (e: Exception) {
            null
        }
    }

    fun rotateInPlace(filePath: String, degrees: Float = 90f, quality: Int = 85): Boolean {
        return try {
            val src = BitmapFactory.decodeFile(filePath) ?: return false
            val matrix = android.graphics.Matrix().apply { postRotate(degrees) }
            val rotated = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)
            FileOutputStream(File(filePath)).use { out ->
                rotated.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(50, 95), out)
            }
            if (rotated != src) src.recycle()
            rotated.recycle()
            true
        } catch (e: Exception) {
            false
        }
    }
}
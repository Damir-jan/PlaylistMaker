package com.practicum.playlistmaker.library.data.db.repositoryImpl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import androidx.core.net.toUri
import com.practicum.playlistmaker.library.domain.repository.LocalStorageRepository
import java.io.File
import java.io.FileOutputStream

class LocalStorageRepositoryImpl(private val context: Context) : LocalStorageRepository {

    companion object {
        const val FILE_NAME = "playlist_cover.jpg"
        const val DIRECTORY_NAME = "playlists"
    }

    override fun saveImageToLocalStorage(uri: Uri): String {
        val fileName = generateUniqueFileName()

        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), DIRECTORY_NAME)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, fileName)
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file.toUri().toString()
    }
    private fun generateUniqueFileName(): String {
        return "playlist_cover_${System.currentTimeMillis()}.jpg"
    }
}

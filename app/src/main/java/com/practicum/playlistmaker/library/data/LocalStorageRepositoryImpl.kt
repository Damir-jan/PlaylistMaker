package com.practicum.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.practicum.playlistmaker.library.domain.repository.LocalStorageRepository
import java.io.File
import java.io.FileOutputStream

class LocalStorageRepositoryImpl(private val context: Context) : LocalStorageRepository {

    companion object {
        const val FILE_NAME = "playlist_cover.jpg"
        const val DIRECTORY_NAME = "playlists"
    }

    override fun saveImageToLocalStorage(uri: Uri) {

        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), DIRECTORY_NAME)
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, FILE_NAME)  ///////нужны разные названия для разных плейлистов?
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = context.contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }
}
//удалить комменты в конце
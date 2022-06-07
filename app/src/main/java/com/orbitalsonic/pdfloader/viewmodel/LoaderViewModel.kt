package com.orbitalsonic.pdfloader.viewmodel

import android.app.Application
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.orbitalsonic.pdfloader.datamodel.FileItem
import com.orbitalsonic.pdfloader.utils.GeneralUtils.getDate
import com.orbitalsonic.pdfloader.utils.GeneralUtils.getFileSize
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

class LoaderViewModel(application: Application) : AndroidViewModel(application) {
    private val mTag = "loaderViewModel"
    var fileList:ArrayList<FileItem> = ArrayList()
    private var isFileFetching = false

    private val handler = CoroutineExceptionHandler { _, exception ->
        Log.e(mTag, "CoroutineExceptionHandler: $exception")
    }

    /**
    0 = Sort By Date
    1 = Sort By Name
    2 = Sort By Size
     */

    fun fetchPdfFiles(sortType:Int,callback: () -> Unit){
        if (fileList.isEmpty() && !isFileFetching){
            viewModelScope.launch(Dispatchers.Main + handler) {
                async(Dispatchers.IO + handler) {
                    Log.d(mTag, "fetchPdfFiles is called")
                    isFileFetching = true
                    val projection = arrayOf(
                        MediaStore.Files.FileColumns._ID,
                        MediaStore.Files.FileColumns.DATA,
                        MediaStore.Files.FileColumns.DISPLAY_NAME,
                        MediaStore.Files.FileColumns.DATE_ADDED,
                        MediaStore.Files.FileColumns.DATE_MODIFIED,
                        MediaStore.Files.FileColumns.SIZE
                    )

                    val mimeTypePdf = "application/pdf"
                    val whereClause = MediaStore.Files.FileColumns.MIME_TYPE + " IN ('" + mimeTypePdf + "')"
                    var orderBy = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
                    when (sortType) {
                        0 -> {
                            orderBy = MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC"
                        }
                        1 -> {
                            orderBy = MediaStore.Files.FileColumns.DISPLAY_NAME
                        }
                        2 -> {
                            orderBy = MediaStore.Files.FileColumns.SIZE + " DESC"
                        }
                    }

                    val cursor: Cursor? = getApplication<Application>().contentResolver.query(
                        MediaStore.Files.getContentUri("external"),
                        projection,
                        whereClause,
                        null,
                        orderBy
                    )

                    cursor?.let {
                        //val idCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
                        val dataCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
                        val addedCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_ADDED)
                        val modifiedCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)
                        val sizeCol = it.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)

                        if (it.moveToFirst()) {
                            do {
                                //val fileUri: Uri = Uri.withAppendedPath(MediaStore.Files.getContentUri("external"), it.getString(idCol))
                                val data = it.getString(dataCol)
                                val dateAdded = it.getLong(addedCol)
                                val dateModified = it.getLong(modifiedCol)
                                val size = it.getLong(sizeCol)
                                val file = File(data)
                                if (file.exists()){
                                    fileList.add(
                                        FileItem(
                                            pdfFilePath = file,
                                            fileName = file.name.replace(".pdf", ""),
                                            dateCreatedName = getDate(dateAdded * 1000),
                                            dateModifiedName = getDate(dateModified * 1000),
                                            sizeName = getFileSize(size),
                                            originalDateCreated = dateAdded,
                                            originalDateModified = dateModified,
                                            originalSize = size
                                        )
                                    )
                                }

                            } while (it.moveToNext())
                        }
                        it.close()
                    }
                }.await()
                callback.invoke()
                isFileFetching = false
                Log.d(mTag, "invoke")
            }
        }else{
            callback.invoke()
        }

    }

    fun isFilesListEmpty() = fileList.isEmpty()

    fun cleanList(){
        fileList.clear()
        fileList = ArrayList()
    }
}
package com.orbitalsonic.pdfloader.manager

import android.annotation.SuppressLint
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.orbitalsonic.pdfloader.datamodel.FileItem
import kotlinx.coroutines.*
import java.io.File
import java.util.*
import kotlin.collections.ArrayList

class LoaderManager {

    companion object {

        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }

        val pdfFileList = ArrayList<FileItem>()


        fun getFilesLoaderAndroidR(
            activity: AppCompatActivity,
            projection: Array<String>,
            uri: Uri,
            selection: String?,
            orderBy: String,
            id: Int,
            callback: () -> Unit
        ) {
            val loaderCallbacks = object : LoaderManager.LoaderCallbacks<Cursor> {
                override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

                    return CursorLoader(
                        activity,
                        uri,
                        projection,
                        selection,
                        null,
                        orderBy
                    )
                }

                @SuppressLint("Range")
                override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                    if (data == null)
                        Log.d("DEBUG_TAG", "onLoadFinished: Cursor is NULL")
                    GlobalScope.launch(Dispatchers.Main + handler) {
                        async(Dispatchers.IO + handler) {

                            while (data!!.moveToNext()) {
                                val path: String = data.getString(0)
                                // your code logic should be here
                                val file = File(path)
                                pdfFileList.add(FileItem(file))
                            }
                            data.close()

                        }.await()
                        callback.invoke()
                        LoaderManager.getInstance(activity).destroyLoader(id)
                    }
                }

                override fun onLoaderReset(loader: Loader<Cursor>) {

                }
            }

            activity.runOnUiThread {
                LoaderManager.getInstance(activity).initLoader(id, null, loaderCallbacks)
            }

        }

        fun getFilesLoader(
            callback: () -> Unit
        ) {
            GlobalScope.launch(Dispatchers.Main + handler) {
                async(Dispatchers.IO + handler) {
                    getFiles(Environment.getExternalStorageDirectory())
                }.await()
                callback.invoke()
            }

        }

        private fun getFiles(dir: File) {
            try {
                val pdfPattern = ".pdf"
                val listFile = dir.listFiles()
                if (listFile != null && listFile.isNotEmpty()) {
                    for (file in listFile) {
                        if (file.isDirectory) {
                            getFiles(file)
                        }
                    }
                    Arrays.sort(
                        listFile
                    ) { file: File, t1: File ->
                        file.lastModified().compareTo(t1.lastModified())
                    }
                    for (file in listFile) {
                        if (file.exists() && file.name.endsWith(pdfPattern)) {
                            pdfFileList.add(FileItem(file))
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        fun isFilesListEmpty() = pdfFileList.isEmpty()

        fun clearFilesData() {
            pdfFileList.clear()
        }

    }
}
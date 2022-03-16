package com.orbitalsonic.pdfloader.manager

import android.os.Environment
import com.orbitalsonic.pdfloader.datamodel.FileItem
import kotlinx.coroutines.*
import java.io.File
import java.util.*

class LoaderManager {

    companion object {

        val handler = CoroutineExceptionHandler { _, exception ->
            exception.printStackTrace()
        }

        val pdfFileList = ArrayList<FileItem>()


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
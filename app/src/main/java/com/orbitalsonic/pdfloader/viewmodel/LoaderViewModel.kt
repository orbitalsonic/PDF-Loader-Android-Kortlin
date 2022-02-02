package com.orbitalsonic.pdfloader.viewmodel

import android.app.Activity
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.orbitalsonic.pdfloader.manager.LoaderManager
import com.orbitalsonic.pdfloader.utils.Constants.PDF_CODE

class LoaderViewModel : ViewModel() {

    private val _filesLoad = MutableLiveData<Boolean>()
    var isFilesLoaded: LiveData<Boolean> = Transformations.map(_filesLoad) { it }

    fun setFilesLoaded(isLoaded: Boolean) {
        _filesLoad.value = isLoaded
    }

    fun getGalleryFilesAndroidR(context: Activity) {
        if (LoaderManager.isFilesListEmpty()) {
            val selection =  "_data LIKE '%.pdf'"
            val documentsUri = MediaStore.Files.getContentUri("external")
            val projection = arrayOf(
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Files.FileColumns.MIME_TYPE
            )

            LoaderManager.getFilesLoaderAndroidR(context as AppCompatActivity,
                projection,
                documentsUri,
                selection,
                MediaStore.Images.Media.DATE_MODIFIED + " DESC",
                PDF_CODE,) {

                setFilesLoaded(true)
            }
        } else {
            setFilesLoaded(true)
        }
    }

    fun getGalleryFiles() {
        if (LoaderManager.isFilesListEmpty()) {
            LoaderManager.getFilesLoader {
                setFilesLoaded(true)
            }
        } else {
            setFilesLoaded(true)
        }
    }

}
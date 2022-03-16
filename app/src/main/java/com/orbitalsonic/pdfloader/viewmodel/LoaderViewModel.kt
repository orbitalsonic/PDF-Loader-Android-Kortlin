package com.orbitalsonic.pdfloader.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.orbitalsonic.pdfloader.manager.LoaderManager

class LoaderViewModel : ViewModel() {

    private val _filesLoad = MutableLiveData<Boolean>()
    var isFilesLoaded: LiveData<Boolean> = Transformations.map(_filesLoad) { it }

    fun setFilesLoaded(isLoaded: Boolean) {
        _filesLoad.value = isLoaded
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
package com.orbitalsonic.pdfloader.datamodel

import java.io.File

data class FileItem(
    var pdfFilePath: File,
    var fileName: String,
    var dateCreatedName: String,
    var dateModifiedName: String,
    var sizeName: String,
    var originalDateCreated: Long,
    var originalDateModified: Long,
    var originalSize: Long
)
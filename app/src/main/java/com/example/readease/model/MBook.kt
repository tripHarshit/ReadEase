package com.example.readease.model

import com.google.firebase.Timestamp

data class MBook(
    val id: String,
    val startedReading: Timestamp?,
    val endedReading: Timestamp?,
    val volumeInfo: VolumeInfo,
)

data class VolumeInfo(
    val title: String,
    val authors: List<String>,
    val publisher: String,
    val publishedDate: String,
    val description: String,
    val pageCount: Int,
    val categories: List<String>,
    val imageLinks: ImageLinks? = null,
    val rating: Int,
)

data class ImageLinks(
    val smallThumbnail: String,
    val thumbnail: String
){
    val httpsThumbnail: String
        get() = thumbnail.replace("http://", "https://")
}

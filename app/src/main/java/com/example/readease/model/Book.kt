package com.example.readease.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class Book(
    @Exclude var id: String? = null,
    var title: String? = null,
    var authors: List<String> = emptyList(),
    var description: String? = null,
    var categories: List<String> = emptyList(),
    var rating: Int = 0,
    var notes: List<String> = emptyList(),

    @get:PropertyName("published_date")
    @set:PropertyName("published_date")
    var publishedDate: String? = null,

    @get:PropertyName("book_photo_url")
    @set:PropertyName("book_photo_url")
    var photoUrl: String? = null,

    @get:PropertyName("page_count")
    @set:PropertyName("page_count")
    var pageCount: Int? = null,

    @get:PropertyName("started_reading_at")
    @set:PropertyName("started_reading_at")
    var startedReading: Timestamp? = null,

    @get:PropertyName("ended_reading_at")
    @set:PropertyName("ended_reading_at")
    var endedReading: Timestamp? = null,

    @get:PropertyName("google_book_id")
    @set:PropertyName("google_book_id")
    var googleBookId: String? = null,

    @get:PropertyName("user_id")
    @set:PropertyName("user_id")
     var userId: String? = null,

    @get:PropertyName("date_added")
    @set:PropertyName("date_added")
    var dateAdded: String? = null
)
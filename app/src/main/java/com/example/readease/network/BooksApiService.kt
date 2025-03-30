package com.example.readease.network

import com.example.readease.model.BookResponse
import com.example.readease.model.MBook
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query



interface BooksApiService {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("maxResults") maxResults: Int = 15
    ): Response<BookResponse>

    @GET("volumes/{bookId}")
    suspend fun getBookInfo(
        @Path("bookId") bookId: String
    ): MBook

}
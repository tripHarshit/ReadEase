package com.example.readease.repository

import androidx.annotation.RestrictTo
import com.example.readease.model.BookResponse
import com.example.readease.model.MBook
import com.example.readease.network.BooksApiService
import retrofit2.Response

interface BooksDataRepository {
    suspend fun getBooks(query: String,maxResults: Int): Response<BookResponse>
    suspend fun getBookInfo(path: String): MBook
}

class DefaultBooksDataRepository(
       private val booksApiService: BooksApiService
): BooksDataRepository{
    override suspend fun getBooks(query: String,maxResults: Int): Response<BookResponse> {
        return  booksApiService.getBooks(query, maxResults )
    }

    override suspend fun getBookInfo(path: String): MBook {
       return booksApiService.getBookInfo(path)
    }
}
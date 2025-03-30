package com.example.readease.di

import com.example.readease.network.BooksApiService
import com.example.readease.repository.BooksDataRepository
import com.example.readease.repository.DefaultBooksDataRepository
import com.example.readease.repository.DefaultFireRepository
import com.example.readease.repository.FireRepository
import com.google.firebase.firestore.FirebaseFirestore
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val booksDataRepository: BooksDataRepository
    val fireRepository: FireRepository
}

class DefaultAppContainer : AppContainer {

    object RetrofitClient {
        private const val BASE_URL = "https://www.googleapis.com/books/v1/"

        private val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitService: BooksApiService by lazy {
            retrofit.create(BooksApiService::class.java)
        }
    }

    override val booksDataRepository: BooksDataRepository by lazy {
        DefaultBooksDataRepository(RetrofitClient.retrofitService)

    }
    override val fireRepository: FireRepository by lazy {
        DefaultFireRepository(FirebaseFirestore.getInstance())
    }
}

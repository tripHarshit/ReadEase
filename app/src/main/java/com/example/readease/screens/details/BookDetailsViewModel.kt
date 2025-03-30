package com.example.readease.screens.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.readease.ReadEaseApplication
import com.example.readease.model.MBook
import com.example.readease.repository.BooksDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BookDetailsViewModel(
    private val booksDataRepository: BooksDataRepository
) : ViewModel() {

    // Make this function suspend to return a value properly
    suspend fun getBookById(bookId: String): MBook {
        return withContext(Dispatchers.IO) {
            booksDataRepository.getBookInfo(bookId)
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as ReadEaseApplication)
                val booksDataRepository = application.container.booksDataRepository
                BookDetailsViewModel(booksDataRepository = booksDataRepository)
            }
        }
    }
}

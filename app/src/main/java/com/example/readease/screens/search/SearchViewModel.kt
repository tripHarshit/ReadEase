package com.example.readease.screens.search

import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.readease.ReadEaseApplication
import com.example.readease.components.fetchBooks
import com.example.readease.model.MBook
import com.example.readease.repository.BooksDataRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
class SearchViewModel(
    private val booksDataRepository: BooksDataRepository
) : ViewModel() {

    var initialBooksList = mutableStateListOf<MBook>()
        private set

    var searchedBooksList = mutableStateListOf<MBook>()
        private set

    var noBooksFound by mutableStateOf(false)
        private set

    init {
        loadBooks()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun loadBooks() {
        searchByQuery(query = "comics")
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun searchByQuery(
        query: String
    ) {
        searchedBooksList.clear()
        viewModelScope.launch {
            try {
                fetchBooks(query, searchedBooksList, booksDataRepository, viewModelScope)
                noBooksFound = searchedBooksList.isEmpty()
            } catch (e: IOException) {
                noBooksFound = true
            } catch (e: HttpException) {
                noBooksFound = true
            }
        }
    }

    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ReadEaseApplication)
                val booksDataRepository = application.container.booksDataRepository
                SearchViewModel(booksDataRepository)
            }
        }
    }
}

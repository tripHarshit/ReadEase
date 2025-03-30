package com.example.readease.screens.search

import android.os.Build
import androidx.annotation.OptIn
import androidx.annotation.RequiresExtension
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.readease.ReadEaseApplication
import com.example.readease.components.fetchBooks
import com.example.readease.model.Book
import com.example.readease.model.MBook
import com.example.readease.repository.BooksDataRepository
import com.example.readease.repository.FireRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import retrofit2.HttpException
import java.io.IOException

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
class HomeScreenViewModel(
    private val booksDataRepository: BooksDataRepository,
    private val fireRepository: FireRepository,
) : ViewModel() {

    var bestSellerBookList = mutableStateListOf<MBook>()
        private set
    var fictionBookList = mutableStateListOf<MBook>()
        private set
    var magazineList = mutableStateListOf<MBook>()
        private set
    var newBookList = mutableStateListOf<MBook>()
        private set
    var historyBooksList = mutableStateListOf<MBook>()
        private set
    var awardedBooksList = mutableStateListOf<MBook>()

    private val _readingList = MutableStateFlow<List<Book>>(emptyList())
    val getReadingList: StateFlow<List<Book>> = _readingList.asStateFlow()

    private val _book  = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book.asStateFlow()


    init {
        loadBooks()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    private fun loadBooks() {
        getBestSellers(query = "popular books")
        getMagazines(query = "magazines")
        newAdditions(query = "new")
        getFictionBooks(query = "fiction")
        getHistoryBooks(query = "history")
        getAwardedBooks(query = "Pulitzer Prize")
        fetchReadingBooks()
    }

    private fun getAwardedBooks(query: String) {
        fetchBooks(
            query = query, bookList =  awardedBooksList,
            booksDataRepository = booksDataRepository,
            coroutineScope = viewModelScope
        )
    }

    @OptIn(UnstableApi::class)
    private fun getHistoryBooks(query: String) {

        fetchBooks(
            query = query, bookList =  historyBooksList,
            booksDataRepository = booksDataRepository,
            coroutineScope = viewModelScope
        )
    }

    private fun newAdditions(query: String) {
        fetchBooks(
            query = query, bookList =  newBookList,
            booksDataRepository = booksDataRepository,
            coroutineScope = viewModelScope
        )
    }

    fun getMagazines(query: String) {
        fetchBooks(
            query = query, bookList =  magazineList,
            booksDataRepository = booksDataRepository,
            coroutineScope = viewModelScope
        )
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    fun getBestSellers(query: String) {
        fetchBooks(
            query = query, bookList =  bestSellerBookList,
            booksDataRepository = booksDataRepository,
            coroutineScope = viewModelScope
        )
    }
    fun getFictionBooks(query: String) {
        fetchBooks(
            query = query, bookList =  fictionBookList,
            booksDataRepository = booksDataRepository,
            coroutineScope = viewModelScope
        )
    }

    @OptIn(UnstableApi::class)
    private fun fetchReadingBooks() {
        viewModelScope.launch(Dispatchers.IO) {
            delay(5000)
            fireRepository.getAllBooksFlow().collect { books ->
                _readingList.value = books
                Log.d("ReadingListDebug", "Book: ${book.value?.title}, Image: ${book.value?.photoUrl}")
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun getBookById(bookId: String){
        viewModelScope.launch(Dispatchers.IO) {
            val fetchedBook = fireRepository.getBookById(bookId)
            _book.value = fetchedBook
        }
    }


    companion object {
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as ReadEaseApplication)
                val booksDataRepository = application.container.booksDataRepository
                val fireRepository = application.container.fireRepository
                HomeScreenViewModel(booksDataRepository = booksDataRepository,fireRepository = fireRepository)
            }
        }
    }
}

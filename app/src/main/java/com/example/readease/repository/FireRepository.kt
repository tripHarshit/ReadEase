package com.example.readease.repository

import android.util.Log
import androidx.compose.animation.core.snap
import com.example.readease.model.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await

// Interface for Firestore operations
interface FireRepository {
    suspend fun getBookById(bookId: String): Book?
     fun getAllBooksFlow(): StateFlow<List<Book>>
}

// Default implementation of Firestore repository
class DefaultFireRepository(private val db: FirebaseFirestore) : FireRepository {
    private val bookCollection = db.collection("books")
    private val _bookList = MutableStateFlow<List<Book>>(emptyList())
    var bookList: StateFlow<List<Book>> = _bookList.asStateFlow()

    init {
        getAllBooks() // Start listening to Firestore changes
    }

    override suspend fun getBookById(bookId: String): Book? {
        return try {
            val doc = bookCollection.document(bookId).get().await()
            doc.toObject(Book::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Error fetching book", e)
            null
        }
    }

    private fun getAllBooks() {
        bookCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("Firestore", "Firestore Listener Error: ${e.message}")
                return@addSnapshotListener
            }

            if (snapshot != null && !snapshot.isEmpty) {
                _bookList.value = snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
                Log.d("Firestore", "Books updated: ${_bookList.value.size}")
            } else {
                _bookList.value = emptyList()
            }
        }
    }

    override fun getAllBooksFlow(): StateFlow<List<Book>> {
         return bookList
    }
}

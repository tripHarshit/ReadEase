package com.example.readease.screens.details

import android.annotation.SuppressLint
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.readease.model.Book
import com.example.readease.model.MBook
import com.example.readease.screens.search.SearchAppBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalCoilApi::class)
@Composable
fun BookDetailsScreen(navController: NavController, bookId: String, viewModel: BookDetailsViewModel) {
    var book by remember { mutableStateOf<MBook?>(null) }
    val scrollState = rememberScrollState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(bookId) {
        book = viewModel.getBookById(bookId)
    }
    Scaffold(
        topBar = { SearchAppBar(title = "Know Your Book", navController) }
    ) {

        if (book == null) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surfaceDim) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(250.dp)
                    )
                }
            }
            return@Scaffold
        }

        Surface(modifier = Modifier.fillMaxSize().padding(paddingValues = it),
            color = MaterialTheme.colorScheme.surfaceDim) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {

                Image(
                    painter = rememberImagePainter(
                        data = book?.volumeInfo?.imageLinks?.httpsThumbnail ?: "default_image_url"
                    ),
                    contentDescription = "Book Cover",
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .height(250.dp)
                        .width(180.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val context = LocalContext.current
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(modifier = Modifier.verticalScroll(scrollState)) {
                        Text(
                            text = book?.volumeInfo?.title ?: "Unknown Title",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "by ${book?.volumeInfo?.authors?.joinToString(", ") ?: "Unknown"}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val bookDescription = HtmlCompat.fromHtml(
                            book?.volumeInfo?.description ?: "No description available",
                            HtmlCompat.FROM_HTML_MODE_LEGACY
                        ).toString()

                        Text(
                            text = bookDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                            modifier = Modifier.clickable { expanded = !expanded }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "[${book?.volumeInfo?.categories?.joinToString(", ") ?: "Unknown"}]",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(onClick = {
                                val bookToSave = Book(
                                    title = book?.volumeInfo?.title ?: "Unknown",
                                    authors = book?.volumeInfo?.authors ?: emptyList(),
                                    description = book?.volumeInfo?.description ?: "No description available",
                                    categories = book?.volumeInfo?.categories ?: emptyList(),
                                    photoUrl = book?.volumeInfo?.imageLinks?.httpsThumbnail ?: "",
                                    publishedDate = book?.volumeInfo?.publishedDate ?: "Unknown",
                                    googleBookId = book?.id ?: "",
                                    userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                )
                                saveToFirebase(bookToSave, navController)
                                Toast.makeText(context,"Book Added Successfully", Toast.LENGTH_SHORT).show()
                            }) {
                                Text("Add")
                            }

                            Button(onClick = { navController.popBackStack() }) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            }
        }
    }

}

fun saveToFirebase(book: Book, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val dbCollection = db.collection("books")


    dbCollection.add(book)
        .addOnSuccessListener { docRef ->
            val docId = docRef.id
            dbCollection.document(docId)
                .update("id", docId)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        navController.popBackStack()
                    }
                }.addOnFailureListener {
                    Log.d("firebase", "Error updating document ID")
                }
        }.addOnFailureListener {
            Log.d("firebase", "Error adding book to Firestore")
        }
}

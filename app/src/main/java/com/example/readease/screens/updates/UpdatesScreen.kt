package com.example.readease.screens.updates

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.example.readease.R
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.readease.components.RatingBar
import com.example.readease.components.ReviewBox
import com.example.readease.model.ImageLinks
import com.example.readease.model.MBook
import com.example.readease.model.VolumeInfo
import com.example.readease.navigation.ReadEaseScreens
import com.example.readease.screens.details.BookDetailsViewModel
import com.example.readease.screens.search.HomeScreenViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

@androidx.annotation.OptIn(UnstableApi::class)
@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UpdatesScreen(
    navController: NavController,
    bookId: String,
    homeScreenViewModel: HomeScreenViewModel,
    bookDetailsViewModel: BookDetailsViewModel,
) {
    val isReading = remember { mutableStateOf(false) }
    val isFinished = remember { mutableStateOf(false) }
    val ratingVal = remember { mutableIntStateOf(0) }
    val reviewText = remember { mutableStateOf(TextFieldValue("")) }
    val scrollState = rememberScrollState()
    val reviews = remember { mutableStateListOf<String>() }

    // Fetch book details
    LaunchedEffect(bookId) {
        homeScreenViewModel.getBookById(bookId)
    }


    val book by homeScreenViewModel.book.collectAsState()

// Update 'reviews' whenever 'book' changes
    LaunchedEffect(book) {
        book?.notes?.let { newNotes ->
            reviews.clear()  // Clear existing comments
            reviews.addAll(newNotes)  // Add latest comments
        }
    }

    val mBook: MBook? = book?.let {
        MBook(
            id = it.id ?: "",
            startedReading = book?.startedReading,
            endedReading = book?.endedReading,
            volumeInfo = VolumeInfo(
                title = it.title ?: "Unknown Title",
                authors = it.authors.ifEmpty { listOf("Unknown Author") },
                publisher = "Unknown Publisher",
                publishedDate = it.publishedDate ?: "N/A",
                description = it.description ?: "No description available",
                pageCount = it.pageCount ?: 0,
                categories = it.categories.ifEmpty { listOf("Uncategorized") },
                rating = book!!.rating,
                imageLinks = if (!it.photoUrl.isNullOrEmpty()) {
                    ImageLinks(it.photoUrl.toString(), it.photoUrl.toString())
                } else {
                    null
                }
            )
        )
    }

    Scaffold(
        topBar = { SearchAppBar(title = "Reading Journal", navController) }
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceDim,
            modifier = Modifier.padding(it)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                UpdateScreenCard(mBook, navController)

                Spacer(modifier = Modifier.height(30.dp))

                CommentBox(reviewText = reviewText, reviews = reviews)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = { isReading.value = true },
                        modifier = Modifier.weight(1f),
                        enabled = book?.startedReading == null
                    ) {
                        Text(
                            text = "Started On: ${book?.startedReading?.let { formatDate(it) } ?: "Not started"}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(if (isReading.value) 0.5f else 1f)
                        )
                    }

                    Spacer(modifier = Modifier.width(48.dp))

                    TextButton(
                        onClick = { isFinished.value = true },
                        modifier = Modifier.weight(1f),
                        enabled = book?.endedReading == null
                    ) {
                        Text(
                            text = "Finished On: ${book?.endedReading?.let { formatDate(it) } ?: "Not finished"}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.tertiaryContainer.copy(if (isFinished.value) 0.5f else 1f)
                        )
                    }
                }

                Text(
                    text = "RATINGS",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                )

                book?.rating?.toInt()?.let {
                    RatingBar(rating = it) { rating ->
                        ratingVal.intValue = rating
                    }

                    Row(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                        val changedNotes = book?.notes != reviews.toList()
                        val changedRating = book?.rating != ratingVal.intValue

                        // Correct timestamp handling
                        val isFinishedTimeStamp: Timestamp? = if (isFinished.value) Timestamp.now() else book?.endedReading
                        val startedTimeStamp: Timestamp? = if (isReading.value) Timestamp.now() else book?.startedReading

                        val bookUpdate = changedNotes || changedRating || isReading.value || isFinished.value
                        val bookToUpdate = mutableMapOf<String, Any?>()


                        if (isFinished.value || book?.endedReading != null) {
                            bookToUpdate["ended_reading_at"] = isFinishedTimeStamp
                        }
                        if (isReading.value || book?.startedReading != null) {
                            bookToUpdate["started_reading_at"] = startedTimeStamp
                        }

                        if (changedRating) bookToUpdate["rating"] = ratingVal.intValue
                        if (changedNotes) bookToUpdate["notes"] = reviews.toList()

                        bookToUpdate.entries.removeIf { it.value == null }

                        Spacer(modifier = Modifier.width(30.dp))

                        val context = LocalContext.current
                        Button(
                            modifier = Modifier.padding(8.dp),
                            onClick = {
                                if (bookUpdate && book?.id != null) {
                                    val bookRef = FirebaseFirestore.getInstance()
                                        .collection("books")
                                        .document(book!!.id!!)

                                    bookRef.update(bookToUpdate)
                                        .addOnCompleteListener {
                                           Toast.makeText(context,"Book Updated Successfully", Toast.LENGTH_SHORT).show()

                                            navController.navigate(ReadEaseScreens.ReaderHomeScreen.name){
                                                popUpTo(0) { inclusive = true }
                                            }

                                            homeScreenViewModel.getBookById(book!!.id!!)
                                        }
                                        .addOnFailureListener {
                                            Log.w("Update", "Error updating book", it)
                                        }
                                }
                            }) {
                            Text(text = "Update")
                        }

                        Spacer(modifier = Modifier.width(100.dp))

                        val openDialog = remember { mutableStateOf(false) }
                        if(openDialog.value){
                            ShowAlertBox(message = stringResource(R.string.action) + "\n" + stringResource(R.string.sure),
                                openDialog ) {
                                FirebaseFirestore.getInstance()
                                    .collection("books")
                                    .document(bookId)
                                    .delete()
                                    .addOnCompleteListener {
                                        if(it.isSuccessful){
                                            openDialog.value = false
                                            navController.navigate(ReadEaseScreens.ReaderHomeScreen.name)
                                        }
                                    }
                            }

                        }
                        Button(modifier = Modifier.padding(8.dp), onClick = {openDialog.value = true}) {
                            Text(text = "Delete")
                        }
                    }
                }
            }
        }
    }
}





@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAppBar(title: String, navController: NavController) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back arrow",
                    tint = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { navController.popBackStack() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceDim
        )
    )
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun UpdateScreenCard(
    book: MBook?,
    navController: NavController,
){

    Card(modifier = Modifier
        .width(400.dp)
        .height(170.dp)
        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
        .clickable(onClick = {
            navController.navigate("${ReadEaseScreens.DetailsScreen.name}/${book?.id}")
        }),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(7.dp)) {

        Row (modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically){
            Card(shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(140.dp)
                    .width(90.dp)
                    .padding(start = 2.dp, top = 2.dp, bottom = 2.dp, end = 6.dp)
            ) {
                Image(
                    painter = rememberImagePainter(data = book?.volumeInfo?.imageLinks?.httpsThumbnail),
                    contentDescription = "book Poster",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly) {
                Text(
                    text = book?.volumeInfo?.title.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding( bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Authors: ${book?.volumeInfo?.authors?.joinToString(",")?:"Unknown"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "[${book?.volumeInfo?.publishedDate?:"Unknown"}]",
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

        }
    }

}

@Composable
fun CommentBox(
    reviewText: MutableState<TextFieldValue>,
    reviews: MutableList<String>,
){
    ReviewBox(reviews = reviews, reviewText = reviewText )
}

fun formatDate(timestamp: Timestamp?): String {
    return timestamp?.toDate()?.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
    } ?: "N/A"
}

@Composable
fun ShowAlertBox(
    message: String,
    openDialog: MutableState<Boolean>,
    onYesPressed: () -> Unit
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = { openDialog.value = false },
            title = { Text(text = "Delete Book",
                color = MaterialTheme.colorScheme.primary) },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(onClick = {
                    onYesPressed.invoke() // Execute action only when "Yes" is pressed
                    openDialog.value = false
                }) {
                    Text("Yes",
                        color = MaterialTheme.colorScheme.tertiaryContainer)
                }
            },
            dismissButton = {
                TextButton(onClick = { openDialog.value = false }) {
                    Text("No",
                        color = MaterialTheme.colorScheme.tertiaryContainer)
                }
            }
        )
    }
}

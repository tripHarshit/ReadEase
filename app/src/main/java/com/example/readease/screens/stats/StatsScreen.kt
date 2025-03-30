package com.example.readease.screens.stats

import android.R
import android.annotation.SuppressLint
import android.os.Build
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.readease.model.ImageLinks
import com.example.readease.model.MBook
import com.example.readease.model.VolumeInfo
import com.example.readease.navigation.ReadEaseScreens
import com.example.readease.screens.home.ReaderAppBar
import com.example.readease.screens.search.HomeScreenViewModel
import com.example.readease.screens.search.SearchAppBar
import com.example.readease.screens.search.SearchScreenCard
import com.example.readease.screens.search.SearchViewModel
import com.example.readease.screens.updates.formatDate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale
import kotlin.collections.ifEmpty

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StatsScreen(navController: NavController,
                homeViewModel: HomeScreenViewModel){
    val currentUser =  FirebaseAuth.getInstance().currentUser
    val email = FirebaseAuth.getInstance().currentUser?.email
    val currentUserName = if(!email.isNullOrEmpty()){
        FirebaseAuth.getInstance().currentUser?.email?.split("@")?.get(0)
    }else
        "N/A"
    val readingList = homeViewModel.getReadingList.collectAsState(initial = emptyList())

    val filteredReadingList = readingList.value
        .filter { book -> book.userId == currentUser?.uid }
        .map { book ->
            MBook(
                id = book.id ?: "",
                startedReading = book.startedReading,
                endedReading = book.endedReading,
                volumeInfo = VolumeInfo(
                    title = book.title ?: "Unknown Title",
                    authors = book.authors.ifEmpty { listOf("Unknown Author") },
                    publisher = "Unknown Publisher",
                    publishedDate = book.publishedDate ?: "N/A",
                    description = book.description ?: "No description available",
                    pageCount = book.pageCount ?: 0,
                    rating = book.rating,
                    categories = book.categories.ifEmpty { listOf("Uncategorized") },
                    imageLinks = ImageLinks(book.photoUrl!!, book.photoUrl!!)
                )
            )
        }
    val readBooksList = filteredReadingList.filter { book ->
        book.startedReading != null && book.endedReading != null
    }
    val continueBooksList = filteredReadingList.filter { book ->
        book.startedReading != null && book.endedReading == null
    }
    Scaffold(topBar = {
        SearchAppBar(title = "Your Stats",navController)
    }) {
        Surface(color = MaterialTheme.colorScheme.surfaceDim,
            modifier = Modifier
                .padding(paddingValues = it)
                .fillMaxSize()) {

            Column (modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Top){
                Text(
                    text = "Hi,  ${currentUserName?.uppercase() ?: "Guest"}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.tertiaryContainer
                )


                Spacer(modifier = Modifier.height(40.dp))

                Text(text = "You're Reading: ${continueBooksList.size} books",
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge)
                Text(text = "You've Read: ${readBooksList.size} books",
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(30.dp))

                ReadList(readBooksList,navController)
            }
        }
    }
}


@Composable
fun ReadList(bookList: List<MBook>,
               navController: NavController){
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ){
        items(bookList) {book->
            ReadScreenCard(
                book , navController
            )

        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun ReadScreenCard(book: MBook ,
                     navController: NavController
){

    Card(modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .padding( top = 16.dp)
        .clickable(onClick = {
            navController.navigate("${ReadEaseScreens.UpdateScreen.name}/${book.id}")
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
                    painter = rememberImagePainter(data = book.volumeInfo.imageLinks?.httpsThumbnail),
                    contentDescription = "book Poster",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = book.volumeInfo.title,
                        style = MaterialTheme.typography.headlineMedium,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (book.volumeInfo.rating >= 4) {
                        Icon(
                            imageVector = Icons.Default.ThumbUp,
                            contentDescription = "liked",
                            tint = MaterialTheme.colorScheme.tertiaryContainer,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }


                Text(
                    text = "Authors: [${book.volumeInfo.authors?.joinToString(",")?:"Unknown"}]",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Started On: ${formatDate(book.startedReading)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "Finished On: ${formatDate(book.endedReading)}",
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

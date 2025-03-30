package com.example.readease.screens.home

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.readease.R
import com.example.readease.components.BookRating
import com.example.readease.components.FABContent
import com.example.readease.components.TitleSection
import com.example.readease.model.ImageLinks
import com.example.readease.model.MBook
import com.example.readease.model.VolumeInfo
import com.example.readease.navigation.ReadEaseScreens
import com.example.readease.screens.search.HomeScreenViewModel
import com.google.firebase.auth.FirebaseAuth

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, homeViewModel: HomeScreenViewModel) {
  //  val isReadingListEmpty = remember { mutableStateOf(false) }

    val currentUser = FirebaseAuth.getInstance().currentUser

    val readingList = homeViewModel.getReadingList.collectAsState(initial = emptyList())

    val filteredReadingList = readingList.value // Access the actual list
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
         val homeReadingList = filteredReadingList
        .filter { book -> book.startedReading == null && book.endedReading == null }
        .distinctBy { it.id }
    val continueReadingList = filteredReadingList.filter { book ->
        book.startedReading != null && book.endedReading == null
    }

    Scaffold(
        topBar = { ReaderAppBar(title = "ReadEase", navController = navController) },
        containerColor = MaterialTheme.colorScheme.surfaceDim,
        floatingActionButton = { FABContent { navController.navigate(ReadEaseScreens.SearchScreen.name) } }
    ) { paddingValues ->
        Column {
                HomeContent(
                    modifier = Modifier.padding(paddingValues),
                    continueReadingList = continueReadingList,
                    readingBookList = homeReadingList,
                    bestSellersBookList = homeViewModel.bestSellerBookList,
                    fictionBookList = homeViewModel.fictionBookList,
                    magazinesList = homeViewModel.magazineList,
                    historyBooksList = homeViewModel.historyBooksList,
                    newAdditionsList = homeViewModel.newBookList,
                    awardedBooksList = homeViewModel.awardedBooksList,
                    navController = navController,
                    isHomeReadingListEmpty = homeReadingList.isEmpty()
                )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReaderAppBar(title: String, navController: NavController) {
    val context  = LocalContext.current
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile", tint = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { navController.navigate(ReadEaseScreens.ReaderStatsScreen.name) }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f)) // Dynamic spacing
            }
        },
        actions = {
            IconButton(onClick = {
                FirebaseAuth.getInstance().signOut().run {
                    navController.navigate(ReadEaseScreens.LoginScreen.name)
                    Toast.makeText(context,"You've been logged out!!", Toast.LENGTH_SHORT).show()
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.baseline_logout_24),
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.tertiaryContainer,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceDim
        )
    )
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    continueReadingList: List<MBook>,
    readingBookList: List<MBook>,
    bestSellersBookList: List<MBook>,
    fictionBookList: List<MBook>,
    newAdditionsList: List<MBook>,
    historyBooksList: List<MBook>,
    magazinesList: List<MBook>,
    awardedBooksList: List<MBook> ,
    navController: NavController,
    isHomeReadingListEmpty: Boolean
) {
    val homeScrollState = rememberScrollState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(homeScrollState),
        verticalArrangement = Arrangement.Top
    ) {
        if (continueReadingList.isNotEmpty()) {
            TitleSection(label = "Continue Reading")
            ReadingListArea(listOfBooks = continueReadingList, navController = navController)
        }

        Spacer(modifier = Modifier.height(20.dp))

        if(!isHomeReadingListEmpty) {

            TitleSection(label = "Reading List")
            ReadingListArea(listOfBooks = readingBookList, navController = navController)
        }

        TitleSection(label = "New Additions")
        BookListArea(listOfBooks = newAdditionsList, navController = navController )

        TitleSection(label = "Award-Winning Books")
        BookListArea(listOfBooks = awardedBooksList, navController = navController )

        TitleSection(label = "BestSellers")
        BookListArea(listOfBooks = bestSellersBookList, navController = navController )

        TitleSection(label = "Read Magazines")
        BookListArea(listOfBooks = magazinesList, navController = navController )

        TitleSection(label = "Explore Fiction")
        BookListArea(listOfBooks = fictionBookList, navController = navController )

        TitleSection(label = "Delve Into The History")
        BookListArea(listOfBooks = historyBooksList, navController = navController )





    }
}

@Composable
fun BookListArea(listOfBooks: List<MBook>,
                 navController: NavController) {
          HorizontalScrollableComponent(listOfBooks,navController)
}

@Composable
fun ReadingListArea(listOfBooks: List<MBook>,
                    navController: NavController,
){
    val scrollState = rememberScrollState()
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(280.dp)
        .horizontalScroll(scrollState)){

            for (book in listOfBooks) {
                ListCard(book,
                    { navController.navigate("${ReadEaseScreens.UpdateScreen.name}/${book.id}") })
            }


    }
}
@Composable
fun HorizontalScrollableComponent(listOfBooks: List<MBook>,navController: NavController) {

    val scrollState = rememberScrollState()
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(280.dp)
        .horizontalScroll(scrollState)){



            for (book in listOfBooks) {
                ListCard(book,
                    { navController.navigate("${ReadEaseScreens.DetailsScreen.name}/${book.id}") })
            }

    }
}


@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalCoilApi::class)
@Composable
fun ListCard(book: MBook,
             onPressDetails: (String) -> Unit = {},
             @SuppressLint("ModifierParameter") modifier: Modifier = Modifier){
    val context = LocalContext.current
    val resources  = context.resources
    val displayMetrics = resources.displayMetrics
    val screenWidth = displayMetrics.widthPixels / displayMetrics.density
    val spacing = 10.dp

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),

        modifier = modifier
            .padding(start = 8.dp, top = 8.dp, end = 8.dp)
            .height(242.dp)
            .width(202.dp)
            .clickable { onPressDetails.invoke(book.volumeInfo.title.toString()) }
    ) {
        Column(
            modifier = Modifier.width(screenWidth.dp - (spacing * 2)),
            horizontalAlignment = Alignment.Start
        ) {
            Row(horizontalArrangement = Arrangement.Center) {

                Image(
                    painter = rememberImagePainter(data = book.volumeInfo.imageLinks?.httpsThumbnail),
                    contentDescription = "book Poster",
                    modifier = Modifier
                        .height(140.dp)
                        .width(100.dp)
                        .padding(2.dp)
                )

                Spacer(modifier = Modifier.padding(25.dp))

                Column(
                    modifier = Modifier.padding(top = 25.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "favorites",
                        modifier = Modifier
                            .padding(4.dp)
                            .size(30.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )

                    BookRating(score = book.volumeInfo.rating)
                }
            }

            Text(
                text = book.volumeInfo.title.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = "Authors: ${book.volumeInfo.authors?.joinToString(",")?:"Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

}



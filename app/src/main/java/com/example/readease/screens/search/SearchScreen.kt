package com.example.readease.screens.search

import android.annotation.SuppressLint
import android.os.Build
import androidx.activity.compose.ReportDrawn
import androidx.annotation.RequiresExtension
import androidx.annotation.RequiresPermission
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.readease.model.MBook
import com.example.readease.navigation.ReadEaseScreens
import kotlin.collections.get

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
@androidx.annotation.OptIn(UnstableApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel
) {
    val searchQueryState = rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            SearchAppBar("Search Books", navController)
        }
    ) { paddingValues ->
        Surface (modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
            color = MaterialTheme.colorScheme.surfaceDim){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                    //.padding(paddingValues),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SearchForm(
                    modifier = Modifier.fillMaxWidth(),
                    hint = "Enter keyword (e.g. comics)",
                    searchQueryState = searchQueryState
                ) { query ->
                    viewModel.searchByQuery(query,)  // ✅ Perform search on query update
                }

                SearchList(
                    bookList = viewModel.searchedBooksList,  // ✅ Use ViewModel state
                    navController
                )
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    hint: String,
    searchQueryState: MutableState<String>,
    onSearch: (String) -> Unit = {},
) {
    Column(modifier = modifier) {
        val keyboardController = LocalSoftwareKeyboardController.current

        SearchBar(
            query = searchQueryState.value,
            onQueryChange = { searchQueryState.value = it },
            onSearch = { query ->
                onSearch(query.trim())
                searchQueryState.value = ""
                keyboardController?.hide()
            },
            active = false,
            onActiveChange = {},
            modifier = modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            enabled = !loading,
            placeholder = {
                Text(text = hint, color = MaterialTheme.colorScheme.onSurface.copy(alpha = .2f), style = MaterialTheme.typography.bodyMedium)
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon",tint = MaterialTheme.colorScheme.tertiaryContainer,)
            },
            trailingIcon = {
                if (searchQueryState.value.isNotEmpty()) {
                    IconButton(onClick = { searchQueryState.value = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear", tint = MaterialTheme.colorScheme.tertiaryContainer,)
                    }
                }
            },
            shape = RoundedCornerShape(20.dp),
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            tonalElevation = 6.dp,
            shadowElevation = 6.dp,
        ) { }
    }
}

@Composable
fun SearchList(bookList: List<MBook>,
               navController: NavController){
    LazyColumn (
        modifier = Modifier.fillMaxSize()
    ){
        items(bookList) {book->
            SearchScreenCard(
                book , navController
            )

        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
fun SearchScreenCard(book: MBook ,
                     navController: NavController
                    ){

    Card(modifier = Modifier
        .fillMaxWidth()
        .height(170.dp)
        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 0.dp)
        .clickable(onClick = {
            navController.navigate("${ReadEaseScreens.DetailsScreen.name}/${book.id}")
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
                Text(
                    text = book.volumeInfo.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding( bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                   text = "Authors: ${book.volumeInfo.authors?.joinToString(",")?:"Unknown"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                   text = book.volumeInfo.publishedDate?:"Unknown",
                    fontStyle = FontStyle.Italic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "[${book.volumeInfo.categories?.joinToString(", ")?:" Unknown "}]",
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

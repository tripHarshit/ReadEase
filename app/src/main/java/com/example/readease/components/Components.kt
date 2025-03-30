package com.example.readease.components

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import com.example.readease.R
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.readease.model.MBook
import com.example.readease.repository.BooksDataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException


@Composable
fun TitleSection(modifier: Modifier = Modifier, label: String) {
    Surface(
        color = Color.Transparent,
        modifier = modifier.padding(start = 8.dp, top = 5.dp)
    ) {
        Text(
            text = label,
           style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = .95f),
            textAlign = TextAlign.Left
        )
    }
}

@Composable
fun FABContent(onTap: () -> Unit) {
    FloatingActionButton(
        onClick = { onTap() }, // Ensure onTap is called
        shape = RoundedCornerShape(20.dp),
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier
            .padding(end = 8.dp)
            .size(65.dp),
        elevation = FloatingActionButtonDefaults.elevation(5.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search ,
            contentDescription = "Add",
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Composable
fun BookRating(score: Int ) {

    Surface(
        modifier = Modifier
            .padding(4.dp)
            .height(70.dp),
        shape = RoundedCornerShape(56),
        shadowElevation = 6.dp, tonalElevation = 6.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(3.dp)
            )

            Text(
                text = score.toString(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun LoadingScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surfaceDim
    ) {
        Column(
            modifier = Modifier.fillMaxSize(), // Ensure the column takes full screen
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(250.dp) // Keep it a reasonable size
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchForm(
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    hint: String = "Search by name",
    onSearch: (String) -> Unit = {},
) {
    Column(modifier = modifier) {
        val searchQueryState = rememberSaveable { mutableStateOf("") }
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
                Text(text = hint, color = MaterialTheme.colorScheme.onSurface)
            },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
            },
            trailingIcon = {
                if (searchQueryState.value.isNotEmpty()) {
                    IconButton(onClick = { searchQueryState.value = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
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



@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
fun fetchBooks(
    query: String,
    bookList: MutableList<MBook>,  // Use MutableList so it can be modified
    booksDataRepository: BooksDataRepository,
    coroutineScope: CoroutineScope // Pass ViewModel's scope
) {
    coroutineScope.launch(Dispatchers.IO) {
        if (query.isEmpty()) return@launch
        try {
            val response = booksDataRepository.getBooks(query, 15)
            if (response.isSuccessful) {
                response.body()?.let { bookResponse ->
                    bookList.clear()
                    bookList.addAll(bookResponse.items ?: emptyList())
                }
            }
        } catch (e: IOException) {
            println("Network error: ${e.message}")
        } catch (e: HttpException) {
            println("HTTP error: ${e.message}")
        }
    }
}

@Composable
fun ReviewBox(
    reviewText: MutableState<TextFieldValue>,
    reviews: MutableList<String>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Text("Write  your  thoughts...", color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth())

        LazyColumn(modifier = Modifier.height(120.dp)) {
            items(reviews) { review ->
                Text(text = "- $review", color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        BasicTextField(
            value = reviewText.value,
            onValueChange = { reviewText.value = it },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                .padding(8.dp),
            singleLine = true,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                if (reviewText.value.text.isNotEmpty()) {
                    reviews.add(reviewText.value.text)
                    reviewText.value = TextFieldValue("")
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Review")
        }
    }
}

@Composable
fun RatingBar(
    modifier: Modifier = Modifier,
    rating: Int,
    onPressRating: (Int) -> Unit
) {
    var ratingState by remember { mutableIntStateOf(rating) }
    var selected by remember { mutableStateOf(false) }

    val size by animateDpAsState(
        targetValue = if (selected) 42.dp else 34.dp,
        animationSpec = spring(Spring.DampingRatioMediumBouncy)
    )

    Row(
        modifier = modifier.width(280.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 1..5) {
            Box(
                modifier = Modifier
                    .size(size)
                    .pointerInput(Unit) {
                        forEachGesture {
                            awaitPointerEventScope {
                                val down = awaitFirstDown()
                                selected = true
                                onPressRating(i)
                                ratingState = i
                                down.consume()

                                val up = waitForUpOrCancellation()
                                if (up != null) {
                                    selected = false
                                }
                            }
                        }
                    }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_star_rate_24),
                    contentDescription = "Star",
                    modifier = Modifier.fillMaxSize(),
                    tint = if (i <= ratingState) Color(0xfFFFFC107) else Color.Gray
                )
            }
        }
    }
}

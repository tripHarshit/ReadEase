package com.example.readease.model

import androidx.compose.foundation.lazy.grid.GridItemSpan

data class BookResponse(
    val items: List<MBook>?,
    val kind: String,
    val totalItems: Int

    )

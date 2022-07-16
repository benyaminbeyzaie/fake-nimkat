package com.nimkat.app.view.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import coil.compose.AsyncImage
import com.nimkat.app.R

@OptIn(ExperimentalFoundationApi::class)
@Preview
@Composable
fun Gallery() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_back))
    ) {

        val list = ArrayList<Int>()
        for (i in 0..12) {
            list.add(R.drawable.image_test)
            list.add(R.drawable.image_test2)
            list.add(R.drawable.image_test3)
        }
        list.shuffle()

        LazyVerticalGrid(
            cells = GridCells.Fixed(4),
            content = {
                items(list.size) { index ->
                    ListItem(list[index])
                }
            })

    }
}

@Composable
fun ListItem(@DrawableRes image: Int) {
    Row {
        AsyncImage(
            model = image,
            contentDescription = null
        )
    }
}


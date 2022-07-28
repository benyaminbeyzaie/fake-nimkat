package com.nimkat.app.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.nimkat.app.R

@Composable
fun CircularIndeterminanteProgressBar(
    isDisplayed: Boolean,
    height: Int = 0,
    space: Int = 0
) {
    if (isDisplayed) {
        if (height == 0) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(space.dp),
                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = colorResource(R.color.blue)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    color = colorResource(R.color.blue)
                )
            }
        }

    }
}
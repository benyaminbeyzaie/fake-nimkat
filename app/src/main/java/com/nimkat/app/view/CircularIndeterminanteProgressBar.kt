package com.nimkat.app.view

import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.ybq.android.spinkit.SpinKitView
import com.nimkat.app.R

@Composable
fun CircularIndeterminanteProgressBar(
    isDisplayed: Boolean,
    height: Int = 0,
    space: Int = 0,
    color: Color = Color.Black
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
                var colorResource = colorResource(id = R.color.indicator)
                AndroidView(
                    factory = { context ->
                        SpinKitView(
                            ContextThemeWrapper(
                                context,
                                com.github.ybq.android.spinkit.R.style.SpinKitView_ThreeBounce
                            )
                        ).apply {
                            setColor(colorResource.toArgb())
//                            setColor(Color.Black.toArgb())
                        }
                    },
                    update = {


                    },
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

                var colorResource = colorResource(id = R.color.indicator)
                AndroidView(
                    factory = { context ->
                        SpinKitView(
                            ContextThemeWrapper(
                                context,
                                com.github.ybq.android.spinkit.R.style.SpinKitView_ThreeBounce
                            )
                        ).apply {
                            setColor(colorResource.toArgb())
//                            setColor(Color.Black.toArgb())

                        }
                    },
                    update = {


                    },
                )
            }
        }

    }
}
package com.nimkat.app.view

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.nimkat.app.ui.theme.mainFont

@OptIn(ExperimentalAnimationApi::class)
@ExperimentalMaterialApi
@Composable
fun SnackBar(
    snackbarHostState: SnackbarHostState,
    color: Color,
    ishowing: Boolean,
    onHideSnackbar: () -> Unit
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize(),) {

        SnackbarHost(
            modifier = Modifier.fillMaxWidth()
                .padding(10.dp),
            hostState = snackbarHostState,

            snackbar = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Snackbar(
                            backgroundColor = color,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = snackbarHostState.currentSnackbarData?.message ?: "",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontFamily = mainFont,
                                        fontSize = 19.sp,
                                        textAlign = TextAlign.Center,
                                        textDirection = TextDirection.Rtl,
                                    ),
                                )
                            }
                        }

                }
            }
        )
    }
}
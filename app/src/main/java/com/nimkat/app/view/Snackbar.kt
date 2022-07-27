package com.nimkat.app.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
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
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {

        SnackbarHost(
            modifier = Modifier
                .padding(10.dp),
            hostState = snackbarHostState,

            snackbar = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Snackbar(
                        backgroundColor = color,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Column(
                        ) {
                            Text(
                                text = snackbarHostState.currentSnackbarData?.message ?: "",
                                style = TextStyle(
                                    color = Color.White,
                                    fontFamily = mainFont,
                                    fontSize = 19.sp,
                                    textAlign = TextAlign.Center, textDirection = TextDirection.Rtl
                                ),
                            )
                        }
                    }
                }
            }
        )
    }
}
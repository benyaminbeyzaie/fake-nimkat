package com.nimkat.app.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.nimkat.app.R
import com.nimkat.app.ui.theme.NimkatTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    private var cameraScaffoldState: ScaffoldState? = null
    private var coroutineScope: CoroutineScope? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    cameraScaffoldState = rememberScaffoldState()
                    coroutineScope = rememberCoroutineScope()
                    Greeting(cameraScaffoldState!!)
                }
            }
        }
    }

    override fun onBackPressed() {
        cameraScaffoldState?.let {
            if (it.drawerState.isOpen) {
                coroutineScope?.launch {
                    it.drawerState.close()
                }
                return
            }
        }

        super.onBackPressed()
    }


}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun Greeting(cameraScaffoldState: ScaffoldState) {


    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(
        initialPage = 1,
        pageCount = 3,
        infiniteLoop = false
    )

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Column() {
            Card(
                shape = RoundedCornerShape(0.dp, 0.dp, 16.dp, 16.dp),
                modifier = Modifier
                    .weight(1f)
                    .background(color = colorResource(R.color.main_color))
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxSize()
                    ) { index ->
                        when (index) {
                            0 -> {
                                TextQuestion()
                            }
                            1 -> {
                                Camera(cameraScaffoldState)
                            }
                            2 -> {
                                Gallery()
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .height(40.dp)
                    .background(colorResource(R.color.main_color))
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BnvItem(
                    0,
                    R.string.text_question,
                    pagerState,
                    Modifier
                        .weight(1.0F)
                        .fillMaxHeight()
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        })
                BnvItem(
                    1,
                    R.string.camera,
                    pagerState,
                    Modifier
                        .weight(1.0F)
                        .fillMaxHeight()
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        })
                BnvItem(
                    2,
                    R.string.gallery,
                    pagerState,
                    Modifier
                        .weight(1.0F)
                        .fillMaxHeight()
                        .clickable {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(2)
                            }
                        })
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
@Composable
fun BnvItem(index: Int, @StringRes titleRes: Int, pagerState: PagerState, modifier: Modifier) {
    Column(
        modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(titleRes),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = if (index == pagerState.currentPage) colorResource(R.color.white) else colorResource(
                R.color.bnv_unselected_color
            ),
            fontSize = 13.sp
        )
        AnimatedVisibility(
            visible = index == pagerState.currentPage,
        ) {
            Divider(
                color = colorResource(id = R.color.white),
                thickness = 3.dp,
                modifier = Modifier
                    .width(30.dp)
                    .padding(0.dp, 4.dp, 0.dp, 0.dp)
            )
        }
    }
}

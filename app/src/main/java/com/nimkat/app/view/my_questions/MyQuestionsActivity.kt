package com.nimkat.app.view.my_questions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.models.QuestionModel
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.view.CircularIndeterminanteProgressBar
import com.nimkat.app.view.SnackBar
import com.nimkat.app.view.otp.OtpActivity
import com.nimkat.app.view.question_detail.QuestionDetailActivity
import com.nimkat.app.view_model.MyQuestionsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyQuestionsActivity : AppCompatActivity() {

    companion object {
        fun sendIntent(context: Context) = Intent(context, MyQuestionsActivity::class.java).apply {
            context.startActivity(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel: MyQuestionsViewModel by viewModels()
        viewModel.loadQuestions()
        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        MyQuestionsContent(viewModel)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@Composable
fun MyQuestionsContent(viewModel: MyQuestionsViewModel) {

    val context = LocalContext.current
    val questions = viewModel.myQuestions.observeAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val errorSnackBar = remember { SnackbarHostState() }
    val isLoading = remember { mutableStateOf(false) }

    when (questions.value?.status) {
        DataStatus.Error -> {
            isLoading.value = false
            LaunchedEffect(lifecycleOwner.lifecycleScope) {
                errorSnackBar.showSnackbar(
                    message = context.getString(R.string.errorMessage),
                    actionLabel = "RED",
                    duration = SnackbarDuration.Short
                )
            }
        }
        DataStatus.Loading -> {
            isLoading.value = true
        }
        else -> {
            isLoading.value = false
        }
    }



    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .background(colorResource(R.color.main_color)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
                    IconButton(
                        onClick = {
                            if (context is Activity) context.onBackPressed()
                        },
                        modifier = Modifier
                            .padding(4.dp, 12.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back), null,
                            tint = colorResource(R.color.primary_text),
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(180f)
                        )
                    }
                }
                Text(
                    stringResource(R.string.my_questions),
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = colorResource(R.color.primary_text),
                    fontFamily = mainFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        },
        backgroundColor = colorResource(R.color.background)
    ) {
        if (isLoading.value) {
            CircularIndeterminanteProgressBar(true, space = 20)
        } else {

            val list = ArrayList<QuestionModel>()
            Log.d("My Question Activity", "new list created")
            questions.value?.data?.forEach { questionModel -> list.add(questionModel) }

            val listState = rememberLazyListState()
            LazyVerticalGrid(
                cells = GridCells.Fixed(2),
                modifier = Modifier.padding(12.dp),
                state = listState,
                content = {
                    items(list.size) { index ->
                        Card(
                            modifier = Modifier
                                .padding(4.dp),
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = colorResource(id = R.color.secondary_text_variant)
                        ) {
                            Box(modifier = Modifier.clickable {
                                if (list[index].files.isEmpty()) {
                                    QuestionDetailActivity.sendIntent(
                                        context,
                                        list[index].id!!,
                                        list[index].text,
                                        null
                                    )
                                } else {
                                    QuestionDetailActivity.sendIntent(
                                        context,
                                        list[index].id!!,
                                        list[index].text,
                                        list[index].files.first().file?.attachment
                                    )
                                }
                            }) {
                                if (list[index].files.isNotEmpty() && list[index].files.first().file?.attachment != null) {
                                    SubcomposeAsyncImage(
                                        model = list[index].files.first().file?.attachment,
                                        contentDescription = null,
                                        loading = {
                                            CircularIndeterminanteProgressBar(true)
                                        }
                                    )
                                }
                                if (list[index].text != null) {
                                    Text(
                                        text = (list[index].text.toString()),
                                        modifier = Modifier.padding(12.dp),
                                        fontSize = 14.sp,
                                        color = colorResource(id = R.color.primary_text)
                                    )
                                }
                            }
                        }
                    }
                })

            listState.OnBottomReached {
                viewModel.loadQuestions()
                Log.d("MyQuestionActivity", "do on load more")
            }
        }

    }
    SnackBar(snackbarHostState = errorSnackBar, Color.Red, true) {}
}

@Composable
fun LazyListState.OnBottomReached(loadMore: () -> Unit) {
    // state object which tells us if we should load more
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf true
            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore.value }
            .collect {
                if (it) loadMore()
            }
    }
}

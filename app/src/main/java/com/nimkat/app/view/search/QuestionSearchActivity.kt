package com.nimkat.app.view.search

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.github.ybq.android.spinkit.SpinKitView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.nimkat.app.R
import com.nimkat.app.models.DataHolder
import com.nimkat.app.models.DataStatus
import com.nimkat.app.models.DiscoveryAnswers
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.utils.LIST
import com.nimkat.app.utils.QUESTION
import com.nimkat.app.utils.QUESTION_ID
import com.nimkat.app.view.full_image.FullImageActivity
import com.nimkat.app.view.question_detail.QuestionDetailActivity
import com.nimkat.app.view_model.AskQuestionViewModel
import com.nimkat.app.view_model.AuthViewModel
import com.rd.PageIndicatorView
import com.rd.animation.type.AnimationType
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QuestionSearchActivity : ComponentActivity() {

    companion object {
        fun sendIntent(
            context: Context,
            questions: ArrayList<DiscoveryAnswers>,
            questionId: String?,
        ) =
            Intent(context, QuestionSearchActivity::class.java).apply {
                putExtra(QUESTION_ID, questionId)
                putParcelableArrayListExtra(LIST, questions)
                context.startActivity(this)
            }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val questionId = intent.getStringExtra(QUESTION_ID).orEmpty()

        val questions: ArrayList<DiscoveryAnswers>? = intent.getParcelableArrayListExtra(LIST)
        val askQuestionViewModel: AskQuestionViewModel by viewModels()



        setContent {
            NimkatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        QuestionSearchContent(questionId, questions, askQuestionViewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun QuestionSearchContent(
    questionId: String,
    questions: List<DiscoveryAnswers>?,
    askQuestionViewModel: AskQuestionViewModel,
) {
    val askedTeachers = askQuestionViewModel.askedTeacher.observeAsState()
    val questionModel = askQuestionViewModel.questionModel.observeAsState()
    when (askedTeachers.value?.status) {
        DataStatus.Success -> {
            QuestionDetailActivity.sendIntent(LocalContext.current, questionModel.value!!.data!!.id!!, questionModel.value!!.data!!.text, null)
        }
        else -> {}
    }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .background(colorResource(R.color.main_color))
                    .padding(2.dp, 0.dp),
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
                            tint = colorResource(R.color.white),
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(180f)
                        )
                    }
                }
                Text(
                    stringResource(R.string.search_result),
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = colorResource(R.color.white),
                    fontFamily = mainFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        },
        backgroundColor = colorResource(R.color.background)
    ) {


        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            if (questions.isNullOrEmpty()) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .weight(1.0F)
                ) {


                    Text(
                        stringResource(R.string.question_search_desc_0),
                        fontFamily = mainFont,
                        fontSize = 14.sp,
                        color = colorResource(R.color.primary_text_variant),
                    )

                    val texts = listOf(
                        R.string.question_search_desc_1,
                        R.string.question_search_desc_2,
                        R.string.question_search_desc_3,
                        R.string.question_search_desc_4,
                    )

                    for (i in 0 until 4) {
                        Row(
                            modifier = Modifier.padding(0.dp, 12.dp, 0.dp, 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.size(10.dp),
                                backgroundColor = colorResource(id = R.color.main_color)
                            ) {}
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                stringResource(texts[i]),
                                fontFamily = mainFont,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = colorResource(R.color.primary_text_variant),
                            )
                        }
                    }

                }
            } else {

                val pagerState = rememberPagerState(
                    initialPage = 0,
                    pageCount = questions.size,
                    infiniteLoop = false
                )

                if (questions.size > 1) {
                    AndroidView(
                        factory = { context ->
                            PageIndicatorView(context).apply {
                                selectedColor = ContextCompat.getColor(context, R.color.main_color)
                                unselectedColor = ContextCompat.getColor(context, R.color.gray500)
                                setAnimationType(AnimationType.DROP)
                                count = questions.size
                                setSelected(questions.size - 1)
                            }
                        },
                        update = {
                            it.selection = it.count - pagerState.currentPage - 1

                        },
                        modifier = Modifier.padding(12.dp)
                    )
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0F)
                ) { index ->
                    questions[index].also {
                        QuestionShow(it.question, it.answer)
                    }
                }
            }


            val loading = remember {
                mutableStateOf(false)
            }


            Text(
                text = stringResource(R.string.not_found_answer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 0.dp),
                color = colorResource(R.color.black),
                fontSize = 14.sp,
            )
            CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
                if (loading.value) {
                    AndroidView(
                        factory = { context ->
                            SpinKitView(
                                ContextThemeWrapper(
                                    context,
                                    com.github.ybq.android.spinkit.R.style.SpinKitView_ThreeBounce
                                )
                            ).apply {

                            }
                        },
                        update = {


                        },
                    )

                } else {
                    Button(
                        onClick = {
                            askQuestionViewModel.askTeachers(questionId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(60.dp),
                        enabled = !loading.value,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.blue)),
                    ) {
                        Row {
                            Text(
                                text = stringResource(R.string.ask_from_teachers),
                                style = TextStyle(
                                    fontFamily = secondFont
                                ),
                                color = colorResource(R.color.white),
                                fontSize = 20.sp,
                            )
                        }
                    }
                }
            }
        }

    }

}

@Composable
fun QuestionShow(question: String?, answer: String?) {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 24.dp, 16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.question),
                modifier = Modifier.wrapContentWidth(),
                fontSize = 12.sp,
                color = colorResource(id = R.color.gray500)
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Transparent)
            )
            Divider(color = colorResource(id = R.color.gray300), thickness = 1.dp)
        }

        Card(
            modifier = Modifier
                .padding(16.dp, 24.dp, 16.dp, 0.dp)
                .fillMaxWidth()
                .height(200.dp),
            backgroundColor = colorResource(id = R.color.white)
        ) {
            AsyncImage(
                model = question,
                contentDescription = null,
                modifier = Modifier.clickable {
                    FullImageActivity.sendIntent(context, question.orEmpty())
                }
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 36.dp, 16.dp, 0.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.answer),
                modifier = Modifier.wrapContentWidth(),
                fontSize = 12.sp,
                color = colorResource(id = R.color.gray500)
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(Color.Transparent)
            )
            Divider(color = colorResource(id = R.color.gray300), thickness = 1.dp)
        }

        Card(
            modifier = Modifier
                .padding(16.dp, 24.dp, 16.dp, 24.dp)
                .fillMaxWidth()
                .height(200.dp),
            backgroundColor = colorResource(id = R.color.white)
        ) {
            AsyncImage(
                model = answer,
                contentDescription = null,
                modifier = Modifier.clickable {
                    FullImageActivity.sendIntent(context, answer.orEmpty())
                }
            )
        }


    }


}

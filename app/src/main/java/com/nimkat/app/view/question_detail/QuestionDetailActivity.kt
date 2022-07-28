package com.nimkat.app.view.question_detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.nimkat.app.R
import com.nimkat.app.models.AnswerModel
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.view.CircularIndeterminanteProgressBar
import com.nimkat.app.view_model.MyQuestionsViewModel
import com.nimkat.app.view_model.SingleQuestionViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class QuestionDetailActivity : AppCompatActivity() {

    companion object {
        fun sendIntent(context: Context, id: Int, text: String?, url: String?) =
            Intent(context, QuestionDetailActivity::class.java).apply {
                putExtra("id", id)
                putExtra("text", text)
                putExtra("url", url)
                context.startActivity(this)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = intent.getIntExtra("id", -1)
        val text = intent.getStringExtra("text")
        val url = intent.getStringExtra("url")

        val viewModel: SingleQuestionViewModel by viewModels()
        viewModel.loadAnswers(id.toInt())

        setContent {
            NimkatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        QuestionDetailContent(text, url, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionDetailContent(text: String?, url: String?, viewModel: SingleQuestionViewModel) {
    val context = LocalContext.current
    val answers = viewModel.answers.observeAsState()
    val list = ArrayList<AnswerModel>()
    answers.value?.data?.forEach { answerModel -> list.add(answerModel) }

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
                            tint = colorResource(R.color.primary_text),
                            modifier = Modifier
                                .size(24.dp)
                                .rotate(180f)
                        )
                    }
                }
                Text(
                    stringResource(R.string.question_and_answer),
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

        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 0.dp),
                shape = RoundedCornerShape(8.dp),
                backgroundColor = colorResource(id = R.color.secondary_text_variant)
            ) {
                if (url != null) {
                    val scale = remember { mutableStateOf(1f) }
                    var offsetX by remember { mutableStateOf(0f) }
                    var offsetY by remember { mutableStateOf(0f) }
                    Box(
                        modifier = Modifier
                            .clip(RectangleShape) // Clip the box content
                            .fillMaxWidth()
                            .heightIn(min = 300.dp, max = 400.dp)// Give the size you want...
                            .background(Color.Gray)
                            .pointerInput(Unit) {
                                detectTransformGestures { _, pan, zoom, _ ->
                                    scale.value *= zoom
                                    offsetX -= pan.x
                                    offsetY += pan.y
                                }
                            }
                    ) {
                        SubcomposeAsyncImage(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .fillMaxWidth()
                                .graphicsLayer(
                                    scaleX = maxOf(.5f, minOf(3f, scale.value)),
                                    scaleY = maxOf(.5f, minOf(3f, scale.value)),
                                )
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) },
                            model = url,
                            contentDescription = null,
                            loading = {
                                CircularIndeterminanteProgressBar(true)
                            }
                        )
                    }

                }
                if (text != null) {
                    Text(
                        text = (text.toString()),
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp,
                        color = colorResource(id = R.color.primary_text)
                    )
                }
            }
            Divider(modifier = Modifier.padding(top = 5.dp))
            if (list.isEmpty()) {
                Text(
                    text = "هنوز جوابی برای این سوال ثبت نشده",
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    fontSize = 14.sp,
                    fontFamily = mainFont,
                    textAlign = TextAlign.Center,
                    color = colorResource(id = R.color.primary_text)
                )
            } else {
                Column {
                    list.forEach { answer ->
                        Card(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth()
                                .padding(16.dp, 16.dp, 16.dp, 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = colorResource(id = R.color.secondary_text_variant)
                        ) {
                            Column {

                                if (answer.text != null) {
                                    Text(
                                        text = (answer.text.toString()),
                                        modifier = Modifier.padding(12.dp),
                                        fontSize = 14.sp
                                    )
                                }
                                if (answer.files.isNotEmpty() && answer.files.first().file?.attachment != null) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .padding(12.dp)
                                            .fillMaxWidth()
                                            .align(Alignment.CenterHorizontally),
                                        model = answer.files.first().file?.attachment,
                                        contentDescription = null
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


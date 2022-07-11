package com.nimkat.app.profile_edit.grade

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimkat.app.R
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont

class GradeActivity : ComponentActivity() {


    companion object {
        fun sendIntent(context: Context) = Intent(context, GradeActivity::class.java).apply {
            context.startActivity(this)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        GradeContent()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun GradeContent() {


    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.color_back))
    ) {
        IconButton(
            onClick = {
                if (context is Activity) context.onBackPressed()
            },
            modifier = Modifier
                .padding(4.dp, 12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back), null,
                tint = colorResource(R.color.black),
                modifier = Modifier
                    .size(24.dp)
                    .rotate(180f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            stringResource(R.string.choose_your_grade),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 20.dp, 20.dp, 0.dp),
            color = colorResource(R.color.black),
            fontFamily = secondFont,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val list = listOf(
            GradeModel("ابتدائی", listOf("اول", "دوم", "سوم", "چهارم", "پنجم", "ششم")),
            GradeModel("متوسطه اول", listOf("هفتم", "هشتم", "نهم")),
            GradeModel("متوسطه دوم", listOf("دهم", "یازدهم", "دوازدهم")),
        )

        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(16.dp, 10.dp)
        ) {
            list.forEach { model ->
                item {
                    ExpandableContainer(
                        title = model.title,
                    ) {
                        model.items.forEach {
                            Spacer(modifier = Modifier.height(2.dp))
                            Card(
                                modifier = Modifier,
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, colorResource(R.color.gray300)),
                                elevation = 0.dp,
                                onClick = {

                                }
                            ) {
                                Text(
                                    it,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp, 10.dp),
                                    style = TextStyle(
                                        fontFamily = mainFont,
                                        color = colorResource(R.color.black)
                                    ),
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                        }
                    }
                }
            }

        }

    }

}

enum class ExpandableState { VISIBLE, HIDDEN }

@Composable
fun ExpandableContainer(
    title: String,
    defaultState: ExpandableState = ExpandableState.HIDDEN,
    content: @Composable ColumnScope.() -> Unit,
) {

    //State
    var isContentVisible = rememberSaveable { mutableStateOf(defaultState) }

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {

        Box(modifier = Modifier
            .clickable {
                isContentVisible.value =
                    if (isContentVisible.value == ExpandableState.VISIBLE) ExpandableState.HIDDEN else ExpandableState.VISIBLE
            }) {
            Row(
                Modifier
                    .padding(8.dp, 10.dp)
            ) {
                Text(
                    text = title, Modifier.weight(1f),
                    style = TextStyle(
                        fontFamily = mainFont,
                        color = colorResource(R.color.gray800),
                        fontWeight = FontWeight.Bold
                    ),
                    fontSize = 16.sp
                )
                Icon(
                    painterResource(R.drawable.ic_arrow_bottom),
                    null,
                    modifier = Modifier.rotate(if (isContentVisible.value == ExpandableState.VISIBLE) 180f else 0f)
                )
            }
        }

        AnimatedVisibility(visible = isContentVisible.value == ExpandableState.VISIBLE) {
            Column {
                content()
            }
        }
    }

}


data class GradeModel(
    val title: String,
    val items: List<String>
)
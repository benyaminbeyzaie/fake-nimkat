package com.nimkat.app.view.main

import android.util.Log
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.github.ybq.android.spinkit.SpinKitView
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.utils.toast
import com.nimkat.app.view.login.LoginActivity
import com.nimkat.app.view.profile_edit.grade.ExpandableState
import com.nimkat.app.view.search.QuestionSearchActivity
import com.nimkat.app.view_model.TextQuestionViewModel

@Preview
@Composable
fun TextQuestion(viewModel: TextQuestionViewModel, lifecycleOwner: LifecycleOwner) {

    val context = LocalContext.current

    val text = remember { mutableStateOf("") }
    val loading = remember {
        mutableStateOf(false)
    }

    viewModel.myQuestions.removeObservers(lifecycleOwner)
    viewModel.reCreate()
    viewModel.myQuestions.observe(lifecycleOwner) {
        loading.value = it.status == DataStatus.Loading

        when (it.status) {
            DataStatus.NeedLogin -> {
                LoginActivity.sendIntent(context)
            }
            DataStatus.Success -> {
                it.data?.let { list ->
                    QuestionSearchActivity.sendIntent(context, text.value, list)
                }
            }
            DataStatus.Error -> {
                context.toast("Error : ".plus(it.message.toString()))
            }
        }
    }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
    ) {

        val text = remember { mutableStateOf("") }

        TextField(
            value = text.value,
            maxLines = 10,
            onValueChange = {
                text.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 170.dp)
                .padding(16.dp, 16.dp, 16.dp, 0.dp),
            placeholder = {
                Text(
                    stringResource(id = R.string.enter_your_question),
                    fontFamily = mainFont,
                    color = colorResource(
                        R.color.color_hint
                    ),
                    fontSize = 14.sp
                )
            },
            shape = RoundedCornerShape(24.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = colorResource(R.color.textfield_background),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = colorResource(id = R.color.blue)
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                fontFamily = mainFont,
                color = colorResource(R.color.primary_text)
            )
        )

        CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
            Button(
                onClick = {
                    if (text.value.isEmpty()) return@Button
                    viewModel.loadQuestions(text.value)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(60.dp),
                enabled = !loading.value,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.green)),
            ) {

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
                    Row {
                        Text(
                            text = stringResource(R.string.ok),
                            style = TextStyle(
                                fontFamily = secondFont
                            ),
                            color = colorResource(R.color.white),
                            fontSize = 20.sp,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Outlined.CheckCircle,
                            null,
                            tint = colorResource(R.color.white),
                            modifier = Modifier.padding(0.dp, 6.dp, 0.dp, 0.dp)
                        )
                    }
                }
            }
        }

    }
}

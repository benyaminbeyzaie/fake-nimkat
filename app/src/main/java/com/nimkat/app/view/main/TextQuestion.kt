package com.nimkat.app.view.main

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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.view.CircularIndeterminanteProgressBar
import com.nimkat.app.view_model.AskQuestionViewModel

@Composable
fun TextQuestion(viewModel: AskQuestionViewModel) {
    val text = remember { mutableStateOf("") }
    val loading = remember {
        mutableStateOf(false)
    }

    val discoveryAnswers = viewModel.discoveryAnswers.observeAsState()

    when (discoveryAnswers.value?.status) {
        DataStatus.Loading -> {
            loading.value = true
        }
        else -> {
            loading.value = false
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
                    viewModel.sendQuestion(text.value)
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
                    CircularIndeterminanteProgressBar(true)
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

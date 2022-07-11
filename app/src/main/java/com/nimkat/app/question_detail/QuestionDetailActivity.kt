package com.nimkat.app.question_detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimkat.app.R
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont

class QuestionDetailActivity : ComponentActivity() {

    companion object {
        fun sendIntent(context: Context) =
            Intent(context, QuestionDetailActivity::class.java).apply {
                context.startActivity(this)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NimkatTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        QuestionDetailContent()
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionDetailContent() {
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
                    stringResource(R.string.question_and_answer),
                    modifier = Modifier
                        .fillMaxWidth(),
                    color = colorResource(R.color.white),
                    fontFamily = mainFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                )
            }
        },
        backgroundColor = colorResource(R.color.color_back)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp, 16.dp, 16.dp, 0.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "سوال 1",
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp,
                    fontFamily = mainFont
                )
            }

            Text(
                text = "هنوز جوابی برای این سوال ثبت نشده",
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                fontSize = 14.sp,
                fontFamily = mainFont,
                textAlign = TextAlign.Center
            )
        }


    }
}


package com.nimkat.app.view.profile_edit

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nimkat.app.R
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.view.profile_edit.ui.theme.NimkatTheme

class WhatsYourNameActivity : ComponentActivity() {
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
                        Greeting2()
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting2() {

    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
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
                tint = colorResource(R.color.primary_text),
                modifier = Modifier
                    .size(24.dp)
                    .rotate(180f)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            stringResource(R.string.whats_your_name),
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 20.dp, 20.dp, 0.dp),
            color = colorResource(R.color.primary_text),
            fontFamily = secondFont,
            fontSize = 32.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        val name = remember { mutableStateOf("") }

        TextField(
            value = name.value,
            maxLines = 1,
            onValueChange = {
                name.value = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 0.dp),
            placeholder = {
                Text(
                    stringResource(id = R.string.name),
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
                disabledIndicatorColor = Color.Transparent
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
                    if (name.value != ""){
                        val data = Intent().apply {
                            putExtra("name", name.value)
                        }
                        if (context is Activity) {
                            context.setResult(Activity.RESULT_OK, data)
                            context.finish()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.blue)),
            ) {
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

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    NimkatTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                Greeting2()
            }
        }
    }
}
package com.nimkat.app.view.question_crop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.nimkat.app.R
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.secondFont
import com.theartofdev.edmodo.cropper.CropImageView

class QuestionCropActivity : ComponentActivity() {

    companion object {
        fun sendIntent(context: Context) = Intent(context, QuestionCropActivity::class.java).apply {
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
                        QuestionCropContent()
                    }
                }
            }
        }
    }


}

@Composable
fun QuestionCropContent() {
    Column(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { factoryContext ->
                CropImageView(factoryContext)
            },
            modifier = Modifier.weight(1f),
            update = { cropImageView ->
                cropImageView.setAspectRatio(2, 1)
                cropImageView.setFixedAspectRatio(false)
                cropImageView.guidelines = CropImageView.Guidelines.OFF
//            cropImageView.setImageUriAsync()


                cropImageView.imageResource = R.drawable.image_test2
            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {


            IconButton(
                onClick = {

                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_reset),
                    null,
                    tint = colorResource(R.color.main_color),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(
                onClick = {

                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_rotate),
                    null,
                    tint = colorResource(R.color.main_color),
                )
            }
        }

        CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.green)),
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



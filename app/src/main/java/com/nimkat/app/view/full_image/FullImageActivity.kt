package com.nimkat.app.view.full_image

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.github.chrisbanes.photoview.PhotoView
import com.github.ybq.android.spinkit.SpinKitView
import com.nimkat.app.R
import com.nimkat.app.models.DiscoveryAnswers
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.utils.IMAGE_URL
import com.nimkat.app.utils.LIST
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.view.search.QuestionSearchActivity
import com.nimkat.app.view.search.QuestionSearchContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FullImageActivity : AppCompatActivity() {


    companion object {
        fun sendIntent(context: Context, imageUrl: String) =
            Intent(context, FullImageActivity::class.java).apply {
                putExtra(IMAGE_URL, imageUrl)
                context.startActivity(this)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val imageUrl = intent.getStringExtra(IMAGE_URL).orEmpty()

        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        FullImageContent(imageUrl)
                    }
                }
            }
        }
    }
}

@Composable
fun FullImageContent(imageUrl: String) {

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
                    stringResource(R.string.image),
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
        AndroidView(
            factory = { context ->
                PhotoView(context)
            },
            update = {

                CoroutineScope(Dispatchers.IO).launch {
                    val loader = ImageLoader(context)
                    val request = ImageRequest.Builder(context)
                        .data(imageUrl)
                        .allowHardware(false) // Disable hardware bitmaps.
                        .build()

                    loader.execute(request).also { result ->
                        if (result is SuccessResult) {
                            val successResult = (result as SuccessResult).drawable
                            val bitmap = (successResult as BitmapDrawable).bitmap

                            CoroutineScope(Dispatchers.Main).launch {
                                try {
                                    it.setImageBitmap(bitmap)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }


                }



            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

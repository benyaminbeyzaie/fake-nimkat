package com.nimkat.app.view.question_crop

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.ImageLoader
import coil.request.ImageRequest
import com.nimkat.app.R
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.utils.IMAGE_PATH
import com.theartofdev.edmodo.cropper.CropImageView


class QuestionCropActivity : ComponentActivity() {

    companion object {
        fun sendIntent(activity: Activity, imagePath: String) =
            Intent(activity, QuestionCropActivity::class.java).apply {
                putExtra(IMAGE_PATH, imagePath)
            }
    }

    private var imagePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkArgument()

        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        QuestionCropContent(imagePath)
                    }
                }
            }
        }
    }

    private fun checkArgument() {
        imagePath = intent.getStringExtra(IMAGE_PATH).orEmpty()
    }

}

@Composable
fun QuestionCropContent(imagePath: String) {

    val context = LocalContext.current
    var croppedImage: Bitmap? = null
    var cropImageView: CropImageView? = null

    Column(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { factoryContext ->
                cropImageView = CropImageView(factoryContext)
                cropImageView!!
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            update = { cropImageView ->
                cropImageView.setAspectRatio(2, 1)
                cropImageView.setFixedAspectRatio(false)
                cropImageView.guidelines = CropImageView.Guidelines.OFF
//                cropImageView.imageResource = R.drawable.image_test2


                val loader = ImageLoader(context)
                val req = ImageRequest.Builder(context)
                    .data(imagePath) // demo link
                    .lifecycle((context as ComponentActivity).lifecycle)
                    .target { result ->
                        val bitmap = (result as BitmapDrawable).bitmap
                        cropImageView.setImageBitmap(bitmap)
                    }
                    .build()
                loader.enqueue(req)

                cropImageView.setOnCropImageCompleteListener { view, result ->
                    croppedImage = view?.croppedImage
                }


            }
        )

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {


            IconButton(
                onClick = {
                    cropImageView?.resetCropRect()
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
                    cropImageView?.rotatedDegrees = cropImageView!!.rotatedDegrees + 90
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
                onClick = {
                    val bundle = Bundle().apply {
                        putParcelable("image", croppedImage)
                    }
                    if (context is Activity) {
                        context.setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra("data", bundle)
                        })
                        context.finish()
                    }
                },
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



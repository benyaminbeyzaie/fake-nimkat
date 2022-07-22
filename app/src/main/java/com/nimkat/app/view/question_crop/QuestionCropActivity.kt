package com.nimkat.app.view.question_crop

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.nimkat.app.R
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.secondFont
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView


class QuestionCropActivity : AppCompatActivity() {

    private var imagePath = ""
    private lateinit var photoUri: Uri
    private var mode: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkArgument()

        if (mode == 0) {
            CropImage.activity(photoUri)
                .start(this)
        }else if (mode == 1){
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

    }

    private fun checkArgument() {
        val intent = intent
        mode = intent.getIntExtra("mode" , 1)
        if (mode == 0) {
            photoUri = intent.getParcelableExtra<Uri>("URI")!!
        }
        Log.d("kiloURI", imagePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                Log.d("kiloURI", "copped image uri is: $resultUri")
                a(resultUri);
            }
        }
    }


    fun a(photouri: Uri) {

        setContent {
            NimkatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Log.d("imageview", imagePath)
                        QuestionCropContent(photouri)

                    }

                }
            }
        }
    }

}


@Composable
fun QuestionCropContent(photouri: Uri) {

    val context = LocalContext.current


    /***
     *this part will get a photo Uri and return a bitmap
     */
    lateinit var bitmap: Bitmap
    val shouldShowCamera: MutableState<Boolean> = remember { mutableStateOf(false) }
    Glide.with(context)
        .asBitmap()
        .load(photouri)
        .into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                bitmap = resource
                shouldShowCamera.value = true
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                // this is called when imageView is cleared on lifecycle call or for
                // some other reason.
                // if you are referencing the bitmap somewhere else too other than this imageView
                // clear it here as you can no longer have the bitmap
            }
        })


    Column(Modifier.fillMaxSize()) {

//      we can load images with uri and Coil library

//        if (shouldShowCamera.value) {
//            Image(
//                painter = rememberAsyncImagePainter(photouri),
//                contentDescription = null,
//                modifier = Modifier
//                    .weight(1f)
//                    .fillMaxWidth(),
//            )
//        }


//      or we can load images as a bitmap

        if (shouldShowCamera.value) {
            Image(
                bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp, 0.dp)
        ) {

        }

        CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(5.dp)
            ) {
                Button(
                    onClick = {
                        val data = Intent().apply {
                            putExtra("photouri", photouri)
                        }

                        if (context is Activity) {
                            context.setResult(Activity.RESULT_OK, data)
                            context.finish()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
//                        .fillMaxWidth()
                        .padding(8.dp)
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

                Button(
                    onClick = {

                        if (context is Activity) {
                            context.setResult(Activity.RESULT_CANCELED, Intent().apply {
                            })
                            context.finish()
                        }
                    },
                    modifier = Modifier.
                    weight(1f)
//                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.red)),
                ) {
                    Row {
                        Text(
                            text = stringResource(R.string.cancel),
                            style = TextStyle(
                                fontFamily = secondFont
                            ),
                            color = colorResource(R.color.white),
                            fontSize = 20.sp,
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            Icons.Outlined.HighlightOff,
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



package com.nimkat.app.view.question_crop

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.HighlightOff
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.ybq.android.spinkit.SpinKitView
import com.nimkat.app.R
import com.nimkat.app.models.DataStatus
import com.nimkat.app.ui.theme.NimkatTheme
import com.nimkat.app.ui.theme.RippleWhite
import com.nimkat.app.ui.theme.secondFont
import com.nimkat.app.utils.toast
import com.nimkat.app.view.SnackBar
import com.nimkat.app.view.login.LoginActivity
import com.nimkat.app.view.search.QuestionSearchActivity
import com.nimkat.app.view_model.AskQuestionViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream
import java.io.InputStream


@AndroidEntryPoint
class QuestionCropActivity : AppCompatActivity() {

    private var imagePath = ""
    private lateinit var photoUri: Uri
    private var mode: Int = 1
    private val askQuestionsViewModel: AskQuestionViewModel by viewModels()
    lateinit var resultUri : Uri
    var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var flag = false
        askQuestionsViewModel.discoveryAnswers.observe(this) { value ->
            when (value?.status) {
                DataStatus.NeedLogin -> {
                    LoginActivity.sendIntent(this)
                    isLoading = false
                }
                DataStatus.Success -> {
                    value.data?.let { list ->
                        if (flag) {
                            QuestionSearchActivity.sendIntent(
                                this,
                                list,
                                askQuestionsViewModel.questionId.value!!.data.toString()
                            )
                            finish()
                        }
                    }
                    isLoading = false
                }
                DataStatus.Error -> {
                    isLoading = false
                    contentSetter(resultUri, askQuestionsViewModel , true , isLoading)
                }
                DataStatus.Loading ->{
                    isLoading = true
                    contentSetter(resultUri, askQuestionsViewModel , true , isLoading)
                }
                else -> {isLoading = false
//                    contentSetter(resultUri, askQuestionsViewModel , true , isLoading)
                }
            }
        }
        flag = true

        checkArgument()

        if (mode == 0) {
            CropImage.activity(photoUri)
                .start(this)
        } else if (mode == 1) {
            CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this)
        }

    }

    private fun checkArgument() {
        val intent = intent
        mode = intent.getIntExtra("mode", 1)
        if (mode == 0) {
            photoUri = intent.getParcelableExtra<Uri>("URI")!!
        }
        Log.d("ImageCapture", imagePath)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                resultUri = result.uri
                Log.d("ImageCapture", "copped image uri is: $resultUri")
                contentSetter(resultUri, askQuestionsViewModel , isLoading= isLoading);
            } else {
                setResult(Activity.RESULT_CANCELED, Intent().apply {
                })
                finish()

            }
        }
    }


    fun contentSetter(photouri: Uri, askQuestionsViewModel: AskQuestionViewModel , error:Boolean = false , isLoading: Boolean) {
        setContent {
            NimkatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                        Log.d("imageview", imagePath)
                        QuestionCropContent(photouri, askQuestionsViewModel , error , isLoading)

                    }

                }
            }
        }
    }

}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun QuestionCropContent(photouri: Uri, askQuestionsViewModel: AskQuestionViewModel, error: Boolean, isLoading: Boolean) {
    val shouldShowCamera: MutableState<Boolean> = remember { mutableStateOf(false) }
    shouldShowCamera.value = false

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val errorSnackBar = remember { SnackbarHostState() }

    if (error){
        LaunchedEffect(lifecycleOwner.lifecycleScope) {
            errorSnackBar.showSnackbar(
                message = context.getString(R.string.errorMessage),
                actionLabel = "RED",
                duration = SnackbarDuration.Short
            )
        }
    }

    lateinit var bitmap: Bitmap
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

    Column(
        Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
    ) {
        if (shouldShowCamera.value) {
            Image(
                bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }

        CompositionLocalProvider(LocalRippleTheme provides RippleWhite) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Button(
                    onClick = {
                        val imageStream: InputStream? =
                            context.contentResolver.openInputStream(photouri)
                        val selectedImage = BitmapFactory.decodeStream(imageStream)
                        val baos = ByteArrayOutputStream()
                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val b: ByteArray = baos.toByteArray()
                        val encodedImage: String = Base64.encodeToString(b, Base64.DEFAULT)
                        askQuestionsViewModel.askImageQuestion(encodedImage)
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(R.color.green)),
                ) {
                    if (isLoading) {
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

                Button(
                    onClick = {

                        if (context is Activity) {
                            context.setResult(Activity.RESULT_CANCELED, Intent().apply {
                            })
                            context.finish()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
//                        .fillMaxWidth()
                        .padding(8.dp)
                        .height(60.dp),
                    enabled = !isLoading,
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
    SnackBar(snackbarHostState = errorSnackBar, Color.Red, true) {}

}



package com.nimkat.app.view

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.sp
import com.nimkat.app.R
import com.nimkat.app.ui.theme.mainFont
import com.nimkat.app.view_model.AuthViewModel

@Composable
fun ConfirmDialogue(
    authViewModel: AuthViewModel,
    showDeleteDialouge: MutableState<Boolean>,
) {
//    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        AlertDialog(
            onDismissRequest = { },
            confirmButton = {
                TextButton(onClick = {
                    authViewModel.delete()
                })
                { Text(
                    text = "بله",
                    color = colorResource(id = R.color.primary_text),
                    fontFamily = mainFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialouge.value = false })
                { Text(text = "خیر",
                    color = colorResource(id = R.color.primary_text),
                    fontFamily = mainFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp) }
            },
            title = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = "حذف حساب کاربری",
                        textAlign = TextAlign.Right,
                        fontFamily = mainFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        style = TextStyle(
                            textDirection = TextDirection.Rtl,
                        )
                    )


                }
            },


            text = {
                Text(
                    text = stringResource(id = R.string.areYouSure),
                    textAlign = TextAlign.Right,
                    fontFamily = mainFont,
                    fontSize = 14.sp
                )
            }
        )
//    }


}
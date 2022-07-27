package com.nimkat.app.utils.SMS

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

class SmsReciever : BroadcastReceiver() {

    var smsBroadcastReciverListener: SmsBroadcastReciverListener? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val smsRetriverStatus = extras?.get(SmsRetriever.EXTRA_STATUS) as Status

            when (smsRetriverStatus.statusCode) {
                CommonStatusCodes.SUCCESS -> {

                    val messageIntent =
                        extras.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                    smsBroadcastReciverListener?.onSuccess(messageIntent)
                }
                CommonStatusCodes.TIMEOUT -> {
                    smsBroadcastReciverListener?.onFailure()
                }
            }
        }

    }

    interface SmsBroadcastReciverListener {
        fun onSuccess(intent: Intent?)
        fun onFailure()
    }
}
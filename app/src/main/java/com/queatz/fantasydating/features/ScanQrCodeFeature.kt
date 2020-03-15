package com.queatz.fantasydating.features

import android.content.Intent
import com.google.zxing.integration.android.IntentIntegrator
import com.queatz.on.On


class ScanQrCodeFeature constructor(private val on: On) {

    var successCallback: (() -> Unit)? = null

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

        result?.contents?.let {
            handleResult(result.contents)
        }
    }

    fun handleResult(code: String) {
        on<InviteFeature>().useInviteCode(code) {
            on<MyProfileFeature>().reload()
            on<PeopleFeature>().reload()
            successCallback?.invoke()
        }
    }

    fun scan(successCallback: (() -> Unit)?) {
        this.successCallback = successCallback

        IntentIntegrator(on<ViewFeature>().activity).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
            setPrompt("Scan an invite QR Code")
            setBeepEnabled(false)
        }.initiateScan()
    }
}
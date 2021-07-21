package com.queatz.fantasydating.features

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.queatz.fantasydating.*
import com.queatz.on.On
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fullscreen_modal.*

class InviteFeature constructor(private val on: On) {
    fun generateInviteCode(success: (Bitmap) -> Unit) {
        on<Api>().createInviteCode { inviteCodeResult: InviteCode ->
            val s = on<ViewFeature>().with { resources.getDimensionPixelSize(R.dimen.qr_code) }
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(inviteCodeResult.code!!, BarcodeFormat.QR_CODE, s, s)
            success(bitmap)
        }
    }

    fun useInviteCode(code: String, success: () -> Unit) {
        on<Api>().useInviteCode(code) {
            if (it.success) {
                success()
            } else {
                on<Say>().say(it.message ?: on<ViewFeature>().with { getString(R.string.invite_code_didnt_work) })
            }
        } error {
            on<Say>().say(R.string.invite_code_didnt_work)
        }
    }

    fun showInviteCode() {
        on<ViewFeature>().with {
            on<LayoutFeature>().canCloseFullscreenModal = true
            fullscreenMessageText.text = "Please wait... <tap data=\"close\">Close</tap>"
            fullscreenMessageLayout.fadeIn()
            fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)

            generateInviteCode {
                fullscreenMessageText.text = "Scan this QR Code to join Qimates, or <tap data=\"close\">Close</tap>"
                fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    null, BitmapDrawable(resources, it), null, null
                )
            }

            fullscreenMessageText.onLinkClick = {
                fullscreenMessageLayout.fadeOut {
                    fullscreenMessageText.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                }
            }
        }
    }
}

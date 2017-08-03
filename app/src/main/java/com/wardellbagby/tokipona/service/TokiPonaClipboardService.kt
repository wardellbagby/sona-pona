package com.wardellbagby.tokipona.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import com.danialgoodwin.globaloverlay.GlobalOverlay
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.ui.activity.MainActivity
import com.wardellbagby.tokipona.util.IntentExtras
import com.wardellbagby.tokipona.util.TAG


class TokiPonaClipboardService : Service() {

    private var mGlossView: View? = null
    private var mGlobalOverlay: GlobalOverlay? = null

    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Binding to this Service is not supported.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (!canDrawOverlays(this)) {
            Log.i(TAG, "TokiPonaClipboardService is stopping because it does not have the permission to draw overlays.")
            stopSelf()
            return START_NOT_STICKY
        }

        if (mGlossView == null) {
            mGlobalOverlay = GlobalOverlay(this)
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.addPrimaryClipChangedListener({
                val clipData = clipboard.primaryClip
                if (clipData.itemCount > 0 && mGlossView == null) {
                    mGlossView = initGlossButton(applicationContext, mGlobalOverlay, clipData.getItemAt(0).text) {
                        mGlossView = null
                    }
                }
            })
        }
        return START_STICKY
    }

    @SuppressLint("InflateParams") // No way to inflate it here without null. There is no parent.
    private fun initGlossButton(context: Context,
                                globalOverlay: GlobalOverlay?,
                                copiedText: CharSequence,
                                onRemovedCallback: () -> Unit): View? {

        if (!canDrawOverlays(context)) {
            Log.i(TAG, "Stopping TokiPonaClipboardService because we have lost the permission to draw overlays.")
            stopSelf()
            return null
        }

        val glossView = LayoutInflater.from(context).inflate(R.layout.gloss_button_view, null)

        globalOverlay?.addOverlayView(glossView, {
            val intent = Intent(context, MainActivity::class.java)
            intent.putExtra(IntentExtras.COPIED_TEXT, copiedText)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            globalOverlay.removeOverlayView(glossView)
            onRemovedCallback()
        })

        return glossView
    }

    private fun canDrawOverlays(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (mGlossView != null) {
            mGlobalOverlay?.removeOverlayView(mGlossView)
        }
    }
}

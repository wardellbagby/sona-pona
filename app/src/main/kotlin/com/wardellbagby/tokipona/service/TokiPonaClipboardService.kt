package com.wardellbagby.tokipona.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.*
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
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel


class TokiPonaClipboardService : Service() {

    companion object {
        val CUTOFF_SENTENCE_PROBABILITY = .7f
    }

    private var mGlossView: View? = null
    private var mGlobalOverlay: GlobalOverlay? = null
    private var mSentenceModel: SentenceModel? = null

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

        if (mSentenceModel == null) {
            val modelStream = assets.open("en-sent.bin")
            mSentenceModel = SentenceModel(modelStream)
        }

        if (mGlossView == null) {
            mGlobalOverlay = GlobalOverlay(this)
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboard.addPrimaryClipChangedListener({
                val clipData = clipboard.primaryClip
                if (isClipDataText(clipData)) {
                    val text = clipData.getItemAt(0).coerceToText(this).toString()
                    if (isTextLikelyEnglish(text)) {
                        mGlossView = initGlossButton(applicationContext, mGlobalOverlay, getGlossIntent(this, text)) {
                            mGlossView = null
                        }
                    }
                }
            })
        }
        return START_STICKY
    }

    private fun isClipDataText(clipData: ClipData): Boolean {
        return clipData.itemCount > 0 && mGlossView == null && clipData.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
    }

    @SuppressLint("InflateParams") // No way to inflate it here without null. There is no parent.
    private fun initGlossButton(context: Context,
                                globalOverlay: GlobalOverlay?,
                                intent: Intent,
                                onRemovedCallback: () -> Unit): View? {

        if (!canDrawOverlays(context)) {
            Log.i(TAG, "Stopping TokiPonaClipboardService because we have lost the permission to draw overlays.")
            stopSelf()
            return null
        }

        val glossView = LayoutInflater.from(context).inflate(R.layout.gloss_button_view, null)

        globalOverlay?.addOverlayView(glossView, {
            startActivity(intent)
            globalOverlay.removeOverlayView(glossView)
            onRemovedCallback()
        })

        return glossView
    }

    private fun getGlossIntent(context: Context, glossableText: String): Intent {
        return Intent(context, MainActivity::class.java).apply {
            putExtra(IntentExtras.GLOSSABLE_TEXT, glossableText)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }

    private fun canDrawOverlays(context: Context): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)
    }

    private fun isTextLikelyEnglish(text: String): Boolean {
        val sentenceDetector = SentenceDetectorME(mSentenceModel)
        sentenceDetector.sentDetect(text) // Don't care about the result.
        val probability = sentenceDetector.sentenceProbabilities.average()
        Log.d(TAG, "Probability of {$probability} for copied text.")
        return probability > CUTOFF_SENTENCE_PROBABILITY
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mGlossView != null) {
            mGlobalOverlay?.removeOverlayView(mGlossView)
        }
    }
}

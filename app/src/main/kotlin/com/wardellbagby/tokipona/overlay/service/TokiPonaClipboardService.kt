package com.wardellbagby.tokipona.overlay.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.WindowManager
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.overlay.hover.ClipboardHoverMenu
import com.wardellbagby.tokipona.util.TAG
import com.wardellbagby.tokipona.util.Words
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.SideDock
import io.mattcarroll.hover.window.WindowViewController
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel

class TokiPonaClipboardService : Service() {

    companion object {
        val CUTOFF_SENTENCE_PROBABILITY = .7f
    }

    private var mHoverView: HoverView? = null
    private var mClipboardHoverMenu: ClipboardHoverMenu? = null
    private var mSentenceModel: SentenceModel? = null
    private var mIsBound = false

    override fun onBind(intent: Intent): IBinder? {
        mIsBound = true
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        mIsBound = false
        return super.onUnbind(intent)
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
        mIsBound = true
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

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener({
            val clipData = clipboard.primaryClip
            if (!mIsBound && isClipDataText(clipData)) {
                val text = clipData.getItemAt(0).coerceToText(this).toString()
                if (isTextLikelyEnglish(text)) {
                    showHoverView(applicationContext, text)
                }
            }
        })

        return START_STICKY
    }

    private fun isClipDataText(clipData: ClipData): Boolean {
        return clipData.itemCount > 0 && clipData.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
    }

    @SuppressLint("InflateParams") // No way to inflate it here without null. There is no parent.
    private fun showHoverView(context: Context, text: String) {

        if (!canDrawOverlays(context)) {
            Log.i(TAG, "Stopping TokiPonaClipboardService because we have lost the permission to draw overlays.")
            stopSelf()
            return
        }

        if (mHoverView == null) {
            mHoverView = HoverView.createForWindow(
                    this,
                    WindowViewController((getSystemService(Context.WINDOW_SERVICE) as WindowManager)),
                    SideDock.SidePosition(SideDock.SidePosition.RIGHT, 0.3f))
            mClipboardHoverMenu = ClipboardHoverMenu(ContextThemeWrapper(this, R.style.AppTheme_NoActionBar)) {
                mHoverView?.release()
                mHoverView?.removeFromWindow()
                mHoverView = null
            }
        }
        mHoverView?.addToWindow()
        Words.getWords(this) {
            Words.glossToText(text, it) {
                mClipboardHoverMenu?.setText(it)
            }
        }

        mHoverView?.setMenu(mClipboardHoverMenu)
        mHoverView?.collapse()
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
        mHoverView?.release()
        mHoverView?.removeFromWindow()
    }
}

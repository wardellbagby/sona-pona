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
import com.wardellbagby.tokipona.TokiPonaApplication
import com.wardellbagby.tokipona.overlay.hover.ClipboardHoverMenu
import com.wardellbagby.tokipona.util.Preferences
import com.wardellbagby.tokipona.util.TAG
import com.wardellbagby.tokipona.util.Words
import io.mattcarroll.hover.HoverView
import io.mattcarroll.hover.SideDock
import io.mattcarroll.hover.window.WindowViewController
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import opennlp.tools.sentdetect.SentenceDetectorME
import opennlp.tools.sentdetect.SentenceModel
import javax.inject.Inject

/**
 * A service that monitors for changes in the clipboard and shows a floating button if the clipboard
 * contains text that could be parsed as Toki Pona.
 *
 * Uses the OpenNLP (https://opennlp.apache.org/) library to determine if text is sentence-y or not.
 * This all happens on-device; no network required.
 */
class TokiPonaClipboardService : Service() {

    companion object {
        const val CUTOFF_SENTENCE_PROBABILITY = .7f
    }

    @Inject lateinit var preferences: Preferences
    private var hoverView: HoverView? = null
    private var clipboardHoverMenu: ClipboardHoverMenu? = null
    private var sentenceModel: SentenceModel? = null
    private var isBound = false

    override fun onBind(intent: Intent): IBinder? {
        isBound = true
        return null
    }

    override fun onUnbind(intent: Intent?): Boolean {
        isBound = false
        return true
    }

    override fun onRebind(intent: Intent?) {
        isBound = true
    }

    override fun onCreate() {
        super.onCreate()
        TokiPonaApplication.appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        Log.d(TAG, "TokiPonaClipboardService has started.")

        if (!preferences.isClipboardServiceEnabled) {
            Log.i(TAG, "TokiPonaClipboardService is stopping because it has been marked as disabled.")
            stopSelf()
        }

        if (!canDrawOverlays(this)) {
            Log.i(TAG, "TokiPonaClipboardService is stopping because it does not have the permission to draw overlays.")
            stopSelf()
            return START_NOT_STICKY
        }

        if (sentenceModel == null) {
            val modelStream = assets.open("en-sent.bin")
            sentenceModel = SentenceModel(modelStream)
        }

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.addPrimaryClipChangedListener({
            val clipData = clipboard.primaryClip
            if (!isBound && isClipDataText(clipData) && !isOurClipData(clipData)) {
                val text = clipData.getItemAt(0).coerceToText(this).toString()
                if (isTextLikelyEnglish(text)) {
                    showHoverView(applicationContext, text)
                }
            }
        })

        return START_STICKY
    }

    private fun isClipDataText(clipData: ClipData): Boolean = clipData.itemCount > 0 && clipData.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)

    private fun isOurClipData(clipData: ClipData): Boolean = clipData.description.label == getString(R.string.app_name)

    @SuppressLint("InflateParams") // No way to inflate it here without null. There is no parent.
    private fun showHoverView(context: Context, text: String) {
        if (!canDrawOverlays(context) || !preferences.isClipboardServiceEnabled) {
            Log.i(TAG, "Stopping TokiPonaClipboardService because the user has told us not to run.")
            stopSelf()
            return
        }

        if (hoverView == null) {
            hoverView = HoverView.createForWindow(
                    this,
                    WindowViewController((getSystemService(Context.WINDOW_SERVICE) as WindowManager)),
                    SideDock.SidePosition(SideDock.SidePosition.RIGHT, 0.3f))
            clipboardHoverMenu = ClipboardHoverMenu(ContextThemeWrapper(this, R.style.AppTheme_NoActionBar)) {
                hoverView?.release()
                hoverView?.removeFromWindow()
                hoverView = null
            }
        }
        hoverView?.addToWindow()
        Words.getWords(this)
                .flatMap { Words.glossToText(text, it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it ->
                    clipboardHoverMenu?.setText(it)
                }

        hoverView?.setMenu(clipboardHoverMenu)
        hoverView?.collapse()
    }

    private fun canDrawOverlays(context: Context): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)

    private fun isTextLikelyEnglish(text: String): Boolean {
        val sentenceDetector = SentenceDetectorME(sentenceModel)
        sentenceDetector.sentDetect(text) // Don't care about the result.
        val probability = sentenceDetector.sentenceProbabilities.average()
        Log.d(TAG, "Probability of {$probability} for copied text.")
        return probability > CUTOFF_SENTENCE_PROBABILITY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "TokiPonaClipboardService has stopped.")
        hoverView?.release()
        hoverView?.removeFromWindow()
    }
}

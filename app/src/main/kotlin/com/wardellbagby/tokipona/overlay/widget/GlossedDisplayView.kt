package com.wardellbagby.tokipona.overlay.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.annotation.AnimRes
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageButton
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import com.wardellbagby.tokipona.R

/**
 * @author Wardell Bagby
 */
class GlossedDisplayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    CardView(context, attrs, defStyleAttr) {

    private val mFactory = ViewSwitcher.ViewFactory { LayoutInflater.from(context).inflate(R.layout.glossed_text_view, mGlossedText, false) as TextView }

    private var mGlossedText: TextSwitcher
    private var mCopyButton: ImageButton
    private var mShareButton: ImageButton

    init {
        LayoutInflater.from(context).inflate(R.layout.glossed_text_display, this, true)
        mGlossedText = findViewById<TextSwitcher>(R.id.textSwitcher)
        mCopyButton = findViewById<ImageButton>(R.id.copy_button)
        mShareButton = findViewById<ImageButton>(R.id.share_button)
        mGlossedText.setFactory(mFactory)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        setOnCopyClickListener {
            val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.primaryClip = ClipData.newPlainText(context.getString(R.string.app_name), it)
        }
        setOnShareClickListener {
            val sendIntent = Intent(Intent.ACTION_SEND).putExtra(Intent.EXTRA_TEXT, it)
            sendIntent.type = "text/plain"
            context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.share)))
        }
    }

    fun setTextAnimations(context: Context, @AnimRes inAnimRes: Int, @AnimRes outAnimRes: Int) {
        mGlossedText.setInAnimation(context, inAnimRes)
        mGlossedText.setOutAnimation(context, outAnimRes)
    }

    fun setGlossedText(text: CharSequence, animate: Boolean = true) {
        if (animate) {
            mGlossedText.setText(text)
        } else {
            mGlossedText.setCurrentText(text)
        }
    }

    fun getGlossedText(): CharSequence {
        return (mGlossedText.currentView as? TextView)?.text ?: ""
    }

    fun setOnCopyClickListener(callback: (CharSequence) -> Unit) {
        mCopyButton.setOnClickListener {
            callback(getGlossedText())
        }
    }

    fun setOnShareClickListener(callback: (CharSequence) -> Unit) {
        mShareButton.setOnClickListener {
            callback(getGlossedText())
        }
    }

    fun setSharePaneVisibility(visibility: Int) {
        mShareButton.visibility = visibility
        mCopyButton.visibility = visibility
    }
}
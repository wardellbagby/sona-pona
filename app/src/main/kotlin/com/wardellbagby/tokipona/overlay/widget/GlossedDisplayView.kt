package com.wardellbagby.tokipona.overlay.widget

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.annotation.AnimRes
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.ViewSwitcher
import com.wardellbagby.tokipona.R
import kotlinx.android.synthetic.main.glossed_text_display.view.*

/**
 * @author Wardell Bagby
 */
class GlossedDisplayView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        CardView(context, attrs, defStyleAttr) {

    private val mFactory = ViewSwitcher.ViewFactory { LayoutInflater.from(context).inflate(R.layout.default_autosize_text_view, textSwitcher, false) as TextView }

    init {
        LayoutInflater.from(context).inflate(R.layout.glossed_text_display, this, true)
        textSwitcher.setFactory(mFactory)
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
        textSwitcher.setInAnimation(context, inAnimRes)
        textSwitcher.setOutAnimation(context, outAnimRes)
    }

    fun setGlossedText(text: CharSequence, animate: Boolean = true) {
        if (animate) {
            textSwitcher.setText(text)
        } else {
            textSwitcher.setCurrentText(text)
        }
    }

    fun getGlossedText(): CharSequence {
        return (textSwitcher.currentView as? TextView)?.text ?: ""
    }

    private fun setOnCopyClickListener(callback: (CharSequence) -> Unit) {
        copy_button.setOnClickListener {
            callback(getGlossedText())
        }
    }

    private fun setOnShareClickListener(callback: (CharSequence) -> Unit) {
        share_button.setOnClickListener {
            callback(getGlossedText())
        }
    }

    fun setSharePaneVisibility(visibility: Int) {
        share_button.visibility = visibility
        copy_button.visibility = visibility
    }
}
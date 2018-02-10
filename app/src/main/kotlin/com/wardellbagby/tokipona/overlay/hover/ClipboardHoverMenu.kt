package com.wardellbagby.tokipona.overlay.hover

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.skyfishjy.library.RippleBackground
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.overlay.widget.GlossedDisplayView
import io.mattcarroll.hover.Content
import io.mattcarroll.hover.HoverMenu

/**
 * @author Wardell Bagby
 */
class ClipboardHoverMenu(private val context: Context, private val onHiddenCallback: () -> Unit) : HoverMenu() {

    private var section: Section
    private var stopAnimation: Runnable? = null
    @SuppressLint("InflateParams") // No parent to pass in
    private var rootView = LayoutInflater.from(context).inflate(R.layout.gloss_button_content_view, null, false)
    private var glossedDisplayView: GlossedDisplayView = rootView.findViewById(R.id.glossed_display_view)

    init {
        section = Section(SectionId("1"),
                getTabView(),
                getSectionContent())
    }

    private fun getSectionContent(): Content {
        return ClipboardHoverContent()
    }

    @SuppressLint("InflateParams") // No parent to pass in.
    private fun getTabView(): View {
        val tabView = LayoutInflater.from(context).inflate(R.layout.gloss_button_view, null, false) as RippleBackground
        tabView.startRippleAnimation()
        stopAnimation = object : Runnable {
            override fun run() {
                tabView.stopRippleAnimation()
                tabView.removeCallbacks(this)
            }
        }
        tabView.postDelayed(stopAnimation, 30000)
        return tabView
    }

    fun setText(text: String) {
        glossedDisplayView.setGlossedText(text)
        glossedDisplayView.setSharePaneVisibility(View.VISIBLE)
    }

    override fun getSections(): MutableList<Section> {
        return mutableListOf(section)
    }

    override fun getId(): String = "Toki Pona Button!"

    override fun getSection(index: Int): Section? {
        if (index == 0) {
            return section
        }
        return null
    }

    override fun getSection(sectionId: SectionId): Section? {
        if (sectionId == section.id) {
            return section
        }
        return null
    }

    override fun getSectionCount(): Int = 1

    inner class ClipboardHoverContent : Content {
        override fun onShown() {
            stopAnimation?.run()
        }

        override fun getView(): View = rootView

        override fun onHidden() {
            onHiddenCallback()
        }

        override fun isFullscreen(): Boolean = true
    }
}
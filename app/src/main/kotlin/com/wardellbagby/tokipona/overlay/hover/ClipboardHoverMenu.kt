package com.wardellbagby.tokipona.overlay.hover

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.skyfishjy.library.RippleBackground
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.overlay.widget.GlossedDisplayView
import io.mattcarroll.hover.Content
import io.mattcarroll.hover.HoverMenu

/**
 * @author Wardell Bagby
 */
class ClipboardHoverMenu(val context: Context, val onHiddenCallback: () -> Unit) : HoverMenu() {

    private var mSection: Section
    private var mStopAnimation: Runnable? = null
    private var mGlossedDisplayView: GlossedDisplayView? = GlossedDisplayView(context)
    private var mView: View? = null

    init {
        mSection = Section(SectionId("1"),
                getTabView(),
                getSectionContent())
    }

    private fun getSectionContent(): Content {
        return object : Content {
            override fun onShown() {
                mStopAnimation?.run()
            }

            override fun getView(): View {
                if (mView != null) {
                    return mView ?: View(context)
                }
                //This should never ever happen, but that doesn't mean it won't.
                if (mGlossedDisplayView?.parent != null) {
                    val text = mGlossedDisplayView?.getGlossedText() ?: ""
                    mGlossedDisplayView = GlossedDisplayView(context)
                    mGlossedDisplayView?.setGlossedText(text)
                }

                val margin = context.resources.getDimensionPixelSize(R.dimen.glossed_display_view_margin)
                mGlossedDisplayView?.setSharePaneVisibility(View.VISIBLE)

                mView = FrameLayout(context).apply {
                    layoutParams = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.MATCH_PARENT, ViewGroup.MarginLayoutParams.MATCH_PARENT).apply {
                        setMargins(margin, margin, margin, margin)
                    }
                    addView(mGlossedDisplayView ?: View(context))
                }
                return mView ?: View(context)
            }

            override fun onHidden() {
                onHiddenCallback()
            }

            override fun isFullscreen(): Boolean = true
        }
    }

    @SuppressLint("InflateParams") // No parent to pass in.
    private fun getTabView(): View {
        val tabView = LayoutInflater.from(context).inflate(R.layout.gloss_button_view, null, false) as RippleBackground?
        tabView?.startRippleAnimation()
        mStopAnimation = object : Runnable {
            override fun run() {
                tabView?.stopRippleAnimation()
                tabView?.removeCallbacks(this)
            }
        }
        tabView?.postDelayed(mStopAnimation, 30000)
        return tabView ?: View(context)
    }

    fun setText(text: String) {
        mGlossedDisplayView?.setGlossedText(text)
    }

    override fun getSections(): MutableList<Section> {
        return mutableListOf(mSection)
    }

    override fun getId(): String {
        return "Toki Pona Button!"
    }

    override fun getSection(index: Int): Section? {
        if (index == 0) {
            return mSection
        }
        return null
    }

    override fun getSection(sectionId: SectionId): Section? {
        if (sectionId == mSection.id) {
            return mSection
        }
        return null
    }

    override fun getSectionCount(): Int {
        return 1
    }

}
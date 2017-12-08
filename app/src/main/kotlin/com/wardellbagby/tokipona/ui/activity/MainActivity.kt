package com.wardellbagby.tokipona.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.TokiPonaApplication
import com.wardellbagby.tokipona.overlay.service.TokiPonaClipboardService
import com.wardellbagby.tokipona.ui.fragment.DefinitionsFragment
import com.wardellbagby.tokipona.ui.fragment.GlossFragment
import com.wardellbagby.tokipona.ui.fragment.QuizFragment
import com.wardellbagby.tokipona.util.Preferences
import com.wardellbagby.tokipona.util.getLastBackStackEntry
import io.reactivex.Flowable
import io.reactivex.processors.AsyncProcessor
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

/**
 * The main entry point into the app. This Activity handles the bottom navigation bar and swapping
 * between the 3 main fragments.
 *
 * @author Wardell Bagby
 */
class MainActivity : BaseActivity<MainActivity.MainEvent>() {

    open class MainEvent : BaseActivity.BaseEvent()
    data class GlossEvent(val glossableText: String) : MainEvent()

    @Inject lateinit var preferences: Preferences

    private lateinit var eventProcessor: AsyncProcessor<MainEvent>

    private var requestedOverlayPermission by state(false)
    private var ignoreItemSelected = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TokiPonaApplication.appComponent.inject(this)

        eventProcessor = AsyncProcessor.create()

        navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)
        fragment_view_pager.adapter = fragmentPagerAdapter
        fragment_view_pager.addOnPageChangeListener(onPageChangeListener)

        supportFragmentManager.addOnBackStackChangedListener {
            val currentSelectedId = navigation.selectedItemId
            val newSelectedId = supportFragmentManager
                    .getLastBackStackEntry()
                    ?.name
                    ?.toIntOrNull() ?: BottomNavigationView.NO_ID
            if (currentSelectedId != newSelectedId) {
                updateNavigationBarState(newSelectedId)
            }
        }
    }

    private fun updateNavigationBarState(selectedId: Int) {
        ignoreItemSelected = true
        navigation.selectedItemId = selectedId
        ignoreItemSelected = false
    }

    override fun onPostResume() {
        super.onPostResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                preferences.shouldShowOverlayPermission && !requestedOverlayPermission
                        && !Settings.canDrawOverlays(this) -> {
                    requestedOverlayPermission = true
                    showOverlayPermissionDialog()
                }
                requestedOverlayPermission && Settings.canDrawOverlays(this) -> startService(Intent(this, TokiPonaClipboardService::class.java))
            }
        }

        if (isIntentForGloss(intent)) {
            handleGlossIntent(intent)
            intent = null
        }
    }

    private fun isIntentForGloss(intent: Intent?): Boolean =
            intent != null && intent.hasExtra(Intent.EXTRA_TEXT)

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showOverlayPermissionDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.overlay_permission_title)
                .setMessage(R.string.overlay_permission_explanation)
                .setPositiveButton(android.R.string.yes, { _: DialogInterface, _: Int ->
                    startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + packageName)))
                })
                .setNegativeButton(android.R.string.no, { _: DialogInterface, _: Int -> })
                .setNeutralButton(R.string.never, { _: DialogInterface, _: Int ->
                    preferences.shouldShowOverlayPermission = false
                })
                .setCancelable(false)
                .show()
    }

    private fun handleGlossIntent(intent: Intent) {
        val copiedText = intent.extras.getString(Intent.EXTRA_TEXT)
        if (copiedText != null) {
            navigation.selectedItemId = R.id.navigation_gloss
            eventProcessor.onNext(GlossEvent(copiedText))
            eventProcessor.onComplete()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (isIntentForGloss(intent)) {
            intent?.let(this::handleGlossIntent)
            setIntent(null)
        }
    }

    override fun getEvents(): Flowable<MainEvent> = eventProcessor

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (ignoreItemSelected || item.itemId == navigation.selectedItemId) {
            return@OnNavigationItemSelectedListener true
        }

        fragment_view_pager.currentItem = when (item.itemId) {
            R.id.navigation_dictionary -> 0
            R.id.navigation_gloss -> 1
            R.id.navigation_quiz -> 2
            else -> 0
        }

        true
    }

    private val fragmentPagerAdapter = object : FragmentPagerAdapter(supportFragmentManager) {
        override fun getItem(position: Int): Fragment = when (position) {
            0 -> DefinitionsFragment()
            1 -> GlossFragment()
            2 -> QuizFragment()
            else -> Fragment()
        }

        override fun getCount(): Int = 3
    }

    private val onPageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            navigation.selectedItemId = when (position) {
                0 -> R.id.navigation_dictionary
                1 -> R.id.navigation_gloss
                2 -> R.id.navigation_quiz
                else -> -1
            }
        }
    }
}

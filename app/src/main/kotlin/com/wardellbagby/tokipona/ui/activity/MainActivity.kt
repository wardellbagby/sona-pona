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
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.TokiPonaApplication
import com.wardellbagby.tokipona.overlay.service.TokiPonaClipboardService
import com.wardellbagby.tokipona.ui.fragment.DefinitionsFragment
import com.wardellbagby.tokipona.ui.fragment.GlossFragment
import com.wardellbagby.tokipona.util.IntentExtras
import com.wardellbagby.tokipona.util.Preferences
import com.wardellbagby.tokipona.util.getLastBackStackEntry
import io.reactivex.processors.AsyncProcessor
import io.reactivex.processors.FlowableProcessor
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

    @Inject lateinit var mPreferences: Preferences

    private lateinit var mEventProcessor: AsyncProcessor<MainEvent>

    private var mNavigationView: BottomNavigationView? = null
    private var mRequestedOverlayPermission by state(false)

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        val fragment: Fragment = supportFragmentManager.findFragmentByTag(item.itemId.toString()) ?:
                when (item.itemId) {
                    R.id.navigation_dictionary -> DefinitionsFragment()
                    R.id.navigation_gloss -> GlossFragment()
                    else -> Fragment()
                }

        replace(R.id.frameLayout, fragment, item.itemId.toString())
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        TokiPonaApplication.appComponent.inject(this)

        mEventProcessor = AsyncProcessor.create()

        mNavigationView = findViewById<BottomNavigationView>(R.id.navigation)
        mNavigationView?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, DefinitionsFragment(), R.id.navigation_dictionary.toString())
                    .commit()
        }
        /*todo This isn't working correctly. There is some backstack logic that is getting out of
        hand and should be simplified, if possible. Specifically around the child fragment logic
        in DefinitionsFragment. It might be a good idea to have BaseFragment force subclasses to
        override a function that will force all fragments to report an ID that will respond to their
        most parent fragment? Although since the DefinitionsFragment is using childFragments, the
        issue might be more along the lines of this function just having an odd corner case that
        I'm missing.
         */
        supportFragmentManager.addOnBackStackChangedListener {
            val currentSelectedId = mNavigationView?.selectedItemId
            val newSelectedId = supportFragmentManager
                    .getLastBackStackEntry()
                    ?.name
                    ?.toIntOrNull() ?: BottomNavigationView.NO_ID
            if (currentSelectedId != newSelectedId) {
                mNavigationView?.selectedItemId = newSelectedId
            }
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mPreferences.shouldShowOverlayPermission() && !mRequestedOverlayPermission
                    && !Settings.canDrawOverlays(this)) {
                mRequestedOverlayPermission = true
                showOverlayPermissionDialog()
            } else if (mRequestedOverlayPermission && Settings.canDrawOverlays(this)) {
                startService(Intent(this, TokiPonaClipboardService::class.java))
            } else if (isIntentForGloss(intent)) {
                handleGlossIntent(intent)
                intent = null
            }
        }
    }

    private fun isIntentForGloss(intent: Intent?): Boolean {
        return intent != null && intent.hasExtra(IntentExtras.GLOSSABLE_TEXT)
    }

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
                    mPreferences.setShouldShowOverlayPermission(false)
                })
                .setCancelable(false)
                .show()
    }

    private fun handleGlossIntent(intent: Intent) {
        val copiedText = intent.extras.getString(IntentExtras.GLOSSABLE_TEXT)
        if (copiedText != null) {
            mNavigationView?.selectedItemId = R.id.navigation_gloss
            mEventProcessor.onNext(GlossEvent(copiedText))
            mEventProcessor.onComplete()
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (isIntentForGloss(intent)) {
            mEventProcessor = AsyncProcessor.create()
            intent?.let(this::handleGlossIntent)
            setIntent(null)
        }
    }

    override fun getProcessor(): FlowableProcessor<MainEvent>? {
        return mEventProcessor
    }
}

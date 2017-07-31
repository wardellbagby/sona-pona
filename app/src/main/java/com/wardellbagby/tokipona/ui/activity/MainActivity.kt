package com.wardellbagby.tokipona.ui.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.ui.fragment.GlossFragment
import com.wardellbagby.tokipona.ui.fragment.WordListFragment
import com.wardellbagby.tokipona.util.getLastBackStackEntry
import com.wardellbagby.tokipona.util.isLastBackEntry

/**
 * The main entry point into the app. This Activity handles the bottom navigation bar and swapping
 * between the 3 main fragments.
 *
 * @author Wardell Bagby
 */
class MainActivity : BaseActivity() {

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if (supportFragmentManager.isLastBackEntry(item.itemId.toString())) {
            return@OnNavigationItemSelectedListener true
        }
        if (supportFragmentManager.popBackStackImmediate(item.itemId.toString(), 0)) {
            return@OnNavigationItemSelectedListener true
        }
        var fragment: Fragment? = supportFragmentManager.findFragmentByTag(item.itemId.toString())
        if (fragment == null) {
            when (item.itemId) {
                R.id.navigation_dictionary -> {
                    fragment = WordListFragment()
                }
                R.id.navigation_gloss -> {
                    fragment = GlossFragment()
                }
                else -> {
                    fragment = Fragment()
                }
            }
        }
        supportFragmentManager.beginTransaction()
                .addToBackStack(item.itemId.toString())
                .replace(R.id.frameLayout, fragment, item.itemId.toString())
                .commit()
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navigation = findViewById<BottomNavigationView>(R.id.navigation)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.frameLayout, WordListFragment(), R.id.navigation_dictionary.toString())
                    .commit()
        }
        supportFragmentManager.addOnBackStackChangedListener {
            val currentSelectedId = navigation.selectedItemId
            val newSelectedId = supportFragmentManager
                    .getLastBackStackEntry()
                    ?.name
                    ?.toIntOrNull() ?: BottomNavigationView.NO_ID
            if (currentSelectedId != newSelectedId) {
                navigation.selectedItemId = newSelectedId
            }
        }
    }
}

package com.wardellbagby.tokipona.ui.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.util.sendOnBackPressed

/**
 * @author Wardell Bagby
 */
@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity(), Pikkel by PikkelDelegate() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState)
    }

    override fun onBackPressed() {
        if (!supportFragmentManager.sendOnBackPressed()) {
            super.onBackPressed()
        }
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        findViewById<Toolbar?>(R.id.toolbar)?.title = title
    }

    override fun setTitle(titleId: Int) {
        super.setTitle(titleId)
        findViewById<Toolbar?>(R.id.toolbar)?.setTitle(titleId)
    }
}
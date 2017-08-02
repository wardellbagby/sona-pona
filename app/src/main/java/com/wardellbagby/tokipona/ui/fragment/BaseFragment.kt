package com.wardellbagby.tokipona.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.util.sendOnBackPressed

/**
 * @author Wardell Bagby
 */
open class BaseFragment : Fragment(), Pikkel by PikkelDelegate() {

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        restoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState)
    }

    open fun onBackPressed(): Boolean {
        return childFragmentManager.sendOnBackPressed()
    }

    open fun getTitle(): CharSequence? {
        return getString(R.string.app_name)
    }

    open fun setTitle(title: CharSequence?) {
        activity?.title = title
    }

    override fun onResume() {
        super.onResume()
        setTitle(getTitle())
    }
}
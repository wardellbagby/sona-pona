package com.wardellbagby.tokipona.ui.fragment

import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.View
import com.github.yamamotoj.pikkel.Pikkel
import com.github.yamamotoj.pikkel.PikkelDelegate
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.util.Fragments
import com.wardellbagby.tokipona.util.sendOnBackPressed
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * @author Wardell Bagby
 */
open class BaseFragment : Fragment(), Pikkel by PikkelDelegate() {

    private var mDisposables: CompositeDisposable? = null

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        mDisposables = CompositeDisposable()
        restoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        saveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        setTitle(getTitle())
    }

    override fun onPause() {
        super.onPause()
        if (mDisposables?.isDisposed == false) {
            mDisposables?.dispose()
        }
    }

    open fun onBackPressed(): Boolean {
        return childFragmentManager.sendOnBackPressed()
    }

    /**
     * Returns the title for this fragment. This will be set on the attached
     * [com.wardellbagby.tokipona.ui.activity.BaseActivity] in [onResume].
     */
    open fun getTitle(): CharSequence? {
        return getString(R.string.app_name)
    }

    open fun setTitle(title: CharSequence?) {
        activity?.title = title
    }

    /**
     * Returns a list of supported transition names to be used for animating transitions between
     * fragments.
     */
    open fun getSupportedTransitionNames(): List<String> {
        return listOf()
    }

    fun disposeOnPause(disposable: Disposable) {
        mDisposables?.add(disposable)
    }

    fun replace(@IdRes id: Int, fragment: Fragment, tag: String) {
        Fragments.replace(childFragmentManager, id, fragment, tag)
    }

    /**
     * Returns a list of Views that should be excluded from transition animations.
     */
    open fun getTargetsToExcludeFromTransitions(): List<View> {
        return listOf()
    }
}
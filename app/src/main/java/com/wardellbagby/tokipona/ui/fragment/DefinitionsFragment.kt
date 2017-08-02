package com.wardellbagby.tokipona.ui.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wardellbagby.tokipona.R
import com.wardellbagby.tokipona.util.findFragmentByClass
import com.wardellbagby.tokipona.util.isTagInBackstack

/**
 * @author Wardell Bagby
 */
class DefinitionsFragment : BaseFragment() {

    private var mTwoPane = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_definitions, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        mTwoPane = resources.getBoolean(R.bool.is_two_pane)
        var listFragment: WordListFragment? = childFragmentManager.findFragmentByClass(WordListFragment::class.java)
        if (listFragment == null && !fragmentManager.isTagInBackstack(R.id.navigation_dictionary.toString())) {
            listFragment = WordListFragment()
            childFragmentManager.beginTransaction()
                    .replace(R.id.word_detail_container, listFragment, R.id.navigation_dictionary.toString())
                    .addToBackStack(R.id.navigation_dictionary.toString())
                    .commit()
        }
        listFragment?.setOnWordSelectedCallback {

            var fragment: Fragment? = childFragmentManager.findFragmentByTag(it.name)
            if (fragment == null) {
                fragment = WordDetailsFragment()
                fragment.arguments = Bundle().apply { putParcelable(WordDetailsFragment.Companion.WORD, it) }
            }

            childFragmentManager.beginTransaction()
                    .replace(R.id.word_detail_container, fragment, it.name)
                    .addToBackStack(it.name)
                    .commit()
            mTwoPane // We only need to show an item as selected on two panes, where the user can see the list and details.
        }
    }

    override fun onBackPressed(): Boolean {
        return childFragmentManager.popBackStackImmediate(R.id.navigation_dictionary.toString(), 0)
                || super.onBackPressed()
    }
}

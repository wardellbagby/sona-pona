package com.wardellbagby.tokipona.ui.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wardellbagby.tokipona.R

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
        var listFragment: WordListFragment? = childFragmentManager.findFragmentByTag(getString(R.string.fragment_word_list)) as WordListFragment?
        if (listFragment == null) {
            listFragment = WordListFragment()
            replace(R.id.word_detail_container, listFragment, getString(R.string.fragment_word_list))
        }
        listFragment.setOnWordSelectedCallback {
            var fragment: Fragment? = childFragmentManager.findFragmentByTag(it.name)
            if (fragment == null) {
                fragment = WordDetailsFragment()
                fragment.arguments = Bundle().apply { putParcelable(WordDetailsFragment.Companion.WORD, it) }
            }
            replace(R.id.word_detail_container, fragment, it.name)
            mTwoPane // We only need to show an item as selected on two panes, where the user can see the list and details.
        }
    }

    override fun onBackPressed(): Boolean {
        val superResult = super.onBackPressed()
        if (superResult) {
            return true
        }
        if (!childFragmentManager.popBackStackImmediate(getString(R.string.fragment_word_list), 0)) {
            activity.finish()
        }
        return true
    }

    override fun getSupportedTransitionNames(): List<String> {
        return listOf(R.string.transition_name_main_content).map(this::getString)
    }
}

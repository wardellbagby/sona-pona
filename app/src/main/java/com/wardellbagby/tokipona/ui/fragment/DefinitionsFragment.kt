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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_definitions, container, false)
    }

    override fun onViewCreated(rootView: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(rootView, savedInstanceState)
        var listFragment: WordListFragment? = childFragmentManager.findFragmentByClass(WordListFragment::class.java)
        if (listFragment == null && !fragmentManager.isTagInBackstack(R.id.navigation_dictionary.toString())) {
            listFragment = WordListFragment()
            childFragmentManager.beginTransaction()
                    .replace(R.id.word_detail_container, listFragment, R.id.navigation_dictionary.toString())
                    .addToBackStack(R.id.navigation_dictionary.toString())
                    .commit()
        }
        listFragment?.setOnWordClickedListener {

            var fragment: Fragment? = childFragmentManager.findFragmentByTag(it.name)
            if (fragment == null) {
                fragment = WordDetailsFragment()
                fragment.arguments = Bundle().apply { putParcelable(WordDetailsFragment.Companion.WORD, it) }
            }

            childFragmentManager.beginTransaction()
                    .replace(R.id.word_detail_container, fragment, it.name)
                    .addToBackStack(it.name)
                    .commit()
        }
    }
}

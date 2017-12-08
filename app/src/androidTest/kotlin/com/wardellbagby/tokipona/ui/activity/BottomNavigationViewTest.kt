package com.wardellbagby.tokipona.ui.activity

import android.annotation.SuppressLint
import android.support.design.internal.BottomNavigationItemView
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.Espresso.pressBack
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withClassName
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.view.View
import android.view.ViewGroup
import com.wardellbagby.tokipona.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BottomNavigationViewTest {

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun bottomNavigationViewTest_swap_fragments() {
        val appCompatButton = onView(
                allOf(withId(android.R.id.button3), withText("Never"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(`is`("android.widget.ScrollView")),
                                        0),
                                0),
                        isDisplayed()))
        appCompatButton.check { view, noViewFoundException ->
            if (view != null && noViewFoundException == null) {
                view.performClick()
            }
        }

        val bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_dictionary),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                0),
                        isDisplayed()))
        bottomNavigationItemView.perform(click())

        val buttonDictionary = onView(
                allOf(withId(R.id.navigation_dictionary), isDisplayed()))
        buttonDictionary.check(matches(withBottomNavItemCheckedStatus(true)))

        val bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_gloss),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                1),
                        isDisplayed()))
        bottomNavigationItemView2.perform(click())

        val buttonGloss = onView(
                allOf(withId(R.id.navigation_gloss), isDisplayed()))
        buttonGloss.check(matches(withBottomNavItemCheckedStatus(true)))

        val bottomNavigationItemView3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.navigation),
                                0),
                        2),
                        isDisplayed()))
        bottomNavigationItemView3.perform(click())

        val buttonQuiz = onView(
                allOf(withId(R.id.navigation_quiz), isDisplayed()))
        buttonQuiz.check(matches(withBottomNavItemCheckedStatus(true)))

        pressBack()
        buttonGloss.check(matches(withBottomNavItemCheckedStatus(true)))
        pressBack()
        buttonDictionary.check(matches(withBottomNavItemCheckedStatus(true)))

    }

    companion object {

        private fun childAtPosition(
                parentMatcher: Matcher<View>, position: Int): Matcher<View> {

            return object : TypeSafeMatcher<View>() {
                override fun describeTo(description: Description) {
                    description.appendText("Child at position $position in parent ")
                    parentMatcher.describeTo(description)
                }

                public override fun matchesSafely(view: View): Boolean {
                    val parent = view.parent
                    return parent is ViewGroup && parentMatcher.matches(parent)
                            && view == parent.getChildAt(position)
                }
            }
        }

        fun withBottomNavItemCheckedStatus(isChecked: Boolean): Matcher<View> {
            return object : BoundedMatcher<View, BottomNavigationItemView>(BottomNavigationItemView::class.java) {
                internal var triedMatching: Boolean = false

                override fun describeTo(description: Description) {
                    if (triedMatching) {
                        description.appendText("with BottomNavigationItem check status: " + isChecked.toString())
                        description.appendText("But was: " + (!isChecked).toString())
                    }
                }

                @SuppressLint("RestrictedApi") // We gon' do what we want!
                override fun matchesSafely(item: BottomNavigationItemView): Boolean {
                    triedMatching = true
                    return item.itemData.isChecked == isChecked
                }
            }
        }
    }
}

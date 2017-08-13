package com.wardellbagby.tokipona.ui.activity;


import android.annotation.SuppressLint;
import android.support.design.internal.BottomNavigationItemView;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.wardellbagby.tokipona.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BottomNavigationViewTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void bottomNavigationViewTest_swap_fragments() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(android.R.id.button3), withText("Never"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                0),
                        isDisplayed()));
        appCompatButton.check((view, noViewFoundException) -> {
            if (view != null && noViewFoundException == null) {
                view.performClick();
            }
        });

        ViewInteraction bottomNavigationItemView = onView(
                allOf(withId(R.id.navigation_dictionary),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                0),
                        isDisplayed()));
        bottomNavigationItemView.perform(click());

        ViewInteraction buttonDictionary = onView(
                allOf(withId(R.id.navigation_dictionary), isDisplayed()));
        buttonDictionary.check(matches(withBottomNavItemCheckedStatus(true)));

        ViewInteraction bottomNavigationItemView2 = onView(
                allOf(withId(R.id.navigation_gloss),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.navigation),
                                        0),
                                1),
                        isDisplayed()));
        bottomNavigationItemView2.perform(click());

        ViewInteraction buttonGloss = onView(
                allOf(withId(R.id.navigation_gloss), isDisplayed()));
        buttonGloss.check(matches(withBottomNavItemCheckedStatus(true)));

        ViewInteraction bottomNavigationItemView3 = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.navigation),
                                0),
                        2),
                        isDisplayed()));
        bottomNavigationItemView3.perform(click());

        ViewInteraction buttonQuiz = onView(
                allOf(withId(R.id.navigation_quiz), isDisplayed()));
        buttonQuiz.check(matches(withBottomNavItemCheckedStatus(true)));

        pressBack();
        buttonGloss.check(matches(withBottomNavItemCheckedStatus(true)));
        pressBack();
        buttonDictionary.check(matches(withBottomNavItemCheckedStatus(true)));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    public static Matcher<View> withBottomNavItemCheckedStatus(final boolean isChecked) {
        return new BoundedMatcher<View, BottomNavigationItemView>(BottomNavigationItemView.class) {
            boolean triedMatching;

            @Override
            public void describeTo(Description description) {
                if (triedMatching) {
                    description.appendText("with BottomNavigationItem check status: " + String.valueOf(isChecked));
                    description.appendText("But was: " + String.valueOf(!isChecked));
                }
            }

            @SuppressLint("RestrictedApi") // We gon' do what we want!
            @Override
            protected boolean matchesSafely(BottomNavigationItemView item) {
                triedMatching = true;
                return item.getItemData().isChecked() == isChecked;
            }
        };
    }
}

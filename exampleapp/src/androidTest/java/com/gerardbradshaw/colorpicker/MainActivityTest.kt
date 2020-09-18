package com.gerardbradshaw.colorpicker


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @Rule
  @JvmField
  val activityScenarioRule = ActivityScenarioRule<MainActivity>(MainActivity::class.java)

  @Test
  fun mainActivityTest() {
    val appCompatButton = onView(allOf(withId(R.id.compact_example_button), withText("Compact Color Picker"), isDisplayed()))
    appCompatButton.perform(click())

    val textView = onView(allOf(withId(R.id.ex_compact_listener), isDisplayed()))
    textView.check(matches(withText("I'm listening for color changes!")))
  }
}

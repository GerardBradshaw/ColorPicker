package com.gerardbradshaw.colorpicker.compactview

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.gerardbradshaw.colorpicker.CompactViewActivity
import com.gerardbradshaw.colorpicker.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CompactViewActivityTest {

  @Rule
  @JvmField
  val activityScenarioRule = ActivityScenarioRule<CompactViewActivity>(
    CompactViewActivity::class.java)

  @Test
  fun useAppContext() {
    val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    assertEquals("com.gerardbradshaw.colorpicker", appContext.packageName)
  }

  @Test
  fun testActivityLaunch() {
    onView(withId(R.id.ex_compact_listener)).check(matches(isDisplayed()))
  }
}
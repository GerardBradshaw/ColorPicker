package com.gerardbradshaw.colorpicker.compactview

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf

object CompactViewAndroidTestUtil {
  fun changeSliderTypeTo(slider: SliderType, menuFrameResId: Int) {
    onView(allOf(withId(menuFrameResId), isDisplayed()))
      .perform(click())

    onView(withText(slider.value)).inRoot(isPlatformPopup())
      .perform(click())
  }

  enum class SliderType(val value: String) {
    COLOR("Color"),
    SHADE("Shade"),
    TINT("Tint")
  }
}
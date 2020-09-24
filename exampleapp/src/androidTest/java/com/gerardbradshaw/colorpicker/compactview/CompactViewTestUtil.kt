package com.gerardbradshaw.colorpicker.compactview

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matchers

object CompactViewTestUtil {
  fun changeSliderTypeTo(slider: SliderType, menuFrameResId: Int) {
    Espresso.onView(Matchers.allOf(ViewMatchers.withId(menuFrameResId), ViewMatchers.isDisplayed()))
      .perform(ViewActions.click())

    Espresso.onView(ViewMatchers.withText(slider.value)).inRoot(RootMatchers.isPlatformPopup())
      .perform(ViewActions.click())
  }

  enum class SliderType(val value: String) {
    COLOR("Color"),
    SHADE("Shade"),
    TINT("Tint")
  }
}
package com.gerardbradshaw.exampleapp.compactview

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView
import com.gerardbradshaw.exampleapp.R
import org.hamcrest.Matchers.allOf

object CompactViewAndroidTestUtil {
  fun changeSliderTypeTo(slider: ColorSliderView.SliderType) {
    onView(allOf(withId(R.id.color_picker_library_compact_menu_frame), isDisplayed()))
      .perform(click())

    onView(withText(slider.value)).inRoot(isPlatformPopup())
      .perform(click())
  }
}
package com.gerardbradshaw.exampleapp.compactview

import android.os.SystemClock
import android.view.MotionEvent
import android.view.View
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import com.gerardbradshaw.colorpickerlibrary.util.ColorSliderView
import com.gerardbradshaw.colorpickerlibrary.views.CompactColorPickerView
import com.gerardbradshaw.exampleapp.R
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf

object CompactViewAndroidTestUtil {
  fun changeSliderTypeTo(slider: ColorSliderView.SliderType) {
    onView(allOf(withId(R.id.color_picker_library_compact_menu_frame), isDisplayed()))
      .perform(click())

    onView(withText(slider.value)).inRoot(isPlatformPopup())
      .perform(click())
  }

  fun setPickerRatios(colorRatio: Double, shadeRatio: Double, tintRatio: Double) {
    onView(withId(R.id.example_compact_picker))
      .perform(updateRatios(colorRatio, shadeRatio, tintRatio))
  }

  private fun updateRatios(colorRatio: Double, shadeRatio: Double, tintRatio: Double): ViewAction? {
    return object : ViewAction {
      override fun getConstraints(): Matcher<View> {
        return isDisplayed()
      }

      override fun getDescription(): String {
        return "update ratios"
      }

      override fun perform(uiController: UiController, view: View) {
        val picker = view as CompactColorPickerView

        picker.colorRatio = colorRatio
        picker.shadeRatio = shadeRatio
        picker.tintRatio = tintRatio
      }
    }
  }

}
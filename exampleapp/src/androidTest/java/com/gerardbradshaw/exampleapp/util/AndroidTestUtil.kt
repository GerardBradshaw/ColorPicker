package com.gerardbradshaw.exampleapp.util

import android.graphics.Color
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.util.HumanReadables
import androidx.test.internal.util.Checks
import com.gerardbradshaw.exampleapp.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object AndroidTestUtil {
  private const val TAG = "AndroidTestUtil"


  // ---------------- SEEK BAR ----------------

  fun moveSeekBarTo(progress: Int) {
    onView(allOf(withId(R.id.color_picker_library_slider_seek_bar), isDisplayed()))
      .perform(setSeekBarProgressAction(progress))
  }

  fun checkSeekBarIsAtProgress(progress: Int) {
    onView(allOf(withId(R.id.color_picker_library_slider_seek_bar), isDisplayed()))
      .check(matches(isSeekBarAtProgress(progress)))
  }

  private fun setSeekBarProgressAction(progress: Int): ViewAction? {
    return object : ViewAction {
      override fun getDescription(): String {
        return "set a progress on a SeekBar"
      }

      override fun getConstraints(): Matcher<View> {
        return ViewMatchers.isAssignableFrom(SeekBar::class.java)
      }

      override fun perform(uiController: UiController?, view: View?) {
        if (view is SeekBar) view.progress = progress
      }
    }
  }

  private fun isSeekBarAtProgress(progress: Int): Matcher<View?>? {
    return object : BoundedMatcher<View?, SeekBar>(SeekBar::class.java) {
      override fun matchesSafely(view: SeekBar): Boolean {
        return view.progress == progress
      }

      override fun describeTo(description: Description) {
        description.appendText("matches expected position")
      }
    }
  }



  // ---------------- COLOR TAGS ----------------

  fun checkViewColorTagIsExactly(color: Int, resId: Int) {
    onView(allOf(withId(resId), isDisplayed()))
      .check(matches(hasApproximateColorTag(color)))
  }

  fun checkViewColorTagIsApprox(color: Int, resId: Int) {
    onView(allOf(withId(resId), isDisplayed()))
      .check(matches(hasExactColorTag(color)))
  }

  private fun hasExactColorTag(expectedColor: Int): Matcher<View?>? {
    Checks.checkNotNull(expectedColor)

    return object : BoundedMatcher<View?, View>(View::class.java) {
      override fun matchesSafely(view: View): Boolean {
        return isExactlyMatchingExpectedColor(
          expectedColor,
          view.getTag(R.id.color_picker_library_color_tag) as Int)
      }

      override fun describeTo(description: Description) {
        description.appendText("has exact color tag")
      }
    }
  }

  private fun hasApproximateColorTag(expectedTag: Int): Matcher<View?>? {
    Checks.checkNotNull(expectedTag)

    return object : BoundedMatcher<View?, View>(View::class.java) {
      override fun matchesSafely(view: View): Boolean {
        return isApproximatelyMatchingExpectedColor(
          expectedTag,
          view.getTag(R.id.color_picker_library_color_tag) as Int)
      }

      override fun describeTo(description: Description) {
        description.appendText("has approximate color tag")
      }
    }
  }

  private fun isExactlyMatchingExpectedColor(expectedColor: Int, actualColor: Int): Boolean {
    val isMatch = actualColor == expectedColor

    val matchText =
      if (isMatch) "Exact match (both ${getHexString(actualColor)})"
      else "Not a match. (Actual ${getHexString(actualColor)}, expected ${getHexString(expectedColor)})"

    Log.d(TAG, "isExactMatch: $matchText")

    return isMatch
  }

  private fun isApproximatelyMatchingExpectedColor(expectedColor: Int, actualColor: Int): Boolean {
    val expectedRed = Color.red(expectedColor)
    val redBound = (max(expectedRed - 1,0)..min(expectedRed + 1, 255))
    val isRedMatch = Color.red(actualColor) in redBound

    val expectedGreen = Color.green(expectedColor)
    val greenBound = (max(expectedGreen - 1,0)..min(expectedGreen + 1, 255))
    val isGreenMatch = Color.green(actualColor) in greenBound

    val expectedBlue = Color.blue(expectedColor)
    val blueBound = (max(expectedBlue - 1,0)..min(expectedBlue + 1, 255))
    val isBlueMatch = Color.blue(actualColor) in blueBound

    val isMatch = isRedMatch && isGreenMatch && isBlueMatch

    val matchText =
      if (isMatch) "Exact match (both ${getHexString(actualColor)})"
      else "Not a match. (Actual ${getHexString(actualColor)}, expected ${getHexString(expectedColor)})"

    Log.d(TAG, "isApproxMatch: $matchText")

    return isMatch
  }



  // ---------------- OTHERS ----------------

  fun Double.format(digits: Int) = "%.${digits}f".format(this)

  fun getHexString(color: Int): String {
    return String.format("#%06X", 0xFFFFFF and color)
  }

  fun getParameterizedTestParams(): Collection<Array<Any>> {
    val sliderMax = 16777216

    val inputParams = Array<Any>(7) {
      val colorProgress = (it * sliderMax.toDouble() / 6.0).roundToInt()

      val shadeProgress = (sliderMax - colorProgress)

      val tintProgress =
        when (it) {
          0 -> 0
          6 -> ((sliderMax.toDouble() / 6.0)).roundToInt()
          else -> ((sliderMax.toDouble() / 6.0) + shadeProgress).roundToInt()
        }

      ParamTestInput(colorProgress, shadeProgress, tintProgress)
    }

    val pureColors: Array<Int> = arrayOf(
      -65536, -256, -16711936, -16711681, -16776961, -65281, -65535)

    val shadedColors: Array<Int> = arrayOf(
      -16777216, -13948160, -16755456, -16744320, -16777046, -2883372, -65535)

    val tintedColors: Array<Int> = arrayOf(
      -65536, -1, -2752555, -5570561, -8355585, -43521, -54485)

    val shadedAndTintedColors: Array<Int> = arrayOf(
      -16777216, -13948117, -12102329, -11173760, -11184726, -2865196, -54485)

    val expectedOutputs = Array<Any>(7) {
      ParamTestOutput(pureColors[it], shadedColors[it], tintedColors[it], shadedAndTintedColors[it])
    }

    return Array(7) {
      arrayOf(inputParams[it], expectedOutputs[it])
    }.asList()
  }
}
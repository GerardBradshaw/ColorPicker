package com.gerardbradshaw.colorpicker.util

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
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
import com.gerardbradshaw.colorpicker.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.*
import org.junit.Assert
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object AndroidTestUtil {
  const val sliderMax = 16777216
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

  private fun isExactlyMatchingExpectedColor(expectedColor: Int, actualColor: Int): Boolean {
    val isMatch = actualColor == expectedColor

    val matchText =
      if (isMatch) "Exact match (both ${getHexString(actualColor)})"
      else "Not a match. (Actual ${getHexString(actualColor)}, expected ${getHexString(expectedColor)})"

    Log.d(TAG, matchText)

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

    val matchText = if(isMatch) "Exact match" else "Not a match"

    Log.d(
      TAG, "$matchText. (Actual ${getHexString(
        actualColor
      )}, expected ${getHexString(
        expectedColor
      )})")

    return isMatch
  }

  private fun scrubSeekBarAction(progress: Int): ViewAction? {
    return ViewActions.actionWithAssertions(
      GeneralSwipeAction(
        Swipe.FAST,
        SeekBarThumbCoordinatesProvider(
          0
        ),
        SeekBarThumbCoordinatesProvider(
          progress
        ),
        Press.PINPOINT
      )
    )
  }


  // ---------------- PREVIEW ----------------

  fun checkPreviewChangedColorTo(color: Int, previewResId: Int) {
    onView(allOf(withId(previewResId), isDisplayed()))
      .check(matches(hasColorTag(color)))
  }

  private fun hasColorTag(expectedTag: Int): Matcher<View?>? {
    Checks.checkNotNull(expectedTag)

    return object : BoundedMatcher<View?, View>(View::class.java) {
      override fun matchesSafely(view: View): Boolean {
        return isExactlyMatchingExpectedColor(
          expectedTag,
          view.getTag(com.gerardbradshaw.colorpickerlibrary.R.id.color_picker_library_color_tag) as Int
        )
      }

      override fun describeTo(description: Description) {
        description.appendText("with view tag")
      }
    }
  }


  // ---------------- LISTENER ----------------

  fun checkListenerChangedColorTo(color: Int) {
    onView(allOf(withId(R.id.color_picker_library_example_listener), isDisplayed()))
      .check(matches(hasDrawableBackgroundColor(color)))
  }

  private fun hasDrawableBackgroundColor(expectedColor: Int): Matcher<View?>? {
    Checks.checkNotNull(expectedColor)

    return object : BoundedMatcher<View?, View>(View::class.java) {
      override fun matchesSafely(view: View): Boolean {
        return isExactlyMatchingExpectedColor(
          expectedColor, view.getTag(R.id.color_picker_library_color_tag) as Int)
      }

      override fun describeTo(description: Description) {
        description.appendText("with background color")
      }
    }
  }

  // TODO keep this one for robolectric
  fun checkListenerChangedColorTo(color: Int, listener: TextView) {
    val background = listener.background ?: Assert.fail("Listener has no background.")

    if (background !is ColorDrawable) Assert.fail("Listener background is not a color.")

    MatcherAssert.assertThat((background as ColorDrawable).color, equalTo(color))
  }



  // ---------------- OTHER UTILS ----------------

  fun getHexString(color: Int): String {
    return String.format("#%06X", 0xFFFFFF and color)
  }

  fun getParamaterizedTestParams(): Collection<Array<Any>> {
    val inputParams = Array<Any>(7) {
      val colorProgress = (it * sliderMax.toDouble() / 6.0).roundToInt()

      val shadeProgress = (sliderMax - colorProgress)

      val tintProgress =
        when (it) {
          0 -> 0
          6 -> ((sliderMax.toDouble() / 6.0)).roundToInt()
          else -> ((sliderMax.toDouble() / 6.0) + shadeProgress).roundToInt()
        }

      InputParams(colorProgress, shadeProgress, tintProgress)
    }

    val pureColors: Array<Int> = arrayOf(
      -65536, -256, -16711936, -16711681, -16776961, -65281, -65535)

    val shadedColors: Array<Int> = arrayOf(
      -16777216, - 13948160, - 16755456, - 16744320, - 16777046, - 2883372, - 65535)

    val tintedColors: Array<Int> = arrayOf(
      -65536, - 1, - 2752555, - 5570561, - 8355585, - 43521, - 54485)

    val shadedAndTintedColors: Array<Int> = arrayOf(
      -16777216, - 13948117, - 12102329, - 11173760, - 11184726, - 2865196, - 54485)

    val expectedOutputs = Array<Any>(7) {
      OutputColors(pureColors[it], shadedColors[it], tintedColors[it], shadedAndTintedColors[it])
    }

    return Array(7) {
      arrayOf(inputParams[it], expectedOutputs[it])
    }.asList()
  }


  class SeekBarThumbCoordinatesProvider(var progress: Int) : CoordinatesProvider {
    override fun calculateCoordinates(view: View): FloatArray {
      if (view !is SeekBar) {
        throw PerformException.Builder()
          .withViewDescription(HumanReadables.describe(view))
          .withCause(RuntimeException(String.format("SeekBar expected"))).build()
      }

      val width = view.width - view.paddingStart - view.paddingEnd
      val progress = if (progress == 0) view.progress else progress

      val xPosition = (view.paddingLeft + width.toDouble() * (progress.toDouble() / view.max.toDouble())).toFloat()
      val xy =
        getVisibleLeftTop(
          view
        )

      return floatArrayOf(xy[0] + xPosition, xy[1] + 10)
    }

    companion object {
      private fun getVisibleLeftTop(view: View): FloatArray {
        val xy = IntArray(2)
        view.getLocationOnScreen(xy)
        return floatArrayOf(xy[0].toFloat(), xy[1].toFloat())
      }
    }
  }
}
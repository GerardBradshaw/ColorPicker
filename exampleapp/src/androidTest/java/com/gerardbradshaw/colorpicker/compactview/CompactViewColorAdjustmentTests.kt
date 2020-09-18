package com.gerardbradshaw.colorpicker.compactview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.internal.util.Checks
import com.gerardbradshaw.colorpicker.CompactViewActivity
import com.gerardbradshaw.colorpicker.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


@RunWith(Parameterized::class)
class CompactViewColorAdjustmentTests(private val inputProgress: Int, private val expectedPreviewColor: Int) {

  @Rule
  @JvmField
  val asr = ActivityScenarioRule<CompactViewActivity>(
    CompactViewActivity::class.java)

  @Test
  fun should_updatePreview_when_colorSliderPositionChanged() {
    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .perform(setSeekBarProgress(inputProgress))

    onView(allOf(withId(R.id.compact_preview), isDisplayed()))
      .check(matches(isImageColorAsExpected(expectedPreviewColor)))
  }

  @Test
  fun should_notifyListener_when_colorSliderPositionChanged() {
    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .perform(setSeekBarProgress(inputProgress))

    onView(allOf(withId(R.id.ex_compact_listener), isDisplayed()))
      .check(matches(isBackgroundColorAsExpected(expectedPreviewColor)))
  }

  @Test
  fun should_startColorSliderAtPreviousProgress_when_returningFromShadeSlider() {
    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .perform(setSeekBarProgress(inputProgress))

    changeToSlider("Shade")
    changeToSlider("Color")

    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .check(matches(isSeekBarAtProgress(inputProgress)))
  }

  @Test
  fun should_startColorSliderAtPreviousProgress_when_returningFromTintSlider() {
    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .perform(setSeekBarProgress(inputProgress))

    changeToSlider("Tint")
    changeToSlider("Color")

    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .check(matches(isSeekBarAtProgress(inputProgress)))
  }

  @Test
  fun should_startColorSliderAtZeroProgress_when_firstEntering() {
    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .check(matches(isSeekBarAtProgress(0)))
  }


  // ---------------- SEEK BAR ----------------

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

  private fun setSeekBarProgress(progress: Int): ViewAction? {
    return object : ViewAction {
      override fun getDescription(): String {
        return "set a progress on a SeekBar"
      }

      override fun getConstraints(): Matcher<View> {
        return isAssignableFrom(SeekBar::class.java)
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

  private fun getSeekBarProgress(matcher: Matcher<View?>?): Int? {
    var result: Int? = null

    onView(matcher).perform(object : ViewAction {
      override fun getConstraints(): Matcher<View> {
        return isAssignableFrom(SeekBar::class.java)
      }

      override fun getDescription(): String {
        return "getting ratio from a SeekBar"
      }

      override fun perform(uiController: UiController?, view: View) {
        if (view is SeekBar) result = view.progress
      }
    })

    return result
  }



  // ---------------- IMAGE COLOR ----------------

  private fun isImageColorAsExpected(color: Int): Matcher<View?>? {
    Checks.checkNotNull(color)

    return object : BoundedMatcher<View?, ImageView>(ImageView::class.java) {
      override fun matchesSafely(view: ImageView): Boolean {
        return isExactlyMatchingExpectedColor(view.tag as Int)
      }

      override fun describeTo(description: Description) {
        description.appendText("with color filter color")
      }
    }
  }

  private fun isBackgroundColorAsExpected(color: Int): Matcher<View?>? {
    Checks.checkNotNull(color)

    return object : BoundedMatcher<View?, View>(View::class.java) {
      override fun matchesSafely(view: View): Boolean {
        val background = view.background
        val backgroundColor = if (background is ColorDrawable) background.color else null
        return if (backgroundColor != null) {
          isExactlyMatchingExpectedColor(backgroundColor)
        } else false
      }

      override fun describeTo(description: Description) {
        description.appendText("with background color")
      }
    }
  }



  // ---------------- UTIL ----------------

  private fun isApproximatelyMatchingExpectedColor(color: Int): Boolean {
    val expectedRed = Color.red(expectedPreviewColor)
    val redBound = (max(expectedRed - 1,0)..min(expectedRed + 1, 255))
    val isRedMatch = Color.red(color) in redBound

    val expectedGreen = Color.green(expectedPreviewColor)
    val greenBound = (max(expectedGreen - 1,0)..min(expectedGreen + 1, 255))
    val isGreenMatch = Color.green(color) in greenBound

    val expectedBlue = Color.blue(expectedPreviewColor)
    val blueBound = (max(expectedBlue - 1,0)..min(expectedBlue + 1, 255))
    val isBlueMatch = Color.blue(color) in blueBound

    val isMatch = isRedMatch && isGreenMatch && isBlueMatch

    Log.d(TAG, "$isMatch at $inputProgress. Actual/expected color: ${colorToString(color)}/${colorToString(expectedPreviewColor)}")

    return isMatch
  }

  private fun isExactlyMatchingExpectedColor(color: Int): Boolean {
    val isMatch = color == expectedPreviewColor

    val matchText = if(isMatch) "Exact match" else "Not a match"

    Log.d(
      TAG, "$matchText at $inputProgress. (Actual ${colorToString(color)}, " +
        "expected ${colorToString(expectedPreviewColor)})")

    return isMatch
  }

  private fun colorToString(color: Int): String {
    return String.format("#%06X", 0xFFFFFF and color)
  }

  private fun changeToSlider(sliderName: String) {
    onView(allOf(withId(R.id.compact_menu_frame), isDisplayed()))
      .perform(click())

    onView(withText(sliderName)).inRoot(isPlatformPopup())
      .perform(click())
  }




  // ---------------- PARAMETERS ----------------

  companion object {
    private const val TAG = "GGG"
    private const val sliderMax = 16777216

    @Parameterized.Parameters
    @JvmStatic
    fun data(): Collection<Array<Any>> {
      val progressInputs = Array<Any>(7) {
        (it * sliderMax.toDouble() / 6.0).roundToInt()
      }

      val expectedOutputs: Array<Any> = arrayOf(
        Color.argb(255, 255, 0, 0), // Red
        Color.argb(255, 255, 255, 0), // Yellow
        Color.argb(255, 0, 255, 0), // Green
        Color.argb(255, 0, 255, 255), // Light blue
        Color.argb(255, 0, 0, 255), // Dark blue
        Color.argb(255, 255, 0, 255), // Indigo
        Color.argb(255, 255, 0, 1)) // Almost red

      return Array(7) {
        arrayOf(progressInputs[it], expectedOutputs[it])
      }.asList()
    }
  }
}
package com.gerardbradshaw.colorpicker

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
class CompactViewTest(private val inputProgress: Int, private val expectedPureColor: Int) {

  @Rule
  @JvmField
  val asr = ActivityScenarioRule<CompactViewActivity>(CompactViewActivity::class.java)

  private val tintAndShadeProgress = sliderMax - inputProgress



  // ---------------- PREVIEW TESTS ----------------

  @Test
  fun should_updatePreview_when_onlyColorSliderProgressChanged() {
    moveSeekBarTo(inputProgress)
    checkPreviewChangedColorTo(expectedPureColor)
  }

  @Test
  fun should_updatePreview_when_colorAndShadeSliderProgressChanged() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress)

    checkPreviewChangedColorTo(getShadedColor())
  }

  @Test
  fun should_updatePreview_when_colorAndTintSliderProgressChanged() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress)

    checkPreviewChangedColorTo(getTintedColor())
  }

  @Test
  fun should_updatePreview_when_allSliderProgressChanged() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress)

    checkPreviewChangedColorTo(getTintedAndShadedColor())
  }



  // ---------------- LISTENER TESTS ----------------

  @Test
  fun should_notifyListener_when_allSliderProgressChanged() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress)

    checkListenerChangedColorTo(getTintedAndShadedColor())
  }

  @Test
  fun should_notifyListener_when_onlyColorSliderPositionChanged() {
    moveSeekBarTo(inputProgress)
    checkListenerChangedColorTo(expectedPureColor)
  }

  @Test
  fun should_notifyListener_when_colorAndShadeSliderProgressChanged() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress)

    checkListenerChangedColorTo(getShadedColor())
  }

  @Test
  fun should_notifyListener_when_colorAndTintSliderProgressChanged() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress)

    checkListenerChangedColorTo(getTintedColor())
  }



  // ---------------- SEEK BAR TESTS ----------------

  @Test
  fun should_startColorSliderAtPreviousProgress_when_returningFromShadeSlider() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress)

    changeSliderTypeTo(SliderType.COLOR)
    checkSeekBarIsAtProgress(inputProgress)
  }

  @Test
  fun should_startColorSliderAtPreviousProgress_when_returningFromTintSlider() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress)

    changeSliderTypeTo(SliderType.COLOR)
    checkSeekBarIsAtProgress(inputProgress)
  }

  @Test
  fun should_startColorSliderAtZeroProgress_when_firstEntering() {
    checkSeekBarIsAtProgress(0)
  }

  @Test
  fun should_startShadeSliderAtZeroProgress_when_firstEntering() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.SHADE)
    checkSeekBarIsAtProgress(0)
  }

  @Test
  fun should_startTintSliderAtZeroProgress_when_firstEntering() {
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(inputProgress)

    changeSliderTypeTo(SliderType.TINT)
    checkSeekBarIsAtProgress(0)
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

  private fun setSeekBarProgressAction(progress: Int): ViewAction? {
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

  private fun moveSeekBarTo(progress: Int) {
    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .perform(setSeekBarProgressAction(progress))
  }

  private fun checkPreviewChangedColorTo(color: Int) {
    onView(allOf(withId(R.id.compact_preview), isDisplayed()))
      .check(matches(isImageColorAsExpected(color)))
  }

  private fun checkListenerChangedColorTo(color: Int) {
    onView(allOf(withId(R.id.ex_compact_listener), isDisplayed()))
      .check(matches(isBackgroundColorAsExpected(color)))
  }

  private fun checkSeekBarIsAtProgress(progress: Int) {
    onView(allOf(withId(R.id.slider_seek_bar), isDisplayed()))
      .check(matches(isSeekBarAtProgress(progress)))
  }



  // ---------------- IMAGE COLOR ----------------

  private fun isImageColorAsExpected(expectedColor: Int): Matcher<View?>? {
    Checks.checkNotNull(expectedColor)

    return object : BoundedMatcher<View?, ImageView>(ImageView::class.java) {
      override fun matchesSafely(view: ImageView): Boolean {
        return isExactlyMatchingExpectedColor(expectedColor, view.tag as Int)
      }

      override fun describeTo(description: Description) {
        description.appendText("with color filter color")
      }
    }
  }

  private fun isBackgroundColorAsExpected(expectedColor: Int): Matcher<View?>? {
    Checks.checkNotNull(expectedColor)

    return object : BoundedMatcher<View?, View>(View::class.java) {
      override fun matchesSafely(view: View): Boolean {
        val background = view.background
        val backgroundColor = if (background is ColorDrawable) background.color else null
        return if (backgroundColor != null) {
          isExactlyMatchingExpectedColor(expectedColor, backgroundColor)
        } else false
      }

      override fun describeTo(description: Description) {
        description.appendText("with background color")
      }
    }
  }



  // ---------------- UTIL ----------------

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

    Log.d(TAG, "$isMatch at $inputProgress. Actual/expected color: ${colorToString(actualColor)}/${colorToString(expectedColor)}")

    return isMatch
  }

  private fun isExactlyMatchingExpectedColor(expectedColor: Int, actualColor: Int): Boolean {
    val isMatch = actualColor == expectedColor

    val matchText = if(isMatch) "Exact match" else "Not a match"

    Log.d(
      TAG, "$matchText at $inputProgress. (Actual ${colorToString(actualColor)}, " +
        "expected ${colorToString(expectedColor)})")

    return isMatch
  }

  private fun colorToString(color: Int): String {
    return String.format("#%06X", 0xFFFFFF and color)
  }

  private fun getShadedColor(color: Int = expectedPureColor): Int {
    val shadeFactor = 1.0 - (tintAndShadeProgress.toDouble() / sliderMax.toDouble())
    val red = (Color.red(color) * shadeFactor).roundToInt()
    val green = (Color.green(color) * shadeFactor).roundToInt()
    val blue = (Color.blue(color) * shadeFactor).roundToInt()

    return Color.argb(255, red, green, blue)
  }

  private fun getTintedColor(color: Int = expectedPureColor): Int {
    val tintRatio = tintAndShadeProgress.toDouble() / sliderMax.toDouble()
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)

    return when (max(red, max(green, blue))) {
      red -> {
        Color.argb(255,
          red,
          green + ((red - green).toFloat() * tintRatio).roundToInt(),
          blue + ((red - blue).toFloat() * tintRatio).roundToInt())
      }

      green -> {
        Color.argb(255,
          red + ((green - red).toFloat() * tintRatio).roundToInt(),
          green,
          blue + ((green - blue).toFloat() * tintRatio).roundToInt())
      }

      blue -> {
        Color.argb(255,
          red + ((blue - red).toFloat() * tintRatio).roundToInt(),
          green + ((blue - green).toFloat() * tintRatio).roundToInt(),
          blue)
      }

      else -> Color.argb(255, red, green, blue)
    }
  }

  private fun getTintedAndShadedColor(): Int {
    return getTintedColor(getShadedColor(expectedPureColor))
  }

  private fun changeSliderTypeTo(slider: SliderType) {
    onView(allOf(withId(R.id.compact_menu_frame), isDisplayed()))
      .perform(click())


    onView(withText(slider.value)).inRoot(isPlatformPopup())
      .perform(click())
  }

  enum class SliderType(val value: String) {
    COLOR("Color"),
    SHADE("Shade"),
    TINT("Tint")
  }

  companion object {
    private const val TAG = "GGG"
    private const val sliderMax = 16777216

    @Parameterized.Parameters
    @JvmStatic
    fun data(): Collection<Array<Any>> {
      val progressInputs = Array<Any>(7) {
        (it * sliderMax.toDouble() / 6.0).roundToInt()
      }

      val expectedPureColorOutputs: Array<Any> = arrayOf(
        Color.argb(255, 255, 0, 0),
        Color.argb(255, 255, 255, 0),
        Color.argb(255, 0, 255, 0),
        Color.argb(255, 0, 255, 255),
        Color.argb(255, 0, 0, 255),
        Color.argb(255, 255, 0, 255),
        Color.argb(255, 255, 0, 1))

      return Array(7) {
        arrayOf(progressInputs[it], expectedPureColorOutputs[it])
      }.asList()
    }
  }
}
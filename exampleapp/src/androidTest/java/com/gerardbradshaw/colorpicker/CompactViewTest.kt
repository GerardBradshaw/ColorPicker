package com.gerardbradshaw.colorpicker

import android.graphics.Color
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gerardbradshaw.colorpicker.TestUtil.checkListenerChangedColorTo
import com.gerardbradshaw.colorpicker.TestUtil.checkPreviewChangedColorTo
import com.gerardbradshaw.colorpicker.TestUtil.checkSeekBarIsAtProgress
import com.gerardbradshaw.colorpicker.TestUtil.moveSeekBarTo
import org.hamcrest.Matchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.max
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
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)
    checkPreviewChangedColorTo(expectedPureColor, R.id.compact_preview)
  }

  @Test
  fun should_updatePreview_when_colorAndShadeSliderProgressChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    checkPreviewChangedColorTo(getShadedColor(), R.id.compact_preview)
  }

  @Test
  fun should_updatePreview_when_colorAndTintSliderProgressChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    checkPreviewChangedColorTo(getTintedColor(), R.id.compact_preview)
  }

  @Test
  fun should_updatePreview_when_allSliderProgressChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    checkPreviewChangedColorTo(getTintedAndShadedColor(), R.id.compact_preview)
  }



  // ---------------- LISTENER TESTS ----------------

  @Test
  fun should_notifyListener_when_allSliderProgressChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    checkListenerChangedColorTo(getTintedAndShadedColor(), R.id.ex_compact_listener)
  }

  @Test
  fun should_notifyListener_when_onlyColorSliderPositionChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)
    checkListenerChangedColorTo(expectedPureColor, R.id.ex_compact_listener)
  }

  @Test
  fun should_notifyListener_when_colorAndShadeSliderProgressChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    checkListenerChangedColorTo(getShadedColor(), R.id.ex_compact_listener)
  }

  @Test
  fun should_notifyListener_when_colorAndTintSliderProgressChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    checkListenerChangedColorTo(getTintedColor(), R.id.ex_compact_listener)
  }



  // ---------------- SEEK BAR TESTS ----------------

  @Test
  fun should_startColorSliderAtPreviousProgress_when_returningFromShadeSlider() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.COLOR)
    checkSeekBarIsAtProgress(inputProgress, R.id.slider_seek_bar)
  }

  @Test
  fun should_startColorSliderAtPreviousProgress_when_returningFromTintSlider() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(tintAndShadeProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.COLOR)
    checkSeekBarIsAtProgress(inputProgress, R.id.slider_seek_bar)
  }

  @Test
  fun should_startColorSliderAtZeroProgress_when_firstEntering() {
    checkSeekBarIsAtProgress(0, R.id.slider_seek_bar)
  }

  @Test
  fun should_startShadeSliderAtZeroProgress_when_firstEntering() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.TINT)
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.SHADE)
    checkSeekBarIsAtProgress(0, R.id.slider_seek_bar)
  }

  @Test
  fun should_startTintSliderAtZeroProgress_when_firstEntering() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.SHADE)
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)

    changeSliderTypeTo(SliderType.TINT)
    checkSeekBarIsAtProgress(0, R.id.slider_seek_bar)
  }



  // ---------------- COLOR HELPERS ----------------

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
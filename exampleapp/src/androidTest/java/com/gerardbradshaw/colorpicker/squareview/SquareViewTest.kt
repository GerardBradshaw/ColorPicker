package com.gerardbradshaw.colorpicker.squareview


import android.graphics.Color
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gerardbradshaw.colorpicker.R
import com.gerardbradshaw.colorpicker.SquareViewActivity
import com.gerardbradshaw.colorpicker.TestUtil.checkPreviewChangedColorTo
import com.gerardbradshaw.colorpicker.TestUtil.moveSeekBarTo
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import kotlin.math.roundToInt

@RunWith(Parameterized::class)
class SquareViewTest(private val inputProgress: Int, private val expectedPureColor: Int) {

  @Rule
  @JvmField
  val asr = ActivityScenarioRule<SquareViewActivity>(SquareViewActivity::class.java)



  // ---------------- PREVIEW TESTS ----------------

  @Test
  fun should_updatePreview_when_colorSliderProgressChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)
    checkPreviewChangedColorTo(expectedPureColor, R.id.large_preview_new)
  }

  //@Test
  fun should_updatePreview_when_colorAndShadeProgressChanged() {
    moveSeekBarTo(inputProgress, R.id.slider_seek_bar)
    // TODO
  }



  // ---------------- UTIL ----------------


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
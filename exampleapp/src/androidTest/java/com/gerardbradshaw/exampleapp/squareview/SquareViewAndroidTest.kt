package com.gerardbradshaw.exampleapp.squareview


import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.gerardbradshaw.exampleapp.R
import com.gerardbradshaw.exampleapp.SquareViewActivity
import com.gerardbradshaw.exampleapp.util.AndroidTestUtil.checkPreviewChangedColorTo
import com.gerardbradshaw.exampleapp.util.AndroidTestUtil.moveSeekBarTo
import com.gerardbradshaw.exampleapp.squareview.SquareViewAndroidTestUtil.moveThumbTo
import com.gerardbradshaw.exampleapp.util.AndroidTestUtil.getParamaterizedTestParams
import com.gerardbradshaw.colorpickerlibrary.util.InputParams
import com.gerardbradshaw.colorpickerlibrary.util.OutputColors
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class SquareViewAndroidTest(private val input: InputParams, private val expected: OutputColors) {

  @Rule
  @JvmField
  val asr = ActivityScenarioRule<SquareViewActivity>(
    SquareViewActivity::class.java)



  // ---------------- PREVIEW TESTS ----------------

  @Test
  fun should_updatePreview_when_colorSliderProgressChanged() {
    moveSeekBarTo(input.colorProgress)
    checkPreviewChangedColorTo(expected.pureColor, R.id.color_picker_library_large_preview_new)
  }

  @Test
  fun should_updatePreview_when_colorAndShadeProgressChanged() {
    moveSeekBarTo(input.colorProgress)
    moveThumbTo(input.tintRatio, input.shadeRatio, R.id.color_picker_library_large_color_window)
    checkPreviewChangedColorTo(expected.shadedColor, R.id.color_picker_library_large_preview_new)
  }



  // ---------------- UTIL ----------------

  companion object {
    private const val TAG = "GGG"
    private const val sliderMax = 16777216

    @Parameterized.Parameters
    @JvmStatic
    fun data(): Collection<Array<Any>> {
      return getParamaterizedTestParams()
    }
  }
}
package com.gerardbradshaw.exampleapp.squareview

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.gerardbradshaw.exampleapp.R
import com.gerardbradshaw.exampleapp.SquareViewActivity
import com.gerardbradshaw.exampleapp.squareview.SquareViewAndroidTestUtil.checkThumbIsAtRatioPosition
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.checkViewColorTagIsExactly
import com.gerardbradshaw.exampleapp.util.EspressoTestUtil.moveSeekBarTo
import com.gerardbradshaw.exampleapp.squareview.SquareViewAndroidTestUtil.moveThumbTo
import com.gerardbradshaw.exampleapp.testutil.GlobalTestUtil.getParameterizedTestIO
import com.gerardbradshaw.exampleapp.testutil.ParamTestInput
import com.gerardbradshaw.exampleapp.testutil.ParamTestOutput
import org.junit.Rule
import org.junit.Test
import org.junit.experimental.runners.Enclosed
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Enclosed::class)
internal class SquareViewAndroidTest {

  // ---------------- SINGLE RUN TESTS ----------------

  @RunWith(AndroidJUnit4::class)
  class SingleTests {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<SquareViewActivity>(SquareViewActivity::class.java)

    @Test
    fun should_startThumbInTopRightCornerOfSquare_when_firstEntering() {
      checkThumbIsAtRatioPosition(0.0, 0.0)
    }
  }



  // ---------------- PARAMETERIZED TESTS ----------------

  @RunWith(Parameterized::class)
  class ParameterizedTests(private val input: ParamTestInput, private val expected: ParamTestOutput) {

    @Rule
    @JvmField
    val asr = ActivityScenarioRule<SquareViewActivity>(SquareViewActivity::class.java)

    @Test
    fun should_updatePreview_when_colorChanged() {
      moveSeekBarTo(input.colorProgress)
      checkViewColorTagIsExactly(expected.pureColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updateSquareColor_when_colorChanged() {
      moveSeekBarTo(input.colorProgress)
      checkViewColorTagIsExactly(expected.pureColor, R.id.color_picker_library_large_window)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeChanged() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(0.0, input.shadeRatio)
      checkViewColorTagIsExactly(expected.shadedColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorAndTintChanged() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(input.tintRatio, 0.0)
      checkViewColorTagIsExactly(expected.tintedColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_updatePreview_when_colorAndShadeAndTintChanged() {
      moveSeekBarTo(input.colorProgress)
      moveThumbTo(input.tintRatio, input.shadeRatio)
      checkViewColorTagIsExactly(expected.shadedAndTintedColor, R.id.color_picker_library_large_preview_new)
    }

    @Test
    fun should_moveThumbToTappedPosition_when_squareTapped() {
      moveThumbTo(input.tintRatio, input.shadeRatio)
      checkThumbIsAtRatioPosition(input.tintRatio, input.shadeRatio)
    }


    companion object {
      private const val TAG = "SquareViewAndroidTest"

      @Parameterized.Parameters(name = "{0}")
      @JvmStatic
      fun data(): Collection<Array<Any>> {
        return getParameterizedTestIO()
      }
    }
  }
}
